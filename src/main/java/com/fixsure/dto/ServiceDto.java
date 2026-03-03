package com.fixsure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class ServiceDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private String id;
        private String categoryId;
        private String categoryName;
        private String name;
        private String imageUrl;
        private BigDecimal basePrice;
        private Integer durationMinutes;
        private BigDecimal rating;
        private Integer reviewCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail {
        private String id;
        private String categoryId;
        private String categoryName;
        private String name;
        private String description;
        private String imageUrl;
        private BigDecimal basePrice;
        private Integer durationMinutes;
        private BigDecimal rating;
        private Integer reviewCount;
        private List<String> includes;
    }
}
