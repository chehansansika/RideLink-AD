package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.dto.request.PaymentRecordRequest;
import com.ridelink.farepayment.dto.response.ErrorResponse;
import com.ridelink.farepayment.dto.response.PaymentResponse;
import com.ridelink.farepayment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "Endpoints for simulated payment processing, status tracking, and retrieval")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Record simulated payment", description = "Processes a simulated payment (CASH, CARD, DIGITAL_WALLET) for a ride and generates an automatic receipt upon success.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment completed and receipt issued successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment input or amount less than fare",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate payment: Payment for ride has already been completed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Simulated payment processing failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> recordPayment(@Valid @RequestBody PaymentRecordRequest request) {
        PaymentResponse response = paymentService.recordPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment transaction details using the unique payment identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment record retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment record not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get payments by ride ID", description = "Retrieves all payment transactions associated with a given ride identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment records retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No payment records found for ride ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PaymentResponse>> getPaymentsByRideId(@PathVariable String rideId) {
        return ResponseEntity.ok(paymentService.getPaymentsByRideId(rideId));
    }
}
