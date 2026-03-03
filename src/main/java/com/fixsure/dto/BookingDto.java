package com.fixsure.dto;

import com.fixsure.entity.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "User ID is required")
        private String userId;

        @NotNull(message = "Service ID is required")
        private String serviceId;

        @NotNull(message = "Slot ID is required")
        private String slotId;

        @NotNull(message = "Address ID is required")
        private String addressId;

        @NotNull(message = "Scheduled date is required")
        @FutureOrPresent(message = "Scheduled date must be today or in the future")
        private LocalDate scheduledDate;

        @NotNull(message = "Payment method is required")
        private PaymentMethod paymentMethod;

        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String userId;
        private ServiceDto.Summary service;
        private String scheduledDate;
        private SlotDto.Response slot;
        private AddressDto.Response address;
        private String status;
        private String technicianName;
        private String technicianPhone;
        private PaymentDto.Response payment;
        private BigDecimal totalAmount;
        private String notes;
        private String createdAt;
    }
}
