package com.ridelink.ride_management_service.controller;

import com.ridelink.ride_management_service.dto.RideRequest;
import com.ridelink.ride_management_service.dto.RideResponse;
import com.ridelink.ride_management_service.service.RideService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping
    public ResponseEntity<List<RideResponse>> getAllRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable String id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @PostMapping
    public ResponseEntity<RideResponse> createRide(
            @Valid @RequestBody RideRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rideService.createRide(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideResponse> updateRide(
            @PathVariable String id,
            @Valid @RequestBody RideRequest request) {

        return ResponseEntity.ok(rideService.updateRide(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable String id) {

        rideService.deleteRide(id);

        return ResponseEntity.noContent().build();
    }
}
