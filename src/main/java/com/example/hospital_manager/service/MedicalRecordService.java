package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MedicalRecordService {
    private final MedicalRecordRepository records;
    private final PatientRepository patients;
    private final UserRepository users;

    public MedicalRecordService(MedicalRecordRepository r, PatientRepository p, UserRepository u) {
        this.records = r; this.patients = p; this.users = u;
    }

    /** Doctor comes from the token (never from the request body). */
    public MedicalRecordResponse write(MedicalRecordRequest req, String doctorUsername) {
        Patient p = patients.findById(req.patientId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
        AppUser d = users.findByUsername(doctorUsername)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found"));
        MedicalRecord r = new MedicalRecord();
        r.setPatient(p); r.setDoctor(d); r.setDiagnosis(req.diagnosis()); r.setPrescription(req.prescription());
        return MedicalRecordResponse.from(records.save(r));
    }

    public List<MedicalRecordResponse> forPatient(Long patientId) {
        return records.findByPatientId(patientId).stream().map(MedicalRecordResponse::from).toList();
    }
}