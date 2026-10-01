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
 * MongoDB document representing a vehicle registered in the RideLink system.
 *
 * <p>Collection: vehicles
 * <p>Database: ridelink_driver_db
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vehicles")
public class Vehicle {

    /** MongoDB document ID. */
    @Id
    private String id;

    /** Reference to the owning driver. */
    private String driverId;

    /** Unique vehicle registration/license plate number. */
    @Indexed(unique = true)
    private String registrationNumber;

    /** Type of vehicle (e.g., CAR, VAN, BIKE, TUKTUK). */
    private String vehicleType;

    /** Vehicle manufacturer (e.g., Toyota, Honda). */
    private String make;

    /** Vehicle model name (e.g., Prius, Civic). */
    private String model;

    /** Manufacturing year of the vehicle. */
    private Integer year;

    /** Color of the vehicle. */
    private String color;

    /** Passenger capacity of the vehicle. */
    private Integer capacity;

    /** Operational status of the vehicle. Defaults to ACTIVE on registration. */
    @Builder.Default
    private VehicleStatus status = VehicleStatus.ACTIVE;

    /** Timestamp when the vehicle was registered. */
    @CreatedDate
    private LocalDateTime createdAt;

    /** Timestamp when the vehicle record was last updated. */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
