package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) { this.service = service; }

    private Role roleOf(Authentication a) {
        String r = a.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        return Role.valueOf(r);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public List<AppointmentResponse> list(Authentication a) { return service.list(a.getName(), roleOf(a)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','NURSE','RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody AppointmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.book(req));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public AppointmentResponse status(@PathVariable Long id, @Valid @RequestBody StatusRequest req, Authentication a) {
        return service.updateStatus(id, req, a.getName(), roleOf(a));
    }
}