package com.ridelink.driver.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Driver & Vehicle Service.
 *
 * <p>Returns consistent JSON error responses. Stack traces are never exposed.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ------------------------------------------------------------------ //
    // 404 Not Found
    // ------------------------------------------------------------------ //

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDriverNotFound(
            DriverNotFoundException ex, HttpServletRequest request) {
        log.warn("Driver not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, "Driver Not Found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleVehicleNotFound(
            VehicleNotFoundException ex, HttpServletRequest request) {
        log.warn("Vehicle not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, "Vehicle Not Found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(
            org.springframework.web.servlet.resource.NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
    }

    // ------------------------------------------------------------------ //
    // 409 Conflict
    // ------------------------------------------------------------------ //

    @ExceptionHandler(DuplicateDriverException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateDriver(
            DuplicateDriverException ex, HttpServletRequest request) {
        log.warn("Duplicate driver: {}", ex.getMessage());
        return buildError(HttpStatus.CONFLICT, "Duplicate Driver", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DuplicateVehicleException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateVehicle(
            DuplicateVehicleException ex, HttpServletRequest request) {
        log.warn("Duplicate vehicle: {}", ex.getMessage());
        return buildError(HttpStatus.CONFLICT, "Duplicate Vehicle", ex.getMessage(), request.getRequestURI());
    }

    // ------------------------------------------------------------------ //
    // 400 Bad Request — Bean Validation
    // ------------------------------------------------------------------ //

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("message", "One or more fields failed validation");
        body.put("fieldErrors", fieldErrors);
        body.put("path", request.getRequestURI());

        log.warn("Validation failed for request {}: {}", request.getRequestURI(), fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableMessage(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Unreadable HTTP message: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request",
                "Request body is missing or contains invalid JSON", request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
    }

    // ------------------------------------------------------------------ //
    // 500 Internal Server Error — catch-all
    // ------------------------------------------------------------------ //

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: ", request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred. Please try again later.", request.getRequestURI());
    }

    // ------------------------------------------------------------------ //
    // Helper
    // ------------------------------------------------------------------ //

    private ResponseEntity<Map<String, Object>> buildError(
            HttpStatus status, String error, String message, String path) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);

        return ResponseEntity.status(status).body(body);
    }
}
