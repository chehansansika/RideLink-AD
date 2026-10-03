package com.ridelink.driver.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for registering a new vehicle.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVehicleRequest {

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotBlank(message = "Registration number is required")
    @Size(min = 3, max = 20, message = "Registration number must be between 3 and 20 characters")
    private String registrationNumber;

    @NotBlank(message = "Vehicle type is required")
    @Pattern(regexp = "^(CAR|VAN|BIKE|TUKTUK|MINIBUS|TRUCK)$",
             message = "Vehicle type must be one of: CAR, VAN, BIKE, TUKTUK, MINIBUS, TRUCK")
    private String vehicleType;

    @NotBlank(message = "Vehicle make is required")
    @Size(min = 2, max = 50, message = "Make must be between 2 and 50 characters")
    private String make;

    @NotBlank(message = "Vehicle model is required")
    @Size(min = 1, max = 50, message = "Model must be between 1 and 50 characters")
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1980, message = "Year must be 1980 or later")
    @Max(value = 2030, message = "Year must not exceed 2030")
    private Integer year;

    @NotBlank(message = "Color is required")
    @Size(min = 2, max = 30, message = "Color must be between 2 and 30 characters")
    private String color;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be a positive number")
    @Max(value = 50, message = "Capacity cannot exceed 50")
    private Integer capacity;
}
