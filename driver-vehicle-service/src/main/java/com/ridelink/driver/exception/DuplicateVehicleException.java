package com.ridelink.driver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when attempting to register a vehicle with a registration number that already exists.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateVehicleException extends RuntimeException {

    public DuplicateVehicleException(String registrationNumber) {
        super("A vehicle with registration number '" + registrationNumber + "' already exists");
    }
}
