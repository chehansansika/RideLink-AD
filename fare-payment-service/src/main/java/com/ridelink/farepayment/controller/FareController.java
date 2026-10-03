package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.dto.request.FareEstimateRequest;
import com.ridelink.farepayment.dto.request.FinalFareCalculateRequest;
import com.ridelink.farepayment.dto.response.ErrorResponse;
import com.ridelink.farepayment.dto.response.FareCalculationResponse;
import com.ridelink.farepayment.dto.response.FareEstimateResponse;
import com.ridelink.farepayment.service.FareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fares")
@Tag(name = "Fare Management", description = "Endpoints for fare estimation and final fare calculation based on distance and configurable rates")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/estimate")
    @Operation(summary = "Estimate ride fare", description = "Calculates an estimated fare for a ride based on distance and configurable base/KM rates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fare estimated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FareEstimateResponse> estimateFare(@Valid @RequestBody FareEstimateRequest request) {
        FareEstimateResponse response = fareService.estimateFare(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate and persist final fare", description = "Calculates the final fare upon ride completion and saves it to MongoDB for payment processing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Final fare calculated and stored successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ride or distance details",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FareCalculationResponse> calculateFinalFare(@Valid @RequestBody FinalFareCalculateRequest request) {
        FareCalculationResponse response = fareService.calculateAndSaveFinalFare(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get fare by ride ID", description = "Retrieves the stored fare calculation details for a completed ride.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fare record retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Fare record not found for ride ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FareCalculationResponse> getFareByRideId(
            @io.swagger.v3.oas.annotations.Parameter(description = "Identifier of the ride", schema = @Schema(defaultValue = "RIDE-TEST-1"))
            @PathVariable String rideId) {
        return ResponseEntity.ok(fareService.getFareByRideId(rideId));
    }

    @GetMapping("/{fareId}")
    @Operation(summary = "Get fare by fare ID", description = "Retrieves the stored fare record by its unique database identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fare record retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Fare record not found with ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FareCalculationResponse> getFareById(
            @io.swagger.v3.oas.annotations.Parameter(description = "Database ID of the fare record", schema = @Schema(defaultValue = "6abfa4a7a6ec111867535464"))
            @PathVariable String fareId) {
        return ResponseEntity.ok(fareService.getFareById(fareId));
    }
}
