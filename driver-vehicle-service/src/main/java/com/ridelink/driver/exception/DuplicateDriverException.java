package com.ridelink.driver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when attempting to register a driver with a license number that already exists.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateDriverException extends RuntimeException {

    public DuplicateDriverException(String licenseNumber) {
        super("A driver with license number '" + licenseNumber + "' already exists");
    }
}
