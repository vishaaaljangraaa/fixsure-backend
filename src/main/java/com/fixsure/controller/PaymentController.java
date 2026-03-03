package com.fixsure.controller;

import com.fixsure.dto.ApiResponse;
import com.fixsure.dto.PaymentDto;
import com.fixsure.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment order creation, verification, and refunds")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    @Operation(summary = "Create a payment order (gateway order ID for CARD/UPI, or confirm CASH)")
    public ResponseEntity<ApiResponse<PaymentDto.Response>> createOrder(
            @Valid @RequestBody PaymentDto.CreateOrderRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Order created", paymentService.createOrder(req)));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify gateway payment signature and confirm booking")
    public ResponseEntity<ApiResponse<PaymentDto.Response>> verifyPayment(
            @Valid @RequestBody PaymentDto.VerifyRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Payment verified", paymentService.verifyPayment(req)));
    }

    @PostMapping("/refund")
    @Operation(summary = "Initiate a refund for a cancelled booking")
    public ResponseEntity<ApiResponse<PaymentDto.Response>> refundPayment(
            @Valid @RequestBody PaymentDto.RefundRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Refund initiated", paymentService.refundPayment(req)));
    }
}
