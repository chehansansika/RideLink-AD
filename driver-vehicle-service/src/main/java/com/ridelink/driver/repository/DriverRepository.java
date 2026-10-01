package com.ridelink.driver.repository;

import com.ridelink.driver.model.Driver;
import com.ridelink.driver.model.DriverAvailability;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for {@link Driver} documents.
 */
@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {

    /**
     * Find a driver by unique license number.
     *
     * @param licenseNumber the license number to search
     * @return an Optional containing the driver if found
     */
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    /**
     * Check whether a driver with the given license number already exists.
     *
     * @param licenseNumber the license number to check
     * @return true if a driver with this license number exists
     */
    boolean existsByLicenseNumber(String licenseNumber);

    /**
     * Find all drivers with a specific availability status.
     *
     * @param status the availability status to filter by
     * @return list of matching drivers
     */
    List<Driver> findByAvailabilityStatus(DriverAvailability status);

    /**
     * Find all drivers with a specific availability status and service area.
     *
     * @param status      the availability status
     * @param serviceArea the service area (case-sensitive)
     * @return list of matching drivers
     */
    List<Driver> findByAvailabilityStatusAndServiceArea(DriverAvailability status, String serviceArea);
}
