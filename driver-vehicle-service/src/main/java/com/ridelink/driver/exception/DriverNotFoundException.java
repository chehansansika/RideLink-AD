package com.ridelink.driver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested driver is not found in the database.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DriverNotFoundException extends RuntimeException {

    public DriverNotFoundException(String driverId) {
        super("Driver with ID " + driverId + " was not found");
    }
}
