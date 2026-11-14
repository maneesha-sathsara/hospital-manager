package com.example.hospital_manager.service;

import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    private final MedicalRecordRepository records;

    public PatientService(PatientRepository p, AppointmentRepository a, MedicalRecordRepository r) {
        this.patients = p; this.appointments = a; this.records = r;
    }

    public List<Patient> list(String search) {
        return (search == null || search.isBlank()) ? patients.findAll()
                : patients.findByNameContainingIgnoreCase(search);
    }

    public Patient get(Long id) {
        return patients.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found: " + id));
    }

    public Patient create(Patient p) { p.setId(null); return patients.save(p); }

    public Patient update(Long id, Patient in) {
        Patient p = get(id);
        p.setName(in.getName()); p.setAge(in.getAge()); p.setGender(in.getGender());
        p.setPhone(in.getPhone()); p.setEmail(in.getEmail()); p.setAddress(in.getAddress());
        return patients.save(p);
    }

    public void delete(Long id) {
        Patient p = get(id);
        if (appointments.existsByPatientId(id) || records.existsByPatientId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Cannot delete a patient who has appointments or medical records");
        }
        patients.delete(p);
    }
}