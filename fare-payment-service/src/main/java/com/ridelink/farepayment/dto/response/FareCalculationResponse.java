package com.ridelink.farepayment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response containing final calculated fare details")
public class FareCalculationResponse {

    @Schema(description = "Unique fare identifier", example = "66f91a2b...")
    private String fareId;

    @Schema(description = "Ride identifier", example = "ride-101")
    private String rideId;

    @Schema(description = "Passenger identifier", example = "passenger-501")
    private String passengerId;

    @Schema(description = "Distance in kilometers", example = "8.2")
    private double distanceKm;

    @Schema(description = "Base fare amount", example = "100.0")
    private double baseFare;

    @Schema(description = "Rate applied per kilometer", example = "40.0")
    private double ratePerKm;

    @Schema(description = "Distance charge (distance * ratePerKm)", example = "328.0")
    private double distanceCharge;

    @Schema(description = "Service fee", example = "25.0")
    private double serviceFee;

    @Schema(description = "Total final fare", example = "453.0")
    private double totalFare;

    @Schema(description = "Currency", example = "LKR")
    private String currency;

    @Schema(description = "Calculation timestamp")
    private LocalDateTime calculatedAt;

    public FareCalculationResponse() {
    }

    public FareCalculationResponse(String fareId, String rideId, String passengerId, double distanceKm,
                                   double baseFare, double ratePerKm, double distanceCharge,
                                   double serviceFee, double totalFare, String currency,
                                   LocalDateTime calculatedAt) {
        this.fareId = fareId;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.distanceKm = distanceKm;
        this.baseFare = baseFare;
        this.ratePerKm = ratePerKm;
        this.distanceCharge = distanceCharge;
        this.serviceFee = serviceFee;
        this.totalFare = totalFare;
        this.currency = currency;
        this.calculatedAt = calculatedAt;
    }

    public String getFareId() {
        return fareId;
    }

    public void setFareId(String fareId) {
        this.fareId = fareId;
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

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}
