package com.example.hospital_manager.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final ErrorResponseWriter errorWriter;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService uds, ErrorResponseWriter errorWriter) {
        this.jwtService = jwtService;
        this.userDetailsService = uds;
        this.errorWriter = errorWriter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7);
        try {
            String username = jwtService.extractUsername(token);   // signature + expiry checked here
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            errorWriter.write(req, res, HttpStatus.UNAUTHORIZED, "Invalid or expired JWT token: " + e.getMessage());
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            errorWriter.write(req, res, HttpStatus.UNAUTHORIZED, "Invalid token user");
            return;
        }
        chain.doFilter(req, res);
    }
}