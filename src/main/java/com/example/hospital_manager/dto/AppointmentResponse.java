package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.*;
import java.time.LocalDateTime;

public record AppointmentResponse(Long id, Long patientId, String patientName,
                                  Long doctorId, String doctorName,
                                  LocalDateTime dateTime, String reason, AppointmentStatus status) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(a.getId(), a.getPatient().getId(), a.getPatient().getName(),
                a.getDoctor().getId(), a.getDoctor().getFullName(),
                a.getDateTime(), a.getReason(), a.getStatus());
    }
}
