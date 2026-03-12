package com.fixsure.service;

import com.fixsure.dto.BookingDto;
import com.fixsure.dto.SlotDto;
import com.fixsure.entity.*;
import com.fixsure.entity.enums.BookingStatus;
import com.fixsure.entity.enums.PaymentMethod;
import com.fixsure.entity.enums.PaymentStatus;
import com.fixsure.exception.BadRequestException;
import com.fixsure.exception.ResourceNotFoundException;
import com.fixsure.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

        private final BookingRepository bookingRepository;
        private final UserRepository userRepository;
        private final ServiceRepository serviceRepository;
        private final TimeSlotRepository timeSlotRepository;
        private final AddressRepository addressRepository;
        private final TechnicianRepository technicianRepository;
        private final PaymentRepository paymentRepository;
        private final ServiceCatalogService serviceCatalogService;

        public List<SlotDto.Response> getAvailableSlots(UUID serviceId, LocalDate date) {
                // All slots are "available" for simplicity; in production this checks capacity
                return timeSlotRepository.findAll().stream()
                                .map(slot -> SlotDto.Response.builder()
                                                .id(slot.getId().toString())
                                                .label(slot.getLabel())
                                                .startTime(slot.getStartTime().toString())
                                                .endTime(slot.getEndTime().toString())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Transactional
        public BookingDto.Response createBooking(BookingDto.CreateRequest req) {
                User user = userRepository.findById(UUID.fromString(req.getUserId()))
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                com.fixsure.entity.Service service = serviceRepository.findById(UUID.fromString(req.getServiceId()))
                                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
                TimeSlot slot = timeSlotRepository.findById(UUID.fromString(req.getSlotId()))
                                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found"));
                Address address = addressRepository.findById(UUID.fromString(req.getAddressId()))
                                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

                // Validate address belongs to user
                if (!address.getUser().getId().equals(user.getId())) {
                        throw new BadRequestException("Address does not belong to the user");
                }

                // Validate Cash payment limit (₹5,000 max)
                BigDecimal totalAmount = calculateTotal(service.getBasePrice());
                if (req.getPaymentMethod() == PaymentMethod.CASH && totalAmount.compareTo(new BigDecimal("5000")) > 0) {
                        throw new BadRequestException("Cash payment is only available for bookings under ₹5,000");
                }

                // Try to auto-assign an available technician
                Technician technician = tryAssignTechnician(service.getCategory().getName());

                // Create booking
                Booking booking = Booking.builder()
                                .user(user)
                                .service(service)
                                .scheduledDate(req.getScheduledDate())
                                .slot(slot)
                                .address(address)
                                .technician(technician)
                                .totalAmount(totalAmount)
                                .notes(req.getNotes())
                                .status(BookingStatus.PENDING)
                                .build();
                booking = bookingRepository.save(booking);

                // Create associated payment record
                Payment payment = Payment.builder()
                                .booking(booking)
                                .method(req.getPaymentMethod())
                                .status(req.getPaymentMethod() == PaymentMethod.CASH ? PaymentStatus.PENDING
                                                : PaymentStatus.PENDING)
                                .amount(totalAmount)
                                .build();
                payment = paymentRepository.save(payment);
                booking.setPayment(payment);

                return toResponse(booking);
        }

        public BookingDto.Response getBookingById(UUID bookingId) {
                return toResponse(findBookingOrThrow(bookingId));
        }

        public List<BookingDto.Response> getBookingsByUser(UUID userId) {
                userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                                .stream().map(this::toResponse).collect(Collectors.toList());
        }

        @Transactional
        public BookingDto.Response cancelBooking(UUID bookingId) {
                Booking booking = findBookingOrThrow(bookingId);
                if (booking.getStatus() == BookingStatus.COMPLETED ||
                                booking.getStatus() == BookingStatus.CANCELLED) {
                        throw new BadRequestException("Cannot cancel a booking with status: " + booking.getStatus());
                }
                booking.setStatus(BookingStatus.CANCELLED);
                // Mark payment as refunded if it was successful
                if (booking.getPayment() != null &&
                                booking.getPayment().getStatus() == PaymentStatus.SUCCESS) {
                        booking.getPayment().setStatus(PaymentStatus.REFUNDED);
                        paymentRepository.save(booking.getPayment());
                }
                return toResponse(bookingRepository.save(booking));
        }

        // ---- Helpers ----

        /** Simple pricing: basePrice + 18% tax + ₹30 platform fee */
        private BigDecimal calculateTotal(BigDecimal basePrice) {
                BigDecimal tax = basePrice.multiply(new BigDecimal("0.18"));
                BigDecimal platformFee = new BigDecimal("30.00");
                return basePrice.add(tax).add(platformFee).setScale(2, java.math.RoundingMode.HALF_UP);
        }

        private Technician tryAssignTechnician(String categoryName) {
                List<Technician> matching = technicianRepository
                                .findBySpecializationsContainingIgnoreCaseAndIsAvailableTrue(categoryName);
                return matching.isEmpty() ? null : matching.get(0);
        }

        private Booking findBookingOrThrow(UUID bookingId) {
                return bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Booking not found with id: " + bookingId));
        }

        public BookingDto.Response toResponse(Booking booking) {
                com.fixsure.dto.AddressDto.Response addrDto = com.fixsure.dto.AddressDto.Response.builder()
                                .id(booking.getAddress().getId().toString())
                                .line1(booking.getAddress().getLine1())
                                .line2(booking.getAddress().getLine2())
                                .city(booking.getAddress().getCity())
                                .state(booking.getAddress().getState())
                                .pinCode(booking.getAddress().getPinCode())
                                .isDefault(booking.getAddress().getIsDefault())
                                .build();

                SlotDto.Response slotDto = SlotDto.Response.builder()
                                .id(booking.getSlot().getId().toString())
                                .label(booking.getSlot().getLabel())
                                .startTime(booking.getSlot().getStartTime().toString())
                                .endTime(booking.getSlot().getEndTime().toString())
                                .build();

                com.fixsure.dto.PaymentDto.Response paymentDto = null;
                if (booking.getPayment() != null) {
                        Payment p = booking.getPayment();
                        paymentDto = com.fixsure.dto.PaymentDto.Response.builder()
                                        .id(p.getId().toString())
                                        .method(p.getMethod().name())
                                        .status(p.getStatus().name())
                                        .gatewayOrderId(p.getGatewayOrderId())
                                        .gatewayPaymentId(p.getGatewayPaymentId())
                                        .amount(p.getAmount())
                                        .paidAt(p.getPaidAt() != null ? p.getPaidAt().toString() : null)
                                        .build();
                }

                return BookingDto.Response.builder()
                                .id(booking.getId().toString())
                                .userId(booking.getUser().getId().toString())
                                .service(serviceCatalogService.toSummary(booking.getService()))
                                .scheduledDate(booking.getScheduledDate().toString())
                                .slot(slotDto)
                                .address(addrDto)
                                .status(booking.getStatus().name())
                                .technicianName(booking.getTechnician() != null ? booking.getTechnician().getName()
                                                : null)
                                .technicianPhone(booking.getTechnician() != null ? booking.getTechnician().getPhone()
                                                : null)
                                .payment(paymentDto)
                                .totalAmount(booking.getTotalAmount())
                                .notes(booking.getNotes())
                                .createdAt(booking.getCreatedAt() != null ? booking.getCreatedAt().toString() : null)
                                .build();
        }
}
