package com.fixsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "technicians")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    private String email;

    /**
     * Comma-separated list of service category names the technician is skilled in.
     * e.g. "AC,Plumbing"
     */
    @Column(columnDefinition = "TEXT")
    private String specializations;

    @Builder.Default
    private Boolean isAvailable = true;
}
