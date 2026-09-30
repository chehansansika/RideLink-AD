package com.ridelink.farepayment.service;

import com.ridelink.farepayment.config.FareProperties;
import com.ridelink.farepayment.dto.request.PaymentRecordRequest;
import com.ridelink.farepayment.dto.response.PaymentResponse;
import com.ridelink.farepayment.exception.BadRequestException;
import com.ridelink.farepayment.exception.DuplicatePaymentException;
import com.ridelink.farepayment.exception.PaymentProcessingException;
import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.PaymentMethod;
import com.ridelink.farepayment.model.PaymentStatus;
import com.ridelink.farepayment.repository.FareRepository;
import com.ridelink.farepayment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private FareRepository fareRepository;

    @Mock
    private ReceiptService receiptService;

    @Mock
    private FareProperties fareProperties;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        lenient().when(fareProperties.getCurrency()).thenReturn("LKR");
    }

    @Test
    @DisplayName("Should successfully record simulated payment, complete transaction and issue receipt")
    void testRecordPayment_Success() {
        PaymentRecordRequest request = new PaymentRecordRequest("ride-101", "passenger-501", 325.0, PaymentMethod.CARD, false);

        when(paymentRepository.existsByRideIdAndPaymentStatus("ride-101", PaymentStatus.COMPLETED)).thenReturn(false);

        Fare fare = new Fare("f1", "ride-101", "passenger-501", 5.0, 100.0, 40.0, 200.0, 25.0, 325.0, "LKR", LocalDateTime.now());
        when(fareRepository.findByRideId("ride-101")).thenReturn(Optional.of(fare));

        Payment savedPayment = new Payment(
                "payment-1",
                "ride-101",
                "passenger-501",
                325.0,
                "LKR",
                PaymentMethod.CARD,
                PaymentStatus.COMPLETED,
                "TXN-12345678",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        PaymentResponse response = paymentService.recordPayment(request);

        assertNotNull(response);
        assertEquals("payment-1", response.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, response.getPaymentStatus());
        assertEquals("TXN-12345678", response.getTransactionReference());
        verify(receiptService, times(1)).generateReceipt(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw DuplicatePaymentException when payment for ride is already completed")
    void testRecordPayment_Duplicate() {
        PaymentRecordRequest request = new PaymentRecordRequest("ride-101", "passenger-501", 325.0, PaymentMethod.CASH, false);

        when(paymentRepository.existsByRideIdAndPaymentStatus("ride-101", PaymentStatus.COMPLETED)).thenReturn(true);

        assertThrows(DuplicatePaymentException.class, () -> paymentService.recordPayment(request));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when paid amount is less than total fare")
    void testRecordPayment_AmountLessThanFare() {
        PaymentRecordRequest request = new PaymentRecordRequest("ride-101", "passenger-501", 100.0, PaymentMethod.DIGITAL_WALLET, false);

        when(paymentRepository.existsByRideIdAndPaymentStatus("ride-101", PaymentStatus.COMPLETED)).thenReturn(false);

        Fare fare = new Fare("f1", "ride-101", "passenger-501", 5.0, 100.0, 40.0, 200.0, 25.0, 325.0, "LKR", LocalDateTime.now());
        when(fareRepository.findByRideId("ride-101")).thenReturn(Optional.of(fare));

        assertThrows(BadRequestException.class, () -> paymentService.recordPayment(request));
    }

    @Test
    @DisplayName("Should simulate payment failure, persist FAILED status and throw PaymentProcessingException")
    void testRecordPayment_SimulatedFailure() {
        PaymentRecordRequest request = new PaymentRecordRequest("ride-101", "passenger-501", 325.0, PaymentMethod.CARD, true);

        when(paymentRepository.existsByRideIdAndPaymentStatus("ride-101", PaymentStatus.COMPLETED)).thenReturn(false);

        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class,
                () -> paymentService.recordPayment(request));

        assertTrue(exception.getMessage().contains("Simulated payment failed"));
        verify(paymentRepository, times(1)).save(argThat(p -> p.getPaymentStatus() == PaymentStatus.FAILED));
        verify(receiptService, never()).generateReceipt(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when amount is zero or negative")
    void testRecordPayment_InvalidAmount() {
        PaymentRecordRequest zeroAmount = new PaymentRecordRequest("r1", "p1", 0.0, PaymentMethod.CASH, false);
        PaymentRecordRequest negativeAmount = new PaymentRecordRequest("r1", "p1", -50.0, PaymentMethod.CASH, false);

        assertThrows(BadRequestException.class, () -> paymentService.recordPayment(zeroAmount));
        assertThrows(BadRequestException.class, () -> paymentService.recordPayment(negativeAmount));
    }

    @Test
    @DisplayName("Should retrieve payment by ID or throw ResourceNotFoundException")
    void testGetPaymentById() {
        when(paymentRepository.findById("unknown")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById("unknown"));

        Payment payment = new Payment("p1", "r1", "pass1", 200.0, "LKR", PaymentMethod.CASH,
                PaymentStatus.COMPLETED, "TXN-1", null, LocalDateTime.now(), LocalDateTime.now());
        when(paymentRepository.findById("p1")).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentById("p1");
        assertNotNull(response);
        assertEquals("p1", response.getPaymentId());
    }

    @Test
    @DisplayName("Should retrieve payments by ride ID")
    void testGetPaymentsByRideId() {
        Payment payment = new Payment("p1", "r1", "pass1", 200.0, "LKR", PaymentMethod.CASH,
                PaymentStatus.COMPLETED, "TXN-1", null, LocalDateTime.now(), LocalDateTime.now());
        when(paymentRepository.findByRideId("r1")).thenReturn(List.of(payment));

        List<PaymentResponse> responses = paymentService.getPaymentsByRideId("r1");
        assertEquals(1, responses.size());
        assertEquals("r1", responses.get(0).getRideId());
    }
}
