package com.fixsure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AddressDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "Address line 1 is required")
        private String line1;

        private String line2;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "Pin code is required")
        @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid Indian pin code")
        private String pinCode;

        @NotNull
        private Boolean isDefault;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String pinCode;
        private Boolean isDefault;
    }
}
