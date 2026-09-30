package com.ridelink.farepayment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for estimating a ride fare")
public class FareEstimateRequest {

    @Schema(description = "Pickup location or address", example = "Colombo Fort Railway Station")
    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    @Schema(description = "Dropoff destination location or address", example = "Galle Face Green, Colombo")
    @NotBlank(message = "Dropoff location is required")
    private String dropoffLocation;

    @Schema(description = "Estimated ride distance in kilometers", example = "5.5")
    @NotNull(message = "Distance in kilometers is required")
    @Positive(message = "Distance must be greater than zero")
    private Double distanceKm;

    public FareEstimateRequest() {
    }

    public FareEstimateRequest(String pickupLocation, String dropoffLocation, Double distanceKm) {
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
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

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
}
