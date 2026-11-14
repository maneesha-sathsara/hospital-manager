package com.example.hospital_manager.config;

import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository users;
    private final PatientRepository patients;
    private final PasswordEncoder encoder;

    public DataLoader(UserRepository users, PatientRepository patients, PasswordEncoder encoder) {
        this.users = users; this.patients = patients; this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            seed("admin", "admin123", "System Admin", Role.ADMIN);
            seed("doctor1", "doctor123", "Dr. Silva", Role.DOCTOR);
            seed("nurse1", "nurse123", "Nurse Perera", Role.NURSE);
            seed("reception1", "recep123", "Reception Fernando", Role.RECEPTIONIST);
        }
        if (patients.count() == 0) {
            patients.save(new Patient(null, "Kamal Jayasuriya", 45, "Male", "0771234567", "kamal@mail.com", "Colombo"));
            patients.save(new Patient(null, "Nimali Weerasinghe", 32, "Female", "0712345678", "nimali@mail.com", "Kandy"));
            patients.save(new Patient(null, "Sunil Rathnayake", 60, "Male", "0751112223", "sunil@mail.com", "Galle"));
        }
    }

    private void seed(String u, String p, String name, Role r) {
        users.save(new AppUser(null, u, encoder.encode(p), name, r));
    }
}