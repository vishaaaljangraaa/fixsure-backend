package com.fixsure.controller;

import com.fixsure.dto.ApiResponse;
import com.fixsure.dto.BookingDto;
import com.fixsure.dto.SlotDto;
import com.fixsure.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management and slot availability")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/slots")
    @Operation(summary = "Get available time slots (optionally filtered by service and date)")
    public ResponseEntity<ApiResponse<List<SlotDto.Response>>> getSlots(
            @RequestParam(required = false) UUID serviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getAvailableSlots(serviceId, targetDate)));
    }

    @PostMapping("/bookings")
    @Operation(summary = "Create a new booking")
    public ResponseEntity<ApiResponse<BookingDto.Response>> createBooking(
            @Valid @RequestBody BookingDto.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Booking created successfully", bookingService.createBooking(req)));
    }

    @GetMapping("/bookings/{bookingId}")
    @Operation(summary = "Get a booking by ID")
    public ResponseEntity<ApiResponse<BookingDto.Response>> getBooking(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getBookingById(bookingId)));
    }

    @GetMapping("/bookings/user/{userId}")
    @Operation(summary = "Get all bookings for a user")
    public ResponseEntity<ApiResponse<List<BookingDto.Response>>> getUserBookings(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getBookingsByUser(userId)));
    }

    @PatchMapping("/bookings/{bookingId}/cancel")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<ApiResponse<BookingDto.Response>> cancelBooking(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled", bookingService.cancelBooking(bookingId)));
    }
}
