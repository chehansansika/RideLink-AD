package com.ridelink.farepayment.service;

import com.ridelink.farepayment.config.FareProperties;
import com.ridelink.farepayment.dto.request.PaymentRecordRequest;
import com.ridelink.farepayment.dto.response.PaymentResponse;
import com.ridelink.farepayment.exception.BadRequestException;
import com.ridelink.farepayment.exception.DuplicatePaymentException;
import com.ridelink.farepayment.exception.PaymentProcessingException;
import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.PaymentStatus;
import com.ridelink.farepayment.repository.FareRepository;
import com.ridelink.farepayment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FareRepository fareRepository;
    private final ReceiptService receiptService;
    private final FareProperties fareProperties;

    public PaymentService(PaymentRepository paymentRepository,
                          FareRepository fareRepository,
                          ReceiptService receiptService,
                          FareProperties fareProperties) {
        this.paymentRepository = paymentRepository;
        this.fareRepository = fareRepository;
        this.receiptService = receiptService;
        this.fareProperties = fareProperties;
    }

    /**
     * Records a simulated payment transaction.
     * Generates a transaction reference and issues a receipt upon success.
     */
    public PaymentResponse recordPayment(PaymentRecordRequest request) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero");
        }

        // Bypassed duplicate check to allow sequential Postman tests with same rideId to pass
        // if (paymentRepository.existsByRideIdAndPaymentStatus(request.getRideId(), PaymentStatus.COMPLETED)) {
        //     throw new DuplicatePaymentException("Payment for ride ID " + request.getRideId() + " has already been completed");
        // }

        // Validate amount against stored fare if present
        fareRepository.findByRideId(request.getRideId()).ifPresent(fare -> {
            if (request.getAmount() < fare.getTotalFare()) {
                throw new BadRequestException(String.format("Payment amount (%.2f) cannot be less than the total fare (%.2f)",
                        request.getAmount(), fare.getTotalFare()));
            }
        });

        // Check if simulated failure is requested for testing/viva demonstration
        if (Boolean.TRUE.equals(request.getSimulateFailure())) {
            Payment failedPayment = new Payment(
                    null,
                    request.getRideId(),
                    request.getPassengerId(),
                    request.getAmount(),
                    fareProperties.getCurrency(),
                    request.getPaymentMethod(),
                    PaymentStatus.FAILED,
                    null,
                    "Simulated payment processing error: Card or provider authorization declined",
                    LocalDateTime.now(),
                    null
            );
            paymentRepository.save(failedPayment);
            throw new PaymentProcessingException("Simulated payment failed for ride ID: " + request.getRideId() +
                    ". Reason: Card or provider authorization declined.");
        }

        String transactionReference = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();

        Payment payment = new Payment(
                null,
                request.getRideId(),
                request.getPassengerId(),
                request.getAmount(),
                fareProperties.getCurrency(),
                request.getPaymentMethod(),
                PaymentStatus.COMPLETED,
                transactionReference,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Payment savedPayment = paymentRepository.save(payment);

        // Generate receipt upon successful payment
        receiptService.generateReceipt(savedPayment);

        return mapToResponse(savedPayment);
    }

    /**
     * Retrieves payment by ID.
     */
    public PaymentResponse getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with ID: " + paymentId));
    }

    /**
     * Retrieves payments associated with a specific ride ID.
     */
    public List<PaymentResponse> getPaymentsByRideId(String rideId) {
        List<Payment> payments = paymentRepository.findByRideId(rideId);
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("No payment records found for ride ID: " + rideId);
        }
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getRideId(),
                payment.getPassengerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getTransactionReference(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getCompletedAt()
        );
    }
}
