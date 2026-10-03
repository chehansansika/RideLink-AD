package com.ridelink.driver.dto;

import com.ridelink.driver.model.DriverAvailability;
import com.ridelink.driver.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO returned for driver-related API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {

    private String id;
    private String accountId;
    private String name;
    private String phone;
    private String licenseNumber;
    private DriverAvailability availabilityStatus;
    private String serviceArea;
    private Location currentLocation;
    private String vehicleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
