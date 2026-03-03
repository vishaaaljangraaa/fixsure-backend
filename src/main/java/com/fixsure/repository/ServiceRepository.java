package com.fixsure.repository;

import com.fixsure.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    List<Service> findByCategoryId(UUID categoryId);

    @Query("SELECT s FROM Service s WHERE " +
            "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR s.basePrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR s.basePrice <= :maxPrice) AND " +
            "(:minRating IS NULL OR s.rating >= :minRating)")
    List<Service> findByFilters(UUID categoryId,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            java.math.BigDecimal minRating);

    List<Service> findByNameContainingIgnoreCase(String name);
}
