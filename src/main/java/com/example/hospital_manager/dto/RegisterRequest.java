package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Size(min = 3, message = "Username must be at least 3 characters") String username,
        @NotBlank @Size(min = 4, message = "Password must be at least 4 characters") String password,
        @NotBlank(message = "fullName is required") String fullName,
        @NotNull(message = "role is required") Role role) {}