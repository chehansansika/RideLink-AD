package com.ridelink.ride_management_service.exception;

public class RideNotFoundException extends RuntimeException {

    public RideNotFoundException(String id) {
        super("Ride not found with id: " + id);
    }
}
