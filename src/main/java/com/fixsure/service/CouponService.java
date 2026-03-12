package com.fixsure.service;

import com.fixsure.dto.CouponDto;
import com.fixsure.entity.Coupon;
import com.fixsure.exception.ResourceNotFoundException;
import com.fixsure.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponDto.Response addCoupon(CouponDto.Request request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .discountPercentage(request.getDiscountPercentage())
                .active(request.isActive())
                .expiryDate(request.getExpiryDate())
                .build();
        return toResponse(couponRepository.save(coupon));
    }

    public void deleteCoupon(UUID id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }

    public List<CouponDto.Response> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CouponDto.Response toResponse(Coupon coupon) {
        return CouponDto.Response.builder()
                .id(coupon.getId().toString())
                .code(coupon.getCode())
                .discountPercentage(coupon.getDiscountPercentage())
                .active(coupon.isActive())
                .expiryDate(coupon.getExpiryDate())
                .build();
    }
}
