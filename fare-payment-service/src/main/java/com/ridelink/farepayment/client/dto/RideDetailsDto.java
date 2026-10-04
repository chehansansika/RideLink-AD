package com.ridelink.farepayment.client.dto;

public class RideDetailsDto {

    private String rideId;
    private String passengerId;
    private String driverId;
    private String status;
    private Double distanceKm;
    private String pickupLocation;
    private String dropoffLocation;

    public RideDetailsDto() {
    }

    public RideDetailsDto(String rideId, String passengerId, String driverId, String status,
                          Double distanceKm, String pickupLocation, String dropoffLocation) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.status = status;
        this.distanceKm = distanceKm;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }
}
