package com.ridelink.farepayment.service;

import com.ridelink.farepayment.dto.response.ReceiptResponse;
import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.Receipt;
import com.ridelink.farepayment.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    /**
     * Generates and persists a receipt for a successfully completed payment.
     */
    public ReceiptResponse generateReceipt(Payment payment) {
        Receipt receipt = new Receipt(
                null,
                payment.getId(),
                payment.getRideId(),
                payment.getPassengerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getTransactionReference(),
                LocalDateTime.now()
        );

        Receipt saved = receiptRepository.save(receipt);
        return mapToResponse(saved);
    }

    /**
     * Retrieves a receipt by its unique receipt identifier.
     */
    public ReceiptResponse getReceiptById(String receiptId) {
        return receiptRepository.findById(receiptId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with ID: " + receiptId));
    }

    /**
     * Retrieves a receipt associated with a payment ID.
     */
    public ReceiptResponse getReceiptByPaymentId(String paymentId) {
        return receiptRepository.findByPaymentId(paymentId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found for payment ID: " + paymentId));
    }

    /**
     * Retrieves a receipt associated with a ride ID.
     */
    public ReceiptResponse getReceiptByRideId(String rideId) {
        return receiptRepository.findByRideId(rideId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found for ride ID: " + rideId));
    }

    private ReceiptResponse mapToResponse(Receipt receipt) {
        return new ReceiptResponse(
                receipt.getId(),
                receipt.getPaymentId(),
                receipt.getRideId(),
                receipt.getPassengerId(),
                receipt.getFareAmount(),
                receipt.getCurrency(),
                receipt.getPaymentMethod(),
                receipt.getPaymentStatus(),
                receipt.getTransactionReference(),
                receipt.getIssuedAt()
        );
    }
}
