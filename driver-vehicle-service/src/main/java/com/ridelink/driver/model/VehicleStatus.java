package com.ridelink.driver.model;

/**
 * Enumeration representing the operational status of a vehicle.
 */
public enum VehicleStatus {

    /** Vehicle is operational and can be assigned to rides. */
    ACTIVE,

    /** Vehicle is not operational (maintenance, decommissioned, etc.). */
    INACTIVE
}
