package com.ridelink.driver.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing driver profile.
 * All fields are optional; only provided non-null fields will be updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be 7-15 digits, optionally prefixed with +")
    private String phone;

    @Size(min = 4, max = 30, message = "License number must be between 4 and 30 characters")
    private String licenseNumber;

    @Size(min = 2, max = 100, message = "Service area must be between 2 and 100 characters")
    private String serviceArea;

    private String vehicleId;
}
