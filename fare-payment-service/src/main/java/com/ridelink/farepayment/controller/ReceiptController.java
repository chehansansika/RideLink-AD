package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.dto.response.ErrorResponse;
import com.ridelink.farepayment.dto.response.ReceiptResponse;
import com.ridelink.farepayment.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipts")
@Tag(name = "Receipt Management", description = "Endpoints for retrieving payment receipts and transaction records")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping("/{receiptId}")
    @Operation(summary = "Get receipt by ID", description = "Retrieves a payment receipt by its unique receipt identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Receipt not found with specified ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReceiptResponse> getReceiptById(@PathVariable String receiptId) {
        return ResponseEntity.ok(receiptService.getReceiptById(receiptId));
    }

    @GetMapping("/payment/{paymentId}")
    @Operation(summary = "Get receipt by payment ID", description = "Retrieves the issued receipt associated with a specific payment identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Receipt not found for payment ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReceiptResponse> getReceiptByPaymentId(@PathVariable String paymentId) {
        return ResponseEntity.ok(receiptService.getReceiptByPaymentId(paymentId));
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get receipt by ride ID", description = "Retrieves the issued receipt associated with a specific ride identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Receipt not found for ride ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReceiptResponse> getReceiptByRideId(@PathVariable String rideId) {
        return ResponseEntity.ok(receiptService.getReceiptByRideId(rideId));
    }
}
