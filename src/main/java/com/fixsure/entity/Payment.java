package com.fixsure.entity;

import com.fixsure.entity.enums.PaymentMethod;
import com.fixsure.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    /** Gateway-assigned order ID (e.g., Razorpay order_id) */
    private String gatewayOrderId;

    /** Gateway-assigned payment ID after successful payment */
    private String gatewayPaymentId;

    /** Gateway signature for verification */
    private String gatewaySignature;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /** Timestamp when payment was successfully completed */
    private LocalDateTime paidAt;
}
