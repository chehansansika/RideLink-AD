package com.ridelink.driver.model;

/**
 * Enumeration representing the availability status of a driver.
 */
public enum DriverAvailability {

    /** Driver is available and can accept new rides. */
    AVAILABLE,

    /** Driver is currently on a ride. */
    BUSY,

    /** Driver is not active / logged out. */
    OFFLINE
}
