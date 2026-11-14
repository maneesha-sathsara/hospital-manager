package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.MedicalRecord;
import java.time.LocalDateTime;

public record MedicalRecordResponse(Long id, Long patientId, String patientName, String doctorName,
                                    String diagnosis, String prescription, LocalDateTime createdAt) {
    public static MedicalRecordResponse from(MedicalRecord r) {
        return new MedicalRecordResponse(r.getId(), r.getPatient().getId(), r.getPatient().getName(),
                r.getDoctor().getFullName(), r.getDiagnosis(), r.getPrescription(), r.getCreatedAt());
    }
}