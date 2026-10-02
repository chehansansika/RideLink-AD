package com.ridelink.farepayment.dto.response;

import com.ridelink.farepayment.model.PaymentMethod;
import com.ridelink.farepayment.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response containing receipt details issued upon successful payment")
public class ReceiptResponse {

    @Schema(description = "Unique receipt identifier", example = "66f91c3d...")
    private String receiptId;

    @Schema(description = "Associated payment identifier", example = "66f91b7e...")
    private String paymentId;

    @Schema(description = "Associated ride identifier", example = "ride-101")
    private String rideId;

    @Schema(description = "Passenger identifier", example = "passenger-501")
    private String passengerId;

    @Schema(description = "Total fare amount paid", example = "453.0")
    private double fareAmount;

    @Schema(description = "Currency", example = "LKR")
    private String currency;

    @Schema(description = "Payment method", example = "CARD")
    private PaymentMethod paymentMethod;

    @Schema(description = "Payment status", example = "COMPLETED")
    private PaymentStatus paymentStatus;

    @Schema(description = "Transaction reference", example = "TXN-7A8B9C")
    private String transactionReference;

    @Schema(description = "Receipt issuance timestamp")
    private LocalDateTime issuedAt;

    public ReceiptResponse() {
    }

    public ReceiptResponse(String receiptId, String paymentId, String rideId, String passengerId,
                           double fareAmount, String currency, PaymentMethod paymentMethod,
                           PaymentStatus paymentStatus, String transactionReference, LocalDateTime issuedAt) {
        this.receiptId = receiptId;
        this.paymentId = paymentId;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.fareAmount = fareAmount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionReference = transactionReference;
        this.issuedAt = issuedAt;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
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

    public double getFareAmount() {
        return fareAmount;
    }

    public void setFareAmount(double fareAmount) {
        this.fareAmount = fareAmount;
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

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }
}
