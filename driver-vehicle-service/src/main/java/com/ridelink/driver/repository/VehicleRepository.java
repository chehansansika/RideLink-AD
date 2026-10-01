package com.ridelink.driver.repository;

import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.model.VehicleStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for {@link Vehicle} documents.
 */
@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {

    /**
     * Find all vehicles belonging to a specific driver.
     *
     * @param driverId the driver ID
     * @return list of vehicles for the driver
     */
    List<Vehicle> findByDriverId(String driverId);

    /**
     * Check whether a vehicle with the given registration number already exists.
     *
     * @param registrationNumber the registration number to check
     * @return true if a vehicle with this number exists
     */
    boolean existsByRegistrationNumber(String registrationNumber);

    /**
     * Find a vehicle by its registration number.
     *
     * @param registrationNumber the registration number to search
     * @return an Optional containing the vehicle if found
     */
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    /**
     * Find all vehicles of a driver that have a specific status.
     *
     * @param driverId the driver ID
     * @param status   the vehicle status
     * @return list of matching vehicles
     */
    List<Vehicle> findByDriverIdAndStatus(String driverId, VehicleStatus status);

    /**
     * Check whether a driver has at least one vehicle with the given status.
     *
     * @param driverId the driver ID
     * @param status   the vehicle status
     * @return true if such a vehicle exists
     */
    boolean existsByDriverIdAndStatus(String driverId, VehicleStatus status);
}
