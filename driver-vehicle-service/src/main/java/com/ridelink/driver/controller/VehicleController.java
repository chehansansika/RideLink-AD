package com.ridelink.driver.controller;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for vehicle management endpoints.
 *
 * <p>Controller is kept thin — all business logic is delegated to {@link VehicleService}.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Vehicle registration, retrieval, update, and status management APIs")
public class VehicleController {

    private final VehicleService vehicleService;

    // ------------------------------------------------------------------ //
    // CRUD
    // ------------------------------------------------------------------ //

    @Operation(summary = "Register a new vehicle",
               description = "Registers a new vehicle and associates it with a driver. " +
                             "Registration number must be unique across all vehicles.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vehicle registered successfully",
                     content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Referenced driver not found"),
        @ApiResponse(responseCode = "409", description = "Registration number already exists")
    })
    @PostMapping("/api/vehicles")
    public ResponseEntity<VehicleResponse> createVehicle(
            @Valid @RequestBody CreateVehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get vehicle by ID",
               description = "Retrieves the details of a specific vehicle.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle found",
                     content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
        @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @GetMapping("/api/vehicles/{vehicleId}")
    public ResponseEntity<VehicleResponse> getVehicle(
            @Parameter(description = "Vehicle document ID", required = true)
            @PathVariable String vehicleId) {
        return ResponseEntity.ok(vehicleService.getVehicle(vehicleId));
    }

    @Operation(summary = "Get all vehicles for a driver",
               description = "Retrieves all vehicles registered under a specific driver.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of vehicles returned"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @GetMapping("/api/drivers/{driverId}/vehicles")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByDriver(
            @Parameter(description = "Driver document ID", required = true)
            @PathVariable String driverId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByDriver(driverId));
    }

    @Operation(summary = "Update vehicle details",
               description = "Updates one or more fields of an existing vehicle record.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle updated successfully",
                     content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "409", description = "Registration number already in use")
    })
    @PutMapping("/api/vehicles/{vehicleId}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @Parameter(description = "Vehicle document ID", required = true)
            @PathVariable String vehicleId,
            @Valid @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(vehicleId, request));
    }

    // ------------------------------------------------------------------ //
    // Status Management
    // ------------------------------------------------------------------ //

    @Operation(summary = "Activate a vehicle",
               description = "Sets the vehicle status to ACTIVE, making it eligible for ride dispatch.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle activated",
                     content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
        @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @PatchMapping("/api/vehicles/{vehicleId}/activate")
    public ResponseEntity<VehicleResponse> activateVehicle(
            @Parameter(description = "Vehicle document ID", required = true)
            @PathVariable String vehicleId) {
        return ResponseEntity.ok(vehicleService.activateVehicle(vehicleId));
    }

    @Operation(summary = "Deactivate a vehicle",
               description = "Sets the vehicle status to INACTIVE. " +
                             "Drivers cannot be set to AVAILABLE if they have no other ACTIVE vehicles.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle deactivated",
                     content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
        @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    @PatchMapping("/api/vehicles/{vehicleId}/deactivate")
    public ResponseEntity<VehicleResponse> deactivateVehicle(
            @Parameter(description = "Vehicle document ID", required = true)
            @PathVariable String vehicleId) {
        return ResponseEntity.ok(vehicleService.deactivateVehicle(vehicleId));
    }
}
