package com.ridelink.driver.controller;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.model.Location;
import com.ridelink.driver.service.DriverService;
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
 * REST controller for driver management endpoints.
 *
 * <p>Controller is kept thin — all business logic is delegated to {@link DriverService}.
 */
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Driver profile management, availability, location, and service area APIs")
public class DriverController {

    private final DriverService driverService;

    // ------------------------------------------------------------------ //
    // CRUD
    // ------------------------------------------------------------------ //

    @Operation(summary = "Register a new driver",
               description = "Creates a new driver profile. License number must be unique.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Driver created successfully",
                     content = @Content(schema = @Schema(implementation = DriverResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "409", description = "Driver with same license number already exists")
    })
    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(
            @Valid @RequestBody CreateDriverRequest request) {
        DriverResponse response = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get driver by ID",
               description = "Retrieves the profile of a specific driver.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Driver found",
                     content = @Content(schema = @Schema(implementation = DriverResponse.class))),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @GetMapping("/{driverId}")
    public ResponseEntity<DriverResponse> getDriver(
            @Parameter(description = "Driver document ID", required = true)
            @PathVariable String driverId) {
        return ResponseEntity.ok(driverService.getDriver(driverId));
    }

    @Operation(summary = "Get all drivers",
               description = "Retrieves a list of all driver profiles.")
    @ApiResponse(responseCode = "200", description = "List of drivers returned")
    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @Operation(summary = "Update driver profile",
               description = "Updates one or more fields of an existing driver profile.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Driver updated successfully",
                     content = @Content(schema = @Schema(implementation = DriverResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Driver not found"),
        @ApiResponse(responseCode = "409", description = "License number already in use")
    })
    @PutMapping("/{driverId}")
    public ResponseEntity<DriverResponse> updateDriver(
            @Parameter(description = "Driver document ID", required = true)
            @PathVariable String driverId,
            @Valid @RequestBody UpdateDriverRequest request) {
        return ResponseEntity.ok(driverService.updateDriver(driverId, request));
    }

    @Operation(summary = "Delete a driver",
               description = "Permanently removes a driver profile from the system.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Driver deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @DeleteMapping("/{driverId}")
    public ResponseEntity<Void> deleteDriver(
            @Parameter(description = "Driver document ID", required = true)
            @PathVariable String driverId) {
        driverService.deleteDriver(driverId);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------ //
    // Availability
    // ------------------------------------------------------------------ //

    @Operation(summary = "Update driver availability",
               description = """
                   Updates the availability status of a driver.
                   
                   **Rules:**
                   - Setting to `AVAILABLE` requires the driver to have at least one `ACTIVE` vehicle.
                   - `BUSY` drivers are excluded from ride dispatch.
                   - `OFFLINE` drivers are excluded from ride dispatch.
                   """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Availability updated",
                     content = @Content(schema = @Schema(implementation = DriverResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid status or no active vehicle for AVAILABLE"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @PatchMapping("/{driverId}/availability")
    public ResponseEntity<DriverResponse> updateAvailability(
            @Parameter(description = "Driver document ID", required = true)
            @PathVariable String driverId,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        return ResponseEntity.ok(driverService.updateAvailability(driverId, request));
    }

    // ------------------------------------------------------------------ //
    // Location (Simulated)
    // ------------------------------------------------------------------ //

    @Operation(summary = "Update driver location (simulated)",
               description = "Updates the simulated current geographic location of the driver.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Location updated",
                     content = @Content(schema = @Schema(implementation = DriverResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid coordinates"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @PatchMapping("/{driverId}/location")
    public ResponseEntity<DriverResponse> updateLocation(
            @Parameter(description = "Driver document ID", required = true)
            @PathVariable String driverId,
            @Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(driverService.updateLocation(driverId, request));
    }

    @Operation(summary = "Get driver location",
               description = "Retrieves the last known simulated location of the driver.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Location returned",
                     content = @Content(schema = @Schema(implementation = Location.class))),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @GetMapping("/{driverId}/location")
    public ResponseEntity<Location> getLocation(
            @Parameter(description = "Driver document ID", required = true)
            @PathVariable String driverId) {
        Location location = driverService.getLocation(driverId);
        if (location == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(location);
    }

    // ------------------------------------------------------------------ //
    // Service Area
    // ------------------------------------------------------------------ //

    @Operation(summary = "Update driver service area",
               description = "Sets the geographic service area for a driver (e.g., 'Colombo', 'Kandy').")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Service area updated",
                     content = @Content(schema = @Schema(implementation = DriverResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @PatchMapping("/{driverId}/service-area")
    public ResponseEntity<DriverResponse> updateServiceArea(
            @Parameter(description = "Driver document ID", required = true)
            @PathVariable String driverId,
            @Valid @RequestBody UpdateServiceAreaRequest request) {
        return ResponseEntity.ok(driverService.updateServiceArea(driverId, request));
    }

    // ------------------------------------------------------------------ //
    // Eligible Drivers (Integration API)
    // ------------------------------------------------------------------ //

    @Operation(summary = "Get eligible drivers",
               description = """
                   Returns a list of drivers eligible for ride assignment.
                   
                   **Eligibility criteria:**
                   - Availability status is `AVAILABLE`
                   - Has at least one vehicle with status `ACTIVE`
                   - Service area matches the provided `serviceArea` parameter (when given)
                   
                   **Used by:** Ride Management Service
                   """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of eligible drivers returned",
                     content = @Content(schema = @Schema(implementation = EligibleDriverResponse.class)))
    })
    @GetMapping("/eligible")
    public ResponseEntity<List<EligibleDriverResponse>> getEligibleDrivers(
            @Parameter(description = "Filter by service area (optional)")
            @RequestParam(required = false) String serviceArea,
            @Parameter(description = "Requester latitude for proximity (optional, informational only)")
            @RequestParam(required = false) Double latitude,
            @Parameter(description = "Requester longitude for proximity (optional, informational only)")
            @RequestParam(required = false) Double longitude) {
        return ResponseEntity.ok(driverService.getEligibleDrivers(serviceArea));
    }
}
