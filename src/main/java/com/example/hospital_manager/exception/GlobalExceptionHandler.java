package com.example.hospital_manager.exception;

import com.example.hospital_manager.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> build(HttpStatus s, String msg, HttpServletRequest req) {
        return ResponseEntity.status(s).body(
                new ErrorResponse(Instant.now(), s.value(), s.getReasonPhrase(), msg, req.getRequestURI()));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> api(ApiException e, HttpServletRequest r) {
        return build(e.getStatus(), e.getMessage(), r);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)   // 400
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, msg, r);
    }

    @ExceptionHandler(BadCredentialsException.class)           // 401
    public ResponseEntity<ErrorResponse> badCreds(BadCredentialsException e, HttpServletRequest r) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid username or password", r);
    }

    @ExceptionHandler(AccessDeniedException.class)             // 403 (@PreAuthorize)
    public ResponseEntity<ErrorResponse> denied(AccessDeniedException e, HttpServletRequest r) {
        return build(HttpStatus.FORBIDDEN, "You do not have permission to access this resource", r);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> other(Exception e, HttpServletRequest r) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), r);
    }
}