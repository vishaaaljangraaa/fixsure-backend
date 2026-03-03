package com.fixsure.dto;

import com.fixsure.entity.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderRequest {
        @NotBlank(message = "Booking ID is required")
        private String bookingId;

        @NotNull(message = "Payment method is required")
        private PaymentMethod method;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyRequest {
        @NotBlank(message = "Booking ID is required")
        private String bookingId;

        @NotBlank(message = "Gateway order ID is required")
        private String gatewayOrderId;

        @NotBlank(message = "Gateway payment ID is required")
        private String gatewayPaymentId;

        @NotBlank(message = "Gateway signature is required")
        private String gatewaySignature;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundRequest {
        @NotBlank(message = "Booking ID is required")
        private String bookingId;

        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String method;
        private String status;
        private String gatewayOrderId;
        private String gatewayPaymentId;
        private BigDecimal amount;
        private String paidAt;
    }
}
