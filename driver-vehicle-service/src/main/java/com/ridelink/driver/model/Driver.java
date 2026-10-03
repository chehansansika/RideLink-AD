package com.ridelink.driver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB document representing a driver's operational profile in the RideLink system.
 *
 * <p>Authentication credentials are NOT stored here — that is the responsibility
 * of the Account Service.
 *
 * <p>Collection: drivers
 * <p>Database: ridelink_driver_db
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "drivers")
public class Driver {

    /** MongoDB document ID. */
    @Id
    private String id;

    /**
     * Reference to the account in the Account Service.
     * The Account Service owns authentication; this field links profiles.
     */
    private String accountId;

    /** Full name of the driver. */
    private String name;

    /** Contact phone number. */
    private String phone;

    /** Unique driver's license number. */
    @Indexed(unique = true)
    private String licenseNumber;

    /** Current availability of the driver. Defaults to OFFLINE on creation. */
    @Builder.Default
    private DriverAvailability availabilityStatus = DriverAvailability.OFFLINE;

    /** Geographic service area (e.g., "Colombo", "Kandy"). */
    private String serviceArea;

    /** Simulated current location of the driver. */
    private Location currentLocation;

    /** Reference to the vehicle assigned to this driver. */
    private String vehicleId;

    /** Timestamp when the driver profile was created. */
    @CreatedDate
    private LocalDateTime createdAt;

    /** Timestamp when the driver profile was last updated. */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
