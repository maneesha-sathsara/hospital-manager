package com.example.hospital_manager.controller;

import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService service;

    public PatientController(PatientService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public List<Patient> list(@RequestParam(required = false) String search) { return service.list(search); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public Patient get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','NURSE','RECEPTIONIST')")
    public ResponseEntity<Patient> create(@Valid @RequestBody Patient p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(p));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','NURSE','RECEPTIONIST')")
    public Patient update(@PathVariable Long id, @Valid @RequestBody Patient p) { return service.update(id, p); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}