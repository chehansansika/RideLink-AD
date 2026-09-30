package com.ridelink.farepayment.dto.response;

import com.ridelink.farepayment.model.PaymentMethod;
import com.ridelink.farepayment.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response containing payment transaction details")
public class PaymentResponse {

    @Schema(description = "Payment identifier", example = "66f91b7e...")
    private String paymentId;

    @Schema(description = "Ride identifier", example = "ride-101")
    private String rideId;

    @Schema(description = "Passenger identifier", example = "passenger-501")
    private String passengerId;

    @Schema(description = "Payment amount", example = "453.0")
    private double amount;

    @Schema(description = "Currency", example = "LKR")
    private String currency;

    @Schema(description = "Payment method", example = "CARD")
    private PaymentMethod paymentMethod;

    @Schema(description = "Payment status", example = "COMPLETED")
    private PaymentStatus paymentStatus;

    @Schema(description = "Generated transaction reference", example = "TXN-7A8B9C")
    private String transactionReference;

    @Schema(description = "Reason for failure if payment status is FAILED", example = "Simulated card authorization decline")
    private String failureReason;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Completion timestamp")
    private LocalDateTime completedAt;

    public PaymentResponse() {
    }

    public PaymentResponse(String paymentId, String rideId, String passengerId, double amount,
                           String currency, PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                           String transactionReference, String failureReason,
                           LocalDateTime createdAt, LocalDateTime completedAt) {
        this.paymentId = paymentId;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionReference = transactionReference;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
