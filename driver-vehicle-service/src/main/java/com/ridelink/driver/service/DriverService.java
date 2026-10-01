package com.ridelink.driver.service;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.exception.DriverNotFoundException;
import com.ridelink.driver.exception.DuplicateDriverException;
import com.ridelink.driver.model.Driver;
import com.ridelink.driver.model.DriverAvailability;
import com.ridelink.driver.model.Location;
import com.ridelink.driver.model.VehicleStatus;
import com.ridelink.driver.repository.DriverRepository;
import com.ridelink.driver.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for all driver-related business logic.
 *
 * <p>Keeps controllers thin. All validation rules and domain logic live here.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    // ------------------------------------------------------------------ //
    // CRUD
    // ------------------------------------------------------------------ //

    /**
     * Register a new driver profile.
     *
     * @param request the creation request
     * @return the saved driver as a response DTO
     * @throws DuplicateDriverException if a driver with the same license number already exists
     */
    public DriverResponse createDriver(CreateDriverRequest request) {
        log.debug("Creating driver with license: {}", request.getLicenseNumber());

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateDriverException(request.getLicenseNumber());
        }

        Driver driver = Driver.builder()
                .accountId(request.getAccountId())
                .name(request.getName())
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .serviceArea(request.getServiceArea())
                .availabilityStatus(DriverAvailability.OFFLINE)
                .build();

        Driver saved = driverRepository.save(driver);
        log.info("Driver created with ID: {}", saved.getId());
        return toResponse(saved);
    }

    /**
     * Retrieve a single driver by ID.
     *
     * @param driverId the driver document ID
     * @return the driver response DTO
     * @throws DriverNotFoundException if no driver exists with the given ID
     */
    public DriverResponse getDriver(String driverId) {
        Driver driver = findDriverOrThrow(driverId);
        return toResponse(driver);
    }

    /**
     * Retrieve all drivers.
     *
     * @return list of all driver response DTOs
     */
    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing driver's profile (partial update — only non-null fields are changed).
     *
     * @param driverId the driver document ID
     * @param request  the update request
     * @return the updated driver response DTO
     * @throws DriverNotFoundException  if no driver exists with the given ID
     * @throws DuplicateDriverException if the new license number is already in use by another driver
     */
    public DriverResponse updateDriver(String driverId, UpdateDriverRequest request) {
        log.debug("Updating driver ID: {}", driverId);
        Driver driver = findDriverOrThrow(driverId);

        if (request.getLicenseNumber() != null
                && !request.getLicenseNumber().equals(driver.getLicenseNumber())
                && driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateDriverException(request.getLicenseNumber());
        }

        if (request.getName() != null)          driver.setName(request.getName());
        if (request.getPhone() != null)         driver.setPhone(request.getPhone());
        if (request.getLicenseNumber() != null) driver.setLicenseNumber(request.getLicenseNumber());
        if (request.getServiceArea() != null)   driver.setServiceArea(request.getServiceArea());
        if (request.getVehicleId() != null)     driver.setVehicleId(request.getVehicleId());

        Driver updated = driverRepository.save(driver);
        log.info("Driver ID {} updated", driverId);
        return toResponse(updated);
    }

    /**
     * Delete a driver profile.
     *
     * @param driverId the driver document ID
     * @throws DriverNotFoundException if no driver exists with the given ID
     */
    public void deleteDriver(String driverId) {
        log.debug("Deleting driver ID: {}", driverId);
        Driver driver = findDriverOrThrow(driverId);
        driverRepository.delete(driver);
        log.info("Driver ID {} deleted", driverId);
    }

    // ------------------------------------------------------------------ //
    // Availability
    // ------------------------------------------------------------------ //

    /**
     * Update the availability status of a driver.
     *
     * <p>Business rules:
     * <ul>
     *   <li>Setting to {@code AVAILABLE} requires the driver to have at least one ACTIVE vehicle.</li>
     *   <li>{@code BUSY} and {@code OFFLINE} can be set freely.</li>
     * </ul>
     *
     * @param driverId the driver document ID
     * @param request  the availability update request
     * @return the updated driver response DTO
     * @throws DriverNotFoundException if no driver exists with the given ID
     * @throws IllegalArgumentException if trying to set AVAILABLE without an active vehicle
     */
    public DriverResponse updateAvailability(String driverId, UpdateAvailabilityRequest request) {
        log.debug("Updating availability for driver ID: {} to {}", driverId, request.getStatus());
        Driver driver = findDriverOrThrow(driverId);

        if (request.getStatus() == DriverAvailability.AVAILABLE) {
            boolean hasActiveVehicle = vehicleRepository.existsByDriverIdAndStatus(driverId, VehicleStatus.ACTIVE);
            if (!hasActiveVehicle) {
                throw new IllegalArgumentException(
                        "Driver cannot be set to AVAILABLE without an ACTIVE vehicle assigned");
            }
        }

        driver.setAvailabilityStatus(request.getStatus());
        Driver updated = driverRepository.save(driver);
        log.info("Driver ID {} availability set to {}", driverId, request.getStatus());
        return toResponse(updated);
    }

    // ------------------------------------------------------------------ //
    // Location (Simulated)
    // ------------------------------------------------------------------ //

    /**
     * Update the simulated current location of a driver.
     *
     * @param driverId the driver document ID
     * @param request  the location update request
     * @return the updated driver response DTO
     * @throws DriverNotFoundException if no driver exists with the given ID
     */
    public DriverResponse updateLocation(String driverId, UpdateLocationRequest request) {
        log.debug("Updating location for driver ID: {}", driverId);
        Driver driver = findDriverOrThrow(driverId);

        driver.setCurrentLocation(Location.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build());

        Driver updated = driverRepository.save(driver);
        log.info("Driver ID {} location updated to ({}, {})",
                driverId, request.getLatitude(), request.getLongitude());
        return toResponse(updated);
    }

    /**
     * Retrieve the simulated current location of a driver.
     *
     * @param driverId the driver document ID
     * @return the current {@link Location}, or {@code null} if not yet set
     * @throws DriverNotFoundException if no driver exists with the given ID
     */
    public Location getLocation(String driverId) {
        Driver driver = findDriverOrThrow(driverId);
        return driver.getCurrentLocation();
    }

    // ------------------------------------------------------------------ //
    // Service Area
    // ------------------------------------------------------------------ //

    /**
     * Update the service area of a driver.
     *
     * @param driverId the driver document ID
     * @param request  the service area update request
     * @return the updated driver response DTO
     * @throws DriverNotFoundException if no driver exists with the given ID
     */
    public DriverResponse updateServiceArea(String driverId, UpdateServiceAreaRequest request) {
        log.debug("Updating service area for driver ID: {}", driverId);
        Driver driver = findDriverOrThrow(driverId);
        driver.setServiceArea(request.getServiceArea());
        Driver updated = driverRepository.save(driver);
        log.info("Driver ID {} service area set to {}", driverId, request.getServiceArea());
        return toResponse(updated);
    }

    // ------------------------------------------------------------------ //
    // Eligible Drivers (Integration API for Ride Management Service)
    // ------------------------------------------------------------------ //

    /**
     * Find all eligible drivers.
     *
     * <p>A driver is eligible when:
     * <ol>
     *   <li>Availability status is {@code AVAILABLE}</li>
     *   <li>Has at least one vehicle with status {@code ACTIVE}</li>
     *   <li>Service area matches (when {@code serviceArea} query param is provided)</li>
     * </ol>
     *
     * @param serviceArea optional service area filter (case-sensitive)
     * @return list of eligible driver response DTOs
     */
    public List<EligibleDriverResponse> getEligibleDrivers(String serviceArea) {
        log.debug("Finding eligible drivers, serviceArea={}", serviceArea);

        List<Driver> candidates;
        if (serviceArea != null && !serviceArea.isBlank()) {
            candidates = driverRepository.findByAvailabilityStatusAndServiceArea(
                    DriverAvailability.AVAILABLE, serviceArea);
        } else {
            candidates = driverRepository.findByAvailabilityStatus(DriverAvailability.AVAILABLE);
        }

        return candidates.stream()
                .filter(d -> vehicleRepository.existsByDriverIdAndStatus(d.getId(), VehicleStatus.ACTIVE))
                .map(d -> {
                    // Retrieve the vehicle type from the first active vehicle
                    String vehicleType = vehicleRepository
                            .findByDriverIdAndStatus(d.getId(), VehicleStatus.ACTIVE)
                            .stream()
                            .findFirst()
                            .map(v -> v.getVehicleType())
                            .orElse("UNKNOWN");

                    return EligibleDriverResponse.builder()
                            .driverId(d.getId())
                            .name(d.getName())
                            .vehicleId(d.getVehicleId())
                            .vehicleType(vehicleType)
                            .serviceArea(d.getServiceArea())
                            .availabilityStatus(d.getAvailabilityStatus())
                            .currentLocation(d.getCurrentLocation())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------ //
    // Internal helpers
    // ------------------------------------------------------------------ //

    private Driver findDriverOrThrow(String driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
    }

    private DriverResponse toResponse(Driver driver) {
        return DriverResponse.builder()
                .id(driver.getId())
                .accountId(driver.getAccountId())
                .name(driver.getName())
                .phone(driver.getPhone())
                .licenseNumber(driver.getLicenseNumber())
                .availabilityStatus(driver.getAvailabilityStatus())
                .serviceArea(driver.getServiceArea())
                .currentLocation(driver.getCurrentLocation())
                .vehicleId(driver.getVehicleId())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}
