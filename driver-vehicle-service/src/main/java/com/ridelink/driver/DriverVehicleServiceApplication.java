package com.ridelink.driver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

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

    static {
        // Java 21 tightened TLS defaults via `jdk.tls.disabledAlgorithms` (a Security property,
        // not a system property) and added `include jdk.disabled.namedCurves`, which removes
        // several EC curves still used by some MongoDB Atlas cluster TLS configurations.
        // The override below restores a compatible policy while keeping genuinely weak ciphers
        // (RC4, DES, NULL) and deprecated protocol versions (TLSv1, TLSv1.1, SSLv3) disabled.
        // NOTE: Security.setProperty must be called before any SSL context is created.
        Security.setProperty("jdk.tls.disabledAlgorithms",
                "SSLv3, TLSv1, TLSv1.1, RC4, DES, MD5withRSA, " +
                "DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC, anon, NULL");
    }

    public static void main(String[] args) {
        SpringApplication.run(DriverVehicleServiceApplication.class, args);
    }
}
