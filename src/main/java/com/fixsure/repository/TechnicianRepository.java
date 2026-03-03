package com.fixsure.repository;

import com.fixsure.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, UUID> {
    List<Technician> findByIsAvailableTrue();

    List<Technician> findBySpecializationsContainingIgnoreCaseAndIsAvailableTrue(String specialization);
}
