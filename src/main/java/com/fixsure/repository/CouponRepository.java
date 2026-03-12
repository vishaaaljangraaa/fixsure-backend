package com.fixsure.repository;

import com.fixsure.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCode(String code);
}
