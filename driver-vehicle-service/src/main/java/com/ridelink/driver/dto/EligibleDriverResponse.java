package com.ridelink.driver.dto;

import com.ridelink.driver.model.DriverAvailability;
import com.ridelink.driver.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned by the eligible driver endpoint.
 *
 * <p>Used by the Ride Management Service to discover drivers that can be
 * assigned to new ride requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleDriverResponse {

    private String driverId;
    private String name;
    private String vehicleId;
    private String vehicleType;
    private String serviceArea;
    private DriverAvailability availabilityStatus;
    private Location currentLocation;
}
