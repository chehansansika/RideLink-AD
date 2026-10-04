package com.ridelink.driver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested vehicle is not found in the database.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class VehicleNotFoundException extends RuntimeException {

    public VehicleNotFoundException(String vehicleId) {
        super("Vehicle with ID " + vehicleId + " was not found");
    }
}
