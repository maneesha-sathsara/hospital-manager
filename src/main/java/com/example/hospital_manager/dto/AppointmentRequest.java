package com.example.hospital_manager.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull Long patientId,
        @NotNull Long doctorId,
        @NotNull @Future(message = "Appointment must be in the future") LocalDateTime dateTime,
        String reason) {}