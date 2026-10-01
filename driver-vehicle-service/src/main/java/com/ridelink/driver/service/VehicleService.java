package com.ridelink.driver.service;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.exception.DuplicateVehicleException;
import com.ridelink.driver.exception.DriverNotFoundException;
import com.ridelink.driver.exception.VehicleNotFoundException;
import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.model.VehicleStatus;
import com.ridelink.driver.repository.DriverRepository;
import com.ridelink.driver.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for all vehicle-related business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    // ------------------------------------------------------------------ //
    // CRUD
    // ------------------------------------------------------------------ //

    /**
     * Register a new vehicle for a driver.
     *
     * @param request the creation request
     * @return the saved vehicle as a response DTO
     * @throws DriverNotFoundException  if the referenced driver does not exist
     * @throws DuplicateVehicleException if the registration number is already taken
     */
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        log.debug("Registering vehicle with plate: {}", request.getRegistrationNumber());

        // Validate driver exists
        if (!driverRepository.existsById(request.getDriverId())) {
            throw new DriverNotFoundException(request.getDriverId());
        }

        // Prevent duplicate registration number
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateVehicleException(request.getRegistrationNumber());
        }

        Vehicle vehicle = Vehicle.builder()
                .driverId(request.getDriverId())
                .registrationNumber(request.getRegistrationNumber())
                .vehicleType(request.getVehicleType())
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .color(request.getColor())
                .capacity(request.getCapacity())
                .status(VehicleStatus.ACTIVE)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle registered with ID: {}", saved.getId());
        return toResponse(saved);
    }

    /**
     * Retrieve a single vehicle by ID.
     *
     * @param vehicleId the vehicle document ID
     * @return the vehicle response DTO
     * @throws VehicleNotFoundException if no vehicle exists with the given ID
     */
    public VehicleResponse getVehicle(String vehicleId) {
        Vehicle vehicle = findVehicleOrThrow(vehicleId);
        return toResponse(vehicle);
    }

    /**
     * Retrieve all vehicles belonging to a specific driver.
     *
     * @param driverId the driver document ID
     * @return list of vehicle response DTOs
     * @throws DriverNotFoundException if no driver exists with the given ID
     */
    public List<VehicleResponse> getVehiclesByDriver(String driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException(driverId);
        }
        return vehicleRepository.findByDriverId(driverId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing vehicle record (partial update — only non-null fields are changed).
     *
     * @param vehicleId the vehicle document ID
     * @param request   the update request
     * @return the updated vehicle response DTO
     * @throws VehicleNotFoundException  if no vehicle exists with the given ID
     * @throws DuplicateVehicleException if the new registration number is already in use
     */
    public VehicleResponse updateVehicle(String vehicleId, UpdateVehicleRequest request) {
        log.debug("Updating vehicle ID: {}", vehicleId);
        Vehicle vehicle = findVehicleOrThrow(vehicleId);

        if (request.getRegistrationNumber() != null
                && !request.getRegistrationNumber().equals(vehicle.getRegistrationNumber())
                && vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateVehicleException(request.getRegistrationNumber());
        }

        if (request.getRegistrationNumber() != null) vehicle.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getVehicleType() != null)        vehicle.setVehicleType(request.getVehicleType());
        if (request.getMake() != null)               vehicle.setMake(request.getMake());
        if (request.getModel() != null)              vehicle.setModel(request.getModel());
        if (request.getYear() != null)               vehicle.setYear(request.getYear());
        if (request.getColor() != null)              vehicle.setColor(request.getColor());
        if (request.getCapacity() != null)           vehicle.setCapacity(request.getCapacity());

        Vehicle updated = vehicleRepository.save(vehicle);
        log.info("Vehicle ID {} updated", vehicleId);
        return toResponse(updated);
    }

    // ------------------------------------------------------------------ //
    // Status management
    // ------------------------------------------------------------------ //

    /**
     * Activate a vehicle.
     *
     * @param vehicleId the vehicle document ID
     * @return the updated vehicle response DTO
     * @throws VehicleNotFoundException if no vehicle exists with the given ID
     */
    public VehicleResponse activateVehicle(String vehicleId) {
        log.debug("Activating vehicle ID: {}", vehicleId);
        Vehicle vehicle = findVehicleOrThrow(vehicleId);
        vehicle.setStatus(VehicleStatus.ACTIVE);
        Vehicle updated = vehicleRepository.save(vehicle);
        log.info("Vehicle ID {} activated", vehicleId);
        return toResponse(updated);
    }

    /**
     * Deactivate a vehicle.
     *
     * @param vehicleId the vehicle document ID
     * @return the updated vehicle response DTO
     * @throws VehicleNotFoundException if no vehicle exists with the given ID
     */
    public VehicleResponse deactivateVehicle(String vehicleId) {
        log.debug("Deactivating vehicle ID: {}", vehicleId);
        Vehicle vehicle = findVehicleOrThrow(vehicleId);
        vehicle.setStatus(VehicleStatus.INACTIVE);
        Vehicle updated = vehicleRepository.save(vehicle);
        log.info("Vehicle ID {} deactivated", vehicleId);
        return toResponse(updated);
    }

    // ------------------------------------------------------------------ //
    // Internal helpers
    // ------------------------------------------------------------------ //

    private Vehicle findVehicleOrThrow(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .driverId(vehicle.getDriverId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .vehicleType(vehicle.getVehicleType())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .color(vehicle.getColor())
                .capacity(vehicle.getCapacity())
                .status(vehicle.getStatus())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
