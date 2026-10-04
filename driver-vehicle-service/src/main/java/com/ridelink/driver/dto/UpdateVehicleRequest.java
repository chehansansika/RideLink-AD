package com.ridelink.driver.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing vehicle record.
 * All fields are optional; only non-null fields will be updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleRequest {

    @Size(min = 3, max = 20, message = "Registration number must be between 3 and 20 characters")
    private String registrationNumber;

    @Pattern(regexp = "^(CAR|VAN|BIKE|TUKTUK|MINIBUS|TRUCK)$",
             message = "Vehicle type must be one of: CAR, VAN, BIKE, TUKTUK, MINIBUS, TRUCK")
    private String vehicleType;

    @Size(min = 2, max = 50, message = "Make must be between 2 and 50 characters")
    private String make;

    @Size(min = 1, max = 50, message = "Model must be between 1 and 50 characters")
    private String model;

    @Min(value = 1980, message = "Year must be 1980 or later")
    @Max(value = 2030, message = "Year must not exceed 2030")
    private Integer year;

    @Size(min = 2, max = 30, message = "Color must be between 2 and 30 characters")
    private String color;

    @Positive(message = "Capacity must be a positive number")
    @Max(value = 50, message = "Capacity cannot exceed 50")
    private Integer capacity;
}
