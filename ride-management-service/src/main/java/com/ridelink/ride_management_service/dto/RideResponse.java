package com.ridelink.ride_management_service.dto;

import com.ridelink.ride_management_service.model.Ride;

public class RideResponse {

    private String id;
    private String passengerId;
    private String driverId;
    private String pickupLocation;
    private String dropoffLocation;
    private String status;

    public RideResponse() {
    }

    public RideResponse(Ride ride) {
        this.id = ride.getId();
        this.passengerId = ride.getPassengerId();
        this.driverId = ride.getDriverId();
        this.pickupLocation = ride.getPickupLocation();
        this.dropoffLocation = ride.getDropoffLocation();
        this.status = ride.getStatus();
    }

    public String getId() {
        return id;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public String getStatus() {
        return status;
    }
}
