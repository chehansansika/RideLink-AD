package com.ridelink.driver.dto;

import com.ridelink.driver.model.DriverAvailability;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a driver's availability status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAvailabilityRequest {

    @NotNull(message = "Availability status is required")
    private DriverAvailability status;
}
