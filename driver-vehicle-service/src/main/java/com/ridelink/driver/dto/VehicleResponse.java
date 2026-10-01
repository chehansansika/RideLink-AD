package com.ridelink.driver.dto;

import com.ridelink.driver.model.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO returned for vehicle-related API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    private String id;
    private String driverId;
    private String registrationNumber;
    private String vehicleType;
    private String make;
    private String model;
    private Integer year;
    private String color;
    private Integer capacity;
    private VehicleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
