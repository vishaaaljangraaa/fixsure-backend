package com.fixsure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String code;
        private BigDecimal discountPercentage;
        private boolean active;
        private LocalDateTime expiryDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String code;
        private BigDecimal discountPercentage;
        private boolean active;
        private LocalDateTime expiryDate;
    }
}
