package com.example.hospital_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "patients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 150, message = "Age is too large")
    private int age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Pattern(regexp = "^[0-9+]{9,15}$", message = "Phone must be 9-15 digits")
    private String phone;

    @Email(message = "Invalid email")
    private String email;

    private String address;
}