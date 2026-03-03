package com.fixsure.service;

import com.fixsure.dto.PaymentDto;
import com.fixsure.entity.Booking;
import com.fixsure.entity.Payment;
import com.fixsure.entity.enums.BookingStatus;
import com.fixsure.entity.enums.PaymentStatus;
import com.fixsure.exception.BadRequestException;
import com.fixsure.exception.ResourceNotFoundException;
import com.fixsure.repository.BookingRepository;
import com.fixsure.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    /**
     * Creates or retrieves the gateway order for a booking.
     * For Cash payments, the booking is immediately confirmed.
     * For CARD/UPI (gateway), returns the gatewayOrderId to initiate Razorpay SDK
     * flow.
     */
    @Transactional
    public PaymentDto.Response createOrder(PaymentDto.CreateOrderRequest req) {
        UUID bookingId = UUID.fromString(req.getBookingId());
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for booking"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment already completed for this booking");
        }

        switch (req.getMethod()) {
            case CASH -> {
                // Cash on service — mark confirmed immediately
                payment.setStatus(PaymentStatus.PENDING);
                booking.setStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
            }
            case CARD, UPI -> {
                // Simulate gateway order creation (replace with Razorpay SDK in production)
                String simulatedOrderId = "order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                payment.setGatewayOrderId(simulatedOrderId);
            }
        }

        payment.setMethod(req.getMethod());
        payment = paymentRepository.save(payment);
        return toResponse(payment);
    }

    /**
     * Verifies gateway payment signature and marks payment as successful.
     * In production: verify HMAC-SHA256 with Razorpay secret.
     */
    @Transactional
    public PaymentDto.Response verifyPayment(PaymentDto.VerifyRequest req) {
        UUID bookingId = UUID.fromString(req.getBookingId());
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment already verified");
        }

        // In production: verify Razorpay HMAC-SHA256 signature here
        boolean signatureValid = simulateSignatureVerification(req);

        if (signatureValid) {
            payment.setGatewayOrderId(req.getGatewayOrderId());
            payment.setGatewayPaymentId(req.getGatewayPaymentId());
            payment.setGatewaySignature(req.getGatewaySignature());
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        payment = paymentRepository.save(payment);
        return toResponse(payment);
    }

    /**
     * Initiates a refund for a booking (e.g., on cancellation).
     * In production: call Razorpay Refund API.
     */
    @Transactional
    public PaymentDto.Response refundPayment(PaymentDto.RefundRequest req) {
        UUID bookingId = UUID.fromString(req.getBookingId());
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Only successful payments can be refunded");
        }

        // In production: call Razorpay refund API here
        payment.setStatus(PaymentStatus.REFUNDED);

        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return toResponse(paymentRepository.save(payment));
    }

    // ---- Helpers ----

    /**
     * Placeholder for Razorpay HMAC-SHA256 signature verification.
     * Always returns true in local/dev mode.
     */
    private boolean simulateSignatureVerification(PaymentDto.VerifyRequest req) {
        return req.getGatewayOrderId() != null && req.getGatewayPaymentId() != null
                && req.getGatewaySignature() != null;
    }

    public PaymentDto.Response toResponse(Payment payment) {
        return PaymentDto.Response.builder()
                .id(payment.getId().toString())
                .method(payment.getMethod().name())
                .status(payment.getStatus().name())
                .gatewayOrderId(payment.getGatewayOrderId())
                .gatewayPaymentId(payment.getGatewayPaymentId())
                .amount(payment.getAmount())
                .paidAt(payment.getPaidAt() != null ? payment.getPaidAt().toString() : null)
                .build();
    }
}
