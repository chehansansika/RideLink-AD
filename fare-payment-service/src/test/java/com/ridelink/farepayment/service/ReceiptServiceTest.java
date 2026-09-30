package com.ridelink.farepayment.service;

import com.ridelink.farepayment.dto.response.ReceiptResponse;
import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.PaymentMethod;
import com.ridelink.farepayment.model.PaymentStatus;
import com.ridelink.farepayment.model.Receipt;
import com.ridelink.farepayment.repository.ReceiptRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @InjectMocks
    private ReceiptService receiptService;

    @Test
    @DisplayName("Should generate and save a receipt from a completed payment")
    void testGenerateReceipt_Success() {
        Payment payment = new Payment(
                "pay-1",
                "ride-101",
                "passenger-501",
                450.0,
                "LKR",
                PaymentMethod.CARD,
                PaymentStatus.COMPLETED,
                "TXN-XYZ999",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Receipt savedReceipt = new Receipt(
                "receipt-1",
                "pay-1",
                "ride-101",
                "passenger-501",
                450.0,
                "LKR",
                PaymentMethod.CARD,
                PaymentStatus.COMPLETED,
                "TXN-XYZ999",
                LocalDateTime.now()
        );
        when(receiptRepository.save(any(Receipt.class))).thenReturn(savedReceipt);

        ReceiptResponse response = receiptService.generateReceipt(payment);

        assertNotNull(response);
        assertEquals("receipt-1", response.getReceiptId());
        assertEquals("pay-1", response.getPaymentId());
        assertEquals(450.0, response.getFareAmount());
        assertEquals(PaymentMethod.CARD, response.getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, response.getPaymentStatus());
        assertEquals("TXN-XYZ999", response.getTransactionReference());
    }

    @Test
    @DisplayName("Should retrieve receipt by receipt ID")
    void testGetReceiptById() {
        Receipt receipt = new Receipt("rec-1", "pay-1", "r-1", "pass-1", 150.0, "LKR",
                PaymentMethod.CASH, PaymentStatus.COMPLETED, "TXN-1", LocalDateTime.now());
        when(receiptRepository.findById("rec-1")).thenReturn(Optional.of(receipt));

        ReceiptResponse response = receiptService.getReceiptById("rec-1");
        assertNotNull(response);
        assertEquals("rec-1", response.getReceiptId());

        when(receiptRepository.findById("non-existent")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> receiptService.getReceiptById("non-existent"));
    }

    @Test
    @DisplayName("Should retrieve receipt by payment ID")
    void testGetReceiptByPaymentId() {
        Receipt receipt = new Receipt("rec-1", "pay-1", "r-1", "pass-1", 150.0, "LKR",
                PaymentMethod.CASH, PaymentStatus.COMPLETED, "TXN-1", LocalDateTime.now());
        when(receiptRepository.findByPaymentId("pay-1")).thenReturn(Optional.of(receipt));

        ReceiptResponse response = receiptService.getReceiptByPaymentId("pay-1");
        assertNotNull(response);
        assertEquals("pay-1", response.getPaymentId());

        when(receiptRepository.findByPaymentId("pay-unknown")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> receiptService.getReceiptByPaymentId("pay-unknown"));
    }

    @Test
    @DisplayName("Should retrieve receipt by ride ID")
    void testGetReceiptByRideId() {
        Receipt receipt = new Receipt("rec-1", "pay-1", "r-1", "pass-1", 150.0, "LKR",
                PaymentMethod.CASH, PaymentStatus.COMPLETED, "TXN-1", LocalDateTime.now());
        when(receiptRepository.findByRideId("r-1")).thenReturn(Optional.of(receipt));

        ReceiptResponse response = receiptService.getReceiptByRideId("r-1");
        assertNotNull(response);
        assertEquals("r-1", response.getRideId());

        when(receiptRepository.findByRideId("ride-unknown")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> receiptService.getReceiptByRideId("ride-unknown"));
    }
}
