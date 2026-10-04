package com.ridelink.farepayment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for calculating final fare upon ride completion")
public class FinalFareCalculateRequest {

    @Schema(description = "Identifier of the completed ride", example = "ride-101")
    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @Schema(description = "Identifier of the passenger", example = "passenger-501")
    @NotBlank(message = "Passenger ID is required")
    private String passengerId;

    @Schema(description = "Actual distance traveled in kilometers", example = "8.2")
    @NotNull(message = "Distance in kilometers is required")
    @Positive(message = "Distance must be greater than zero")
    private Double distanceKm;

    public FinalFareCalculateRequest() {
    }

    public FinalFareCalculateRequest(String rideId, String passengerId, Double distanceKm) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.distanceKm = distanceKm;
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

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
}
