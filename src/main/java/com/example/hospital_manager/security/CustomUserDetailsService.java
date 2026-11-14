package com.example.hospital_manager.security;

import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    public CustomUserDetailsService(UserRepository users) { this.users = users; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities("ROLE_" + u.getRole().name())
                .build();
    }
}