package com.fixsure.controller;

import com.fixsure.dto.ApiResponse;
import com.fixsure.dto.CouponDto;
import com.fixsure.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Coupon management")
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    @Operation(summary = "Get all coupons")
    public ResponseEntity<ApiResponse<List<CouponDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(couponService.getAllCoupons()));
    }

    @PostMapping
    @Operation(summary = "Add a new coupon")
    public ResponseEntity<ApiResponse<CouponDto.Response>> addCoupon(@RequestBody CouponDto.Request request) {
        return ResponseEntity.ok(ApiResponse.ok(couponService.addCoupon(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a coupon")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable UUID id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
