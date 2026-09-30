package com.ridelink.farepayment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response containing estimated fare details and calculation breakdown")
public class FareEstimateResponse {

    @Schema(description = "Pickup location", example = "Colombo Fort Railway Station")
    private String pickupLocation;

    @Schema(description = "Dropoff destination location", example = "Galle Face Green, Colombo")
    private String dropoffLocation;

    @Schema(description = "Estimated distance in kilometers", example = "5.5")
    private double distanceKm;

    @Schema(description = "Base fare amount", example = "100.0")
    private double baseFare;

    @Schema(description = "Rate applied per kilometer", example = "40.0")
    private double ratePerKm;

    @Schema(description = "Calculated distance charge (distance * ratePerKm)", example = "220.0")
    private double distanceCharge;

    @Schema(description = "Service/booking fee", example = "25.0")
    private double serviceFee;

    @Schema(description = "Estimated total fare (baseFare + distanceCharge + serviceFee)", example = "345.0")
    private double estimatedFare;

    @Schema(description = "Currency code", example = "LKR")
    private String currency;

    @Schema(description = "Applied calculation formula", example = "Base Fare (100.0) + [Distance (5.5 km) * Rate (40.0/km)] + Service Fee (25.0)")
    private String calculationFormula;

    @Schema(description = "Timestamp when estimate was calculated")
    private LocalDateTime estimatedAt;

    public FareEstimateResponse() {
    }

    public FareEstimateResponse(String pickupLocation, String dropoffLocation, double distanceKm,
                                double baseFare, double ratePerKm, double distanceCharge,
                                double serviceFee, double estimatedFare, String currency,
                                String calculationFormula, LocalDateTime estimatedAt) {
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.distanceKm = distanceKm;
        this.baseFare = baseFare;
        this.ratePerKm = ratePerKm;
        this.distanceCharge = distanceCharge;
        this.serviceFee = serviceFee;
        this.estimatedFare = estimatedFare;
        this.currency = currency;
        this.calculationFormula = calculationFormula;
        this.estimatedAt = estimatedAt;
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

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public double getRatePerKm() {
        return ratePerKm;
    }

    public void setRatePerKm(double ratePerKm) {
        this.ratePerKm = ratePerKm;
    }

    public double getDistanceCharge() {
        return distanceCharge;
    }

    public void setDistanceCharge(double distanceCharge) {
        this.distanceCharge = distanceCharge;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCalculationFormula() {
        return calculationFormula;
    }

    public void setCalculationFormula(String calculationFormula) {
        this.calculationFormula = calculationFormula;
    }

    public LocalDateTime getEstimatedAt() {
        return estimatedAt;
    }

    public void setEstimatedAt(LocalDateTime estimatedAt) {
        this.estimatedAt = estimatedAt;
    }
}
