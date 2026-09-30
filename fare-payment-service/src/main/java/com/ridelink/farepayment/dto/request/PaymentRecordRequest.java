package com.ridelink.farepayment.dto.request;

import com.ridelink.farepayment.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for recording a simulated payment")
public class PaymentRecordRequest {

    @Schema(description = "Identifier of the ride being paid for", example = "ride-101")
    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @Schema(description = "Identifier of the passenger making the payment", example = "passenger-501")
    @NotBlank(message = "Passenger ID is required")
    private String passengerId;

    @Schema(description = "Payment amount", example = "453.0")
    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be greater than zero")
    private Double amount;

    @Schema(description = "Simulated payment method (CASH, CARD, DIGITAL_WALLET)", example = "CARD")
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Schema(description = "Simulate payment failure for testing purposes", example = "false")
    private Boolean simulateFailure;

    public PaymentRecordRequest() {
    }

    public PaymentRecordRequest(String rideId, String passengerId, Double amount,
                                PaymentMethod paymentMethod, Boolean simulateFailure) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.simulateFailure = simulateFailure;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getSimulateFailure() {
        return simulateFailure;
    }

    public void setSimulateFailure(Boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}
