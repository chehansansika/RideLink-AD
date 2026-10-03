package com.ridelink.driver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a driver's service area.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceAreaRequest {

    @NotBlank(message = "Service area is required")
    @Size(min = 2, max = 100, message = "Service area must be between 2 and 100 characters")
    private String serviceArea;
}
