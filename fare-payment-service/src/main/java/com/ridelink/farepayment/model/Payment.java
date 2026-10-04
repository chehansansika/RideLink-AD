package com.ridelink.farepayment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    @Indexed
    private String rideId;

    private String passengerId;
    private double amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    @Indexed(unique = true, sparse = true)
    private String transactionReference;

    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public Payment() {
    }

    public Payment(String id, String rideId, String passengerId, double amount, String currency,
                   PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                   String transactionReference, String failureReason,
                   LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
