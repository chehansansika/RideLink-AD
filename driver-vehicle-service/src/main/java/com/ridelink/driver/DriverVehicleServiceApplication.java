package com.ridelink.driver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the RideLink Driver & Vehicle Service.
 *
 * <p>This microservice manages driver operational profiles, vehicle information,
 * driver availability, service areas, simulated locations, and eligible driver
 * discovery for the Ride Management Service.
 *
 * <p>Port: 8082
 * <p>Database: ridelink_driver_db
 */
@SpringBootApplication
public class DriverVehicleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverVehicleServiceApplication.class, args);
    }
}
