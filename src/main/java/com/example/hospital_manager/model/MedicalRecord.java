package com.example.hospital_manager.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MedicalRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Patient patient;

    @ManyToOne(optional = false)
    private AppUser doctor;

    @Column(nullable = false, length = 1000)
    private String diagnosis;

    @Column(length = 1000)
    private String prescription;

    private LocalDateTime createdAt = LocalDateTime.now();
}