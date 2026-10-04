package com.ridelink.driver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new driver profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDriverRequest {

    /** Reference to the account in the Account Service. */
    private String accountId;

    @NotBlank(message = "Driver name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be 7-15 digits, optionally prefixed with +")
    private String phone;

    @NotBlank(message = "License number is required")
    @Size(min = 4, max = 30, message = "License number must be between 4 and 30 characters")
    private String licenseNumber;

    @NotBlank(message = "Service area is required")
    @Size(min = 2, max = 100, message = "Service area must be between 2 and 100 characters")
    private String serviceArea;
}
