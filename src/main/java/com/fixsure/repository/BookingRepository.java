package com.fixsure.repository;

import com.fixsure.entity.Booking;
import com.fixsure.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUserId(UUID userId);
    List<Booking> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Booking> findByUserIdAndStatus(UUID userId, BookingStatus status);
    List<Booking> findByScheduledDateAndSlotId(LocalDate date, UUID slotId);
    long countByServiceIdAndScheduledDateAndSlotId(UUID serviceId, LocalDate date, UUID slotId);
}
