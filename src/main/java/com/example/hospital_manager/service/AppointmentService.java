package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final PatientRepository patients;
    private final UserRepository users;

    public AppointmentService(AppointmentRepository a, PatientRepository p, UserRepository u) {
        this.appointments = a; this.patients = p; this.users = u;
    }

    public AppointmentResponse book(AppointmentRequest req) {
        Patient p = patients.findById(req.patientId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
        AppUser d = users.findById(req.doctorId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found"));
        if (d.getRole() != Role.DOCTOR) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Selected user is not a doctor");
        }
        Appointment a = new Appointment();
        a.setPatient(p); a.setDoctor(d); a.setDateTime(req.dateTime()); a.setReason(req.reason());
        a.setStatus(AppointmentStatus.SCHEDULED);
        return AppointmentResponse.from(appointments.save(a));
    }

    /** DOCTOR sees only own appointments; other staff see all. */
    public List<AppointmentResponse> list(String username, Role role) {
        List<Appointment> list = (role == Role.DOCTOR)
                ? appointments.findByDoctorId(currentUser(username).getId())
                : appointments.findAll();
        return list.stream().map(AppointmentResponse::from).toList();
    }

    /** Ownership rule: a doctor can only change status of his/her own appointments. */
    public AppointmentResponse updateStatus(Long id, StatusRequest req, String username, Role role) {
        Appointment a = appointments.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));
        if (role == Role.DOCTOR && !a.getDoctor().getUsername().equals(username)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "This appointment belongs to another doctor");
        }
        a.setStatus(req.status());
        return AppointmentResponse.from(appointments.save(a));
    }

    private AppUser currentUser(String username) {
        return users.findByUsername(username).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }
}