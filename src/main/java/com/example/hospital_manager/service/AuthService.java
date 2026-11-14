package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.UserRepository;
import com.example.hospital_manager.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        AppUser u = users.findByUsername(req.username())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return new AuthResponse(jwt.generateToken(u), "Bearer", jwt.getExpirationMs(),
                u.getUsername(), u.getFullName(), u.getRole());
    }

    /** Anyone can self-register as PATIENT. Creating staff roles needs an ADMIN token. */
    public UserResponse register(RegisterRequest req) {
        if (req.role() != Role.PATIENT && !callerIsAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only an ADMIN can create " + req.role() + " accounts");
        }
        if (users.existsByUsername(req.username())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }
        AppUser u = new AppUser();
        u.setUsername(req.username());
        u.setPassword(encoder.encode(req.password()));   // BCrypt hash
        u.setFullName(req.fullName());
        u.setRole(req.role());
        return UserResponse.from(users.save(u));
    }

    public UserResponse me(String username) {
        return users.findByUsername(username).map(UserResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<UserResponse> listUsers() { return users.findAll().stream().map(UserResponse::from).toList(); }

    public List<UserResponse> listDoctors() {
        return users.findByRole(Role.DOCTOR).stream().map(UserResponse::from).toList();
    }

    private boolean callerIsAdmin() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.isAuthenticated() && !(a instanceof AnonymousAuthenticationToken)
                && a.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
    }
}