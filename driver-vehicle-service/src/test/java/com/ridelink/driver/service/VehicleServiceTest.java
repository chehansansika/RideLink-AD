package com.ridelink.driver.service;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.exception.DriverNotFoundException;
import com.ridelink.driver.exception.DuplicateVehicleException;
import com.ridelink.driver.exception.VehicleNotFoundException;
import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.model.VehicleStatus;
import com.ridelink.driver.repository.DriverRepository;
import com.ridelink.driver.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link VehicleService}.
 *
 * <p>Repositories are mocked with Mockito. No real MongoDB connection required.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Unit Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private VehicleService vehicleService;

    // ------------------------------------------------------------------ //
    // Fixtures
    // ------------------------------------------------------------------ //

    private Vehicle buildVehicle() {
        return Vehicle.builder()
                .id("VEH001")
                .driverId("DRV001")
                .registrationNumber("CAR-1234")
                .vehicleType("CAR")
                .make("Toyota")
                .model("Prius")
                .year(2022)
                .color("White")
                .capacity(4)
                .status(VehicleStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CreateVehicleRequest buildCreateRequest() {
        return CreateVehicleRequest.builder()
                .driverId("DRV001")
                .registrationNumber("CAR-1234")
                .vehicleType("CAR")
                .make("Toyota")
                .model("Prius")
                .year(2022)
                .color("White")
                .capacity(4)
                .build();
    }

    // ------------------------------------------------------------------ //
    // Create Vehicle
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("createVehicle")
    class CreateVehicle {

        @Test
        @DisplayName("should register vehicle and link to driver when driver has no vehicle")
        void shouldCreateVehicle() {
            CreateVehicleRequest request = buildCreateRequest();
            Vehicle saved = buildVehicle();
            com.ridelink.driver.model.Driver driver = com.ridelink.driver.model.Driver.builder()
                    .id("DRV001")
                    .vehicleId(null)
                    .build();

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(vehicleRepository.existsByRegistrationNumber("CAR-1234")).thenReturn(false);
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(saved);

            VehicleResponse response = vehicleService.createVehicle(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("VEH001");
            assertThat(response.getRegistrationNumber()).isEqualTo("CAR-1234");
            assertThat(response.getStatus()).isEqualTo(VehicleStatus.ACTIVE);
            assertThat(driver.getVehicleId()).isEqualTo("VEH001");
            verify(driverRepository).save(driver);
        }

        @Test
        @DisplayName("should throw DuplicateVehicleException when registration number already exists")
        void shouldThrowDuplicateVehicleException() {
            CreateVehicleRequest request = buildCreateRequest();
            com.ridelink.driver.model.Driver driver = com.ridelink.driver.model.Driver.builder().id("DRV001").build();

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(vehicleRepository.existsByRegistrationNumber("CAR-1234")).thenReturn(true);

            assertThatThrownBy(() -> vehicleService.createVehicle(request))
                    .isInstanceOf(DuplicateVehicleException.class)
                    .hasMessageContaining("CAR-1234");

            verify(vehicleRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw DriverNotFoundException when driver does not exist")
        void shouldThrowDriverNotFoundException() {
            CreateVehicleRequest request = buildCreateRequest();

            when(driverRepository.findById("DRV001")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vehicleService.createVehicle(request))
                    .isInstanceOf(DriverNotFoundException.class);
        }
    }

    // ------------------------------------------------------------------ //
    // Get Vehicle
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("getVehicle")
    class GetVehicle {

        @Test
        @DisplayName("should return vehicle when found")
        void shouldReturnVehicle() {
            when(vehicleRepository.findById("VEH001")).thenReturn(Optional.of(buildVehicle()));

            VehicleResponse response = vehicleService.getVehicle("VEH001");

            assertThat(response.getId()).isEqualTo("VEH001");
            assertThat(response.getMake()).isEqualTo("Toyota");
        }

        @Test
        @DisplayName("should throw VehicleNotFoundException when vehicle not found")
        void shouldThrowVehicleNotFoundException() {
            when(vehicleRepository.findById("NONE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vehicleService.getVehicle("NONE"))
                    .isInstanceOf(VehicleNotFoundException.class)
                    .hasMessageContaining("NONE");
        }
    }

    // ------------------------------------------------------------------ //
    // Update Vehicle
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("updateVehicle")
    class UpdateVehicle {

        @Test
        @DisplayName("should update vehicle fields successfully")
        void shouldUpdateVehicle() {
            Vehicle vehicle = buildVehicle();
            UpdateVehicleRequest request = UpdateVehicleRequest.builder()
                    .color("Blue")
                    .capacity(5)
                    .build();

            Vehicle updated = buildVehicle();
            updated.setColor("Blue");
            updated.setCapacity(5);

            when(vehicleRepository.findById("VEH001")).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updated);

            VehicleResponse response = vehicleService.updateVehicle("VEH001", request);

            assertThat(response.getColor()).isEqualTo("Blue");
            assertThat(response.getCapacity()).isEqualTo(5);
        }

        @Test
        @DisplayName("should throw DuplicateVehicleException when new registration already in use")
        void shouldThrowDuplicateWhenRegistrationConflict() {
            Vehicle vehicle = buildVehicle();
            UpdateVehicleRequest request = UpdateVehicleRequest.builder()
                    .registrationNumber("NEW-9999")
                    .build();

            when(vehicleRepository.findById("VEH001")).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.existsByRegistrationNumber("NEW-9999")).thenReturn(true);

            assertThatThrownBy(() -> vehicleService.updateVehicle("VEH001", request))
                    .isInstanceOf(DuplicateVehicleException.class);
        }
    }

    // ------------------------------------------------------------------ //
    // Status Management
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("activateVehicle / deactivateVehicle")
    class StatusManagement {

        @Test
        @DisplayName("should activate an inactive vehicle")
        void shouldActivateVehicle() {
            Vehicle vehicle = buildVehicle();
            vehicle.setStatus(VehicleStatus.INACTIVE);

            Vehicle activated = buildVehicle();
            activated.setStatus(VehicleStatus.ACTIVE);

            when(vehicleRepository.findById("VEH001")).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.save(any())).thenReturn(activated);

            VehicleResponse response = vehicleService.activateVehicle("VEH001");
            assertThat(response.getStatus()).isEqualTo(VehicleStatus.ACTIVE);
        }

        @Test
        @DisplayName("should deactivate an active vehicle and set driver offline if no active vehicles remain")
        void shouldDeactivateVehicleAndSetDriverOffline() {
            Vehicle vehicle = buildVehicle();
            Vehicle deactivated = buildVehicle();
            deactivated.setStatus(VehicleStatus.INACTIVE);

            com.ridelink.driver.model.Driver driver = com.ridelink.driver.model.Driver.builder()
                    .id("DRV001")
                    .availabilityStatus(com.ridelink.driver.model.DriverAvailability.AVAILABLE)
                    .build();

            when(vehicleRepository.findById("VEH001")).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.save(any())).thenReturn(deactivated);
            when(vehicleRepository.existsByDriverIdAndStatus("DRV001", VehicleStatus.ACTIVE)).thenReturn(false);
            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));

            VehicleResponse response = vehicleService.deactivateVehicle("VEH001");
            assertThat(response.getStatus()).isEqualTo(VehicleStatus.INACTIVE);
            assertThat(driver.getAvailabilityStatus()).isEqualTo(com.ridelink.driver.model.DriverAvailability.OFFLINE);
            verify(driverRepository).save(driver);
        }

        @Test
        @DisplayName("should throw VehicleNotFoundException when vehicle not found for activation")
        void shouldThrowForActivateNotFound() {
            when(vehicleRepository.findById("NONE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vehicleService.activateVehicle("NONE"))
                    .isInstanceOf(VehicleNotFoundException.class);
        }
    }

    // ------------------------------------------------------------------ //
    // Delete Vehicle
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("deleteVehicle")
    class DeleteVehicle {

        @Test
        @DisplayName("should delete vehicle and update driver reference")
        void shouldDeleteVehicle() {
            Vehicle vehicle = buildVehicle();
            com.ridelink.driver.model.Driver driver = com.ridelink.driver.model.Driver.builder()
                    .id("DRV001")
                    .vehicleId("VEH001")
                    .availabilityStatus(com.ridelink.driver.model.DriverAvailability.AVAILABLE)
                    .build();

            when(vehicleRepository.findById("VEH001")).thenReturn(Optional.of(vehicle));
            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(vehicleRepository.existsByDriverIdAndStatus("DRV001", VehicleStatus.ACTIVE)).thenReturn(false);
            when(vehicleRepository.findByDriverId("DRV001")).thenReturn(List.of());

            vehicleService.deleteVehicle("VEH001");

            verify(vehicleRepository).delete(vehicle);
            assertThat(driver.getVehicleId()).isNull();
            assertThat(driver.getAvailabilityStatus()).isEqualTo(com.ridelink.driver.model.DriverAvailability.OFFLINE);
            verify(driverRepository).save(driver);
        }

        @Test
        @DisplayName("should throw VehicleNotFoundException when deleting non-existent vehicle")
        void shouldThrowWhenDeletingNonExistentVehicle() {
            when(vehicleRepository.findById("NONE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vehicleService.deleteVehicle("NONE"))
                    .isInstanceOf(VehicleNotFoundException.class);
        }
    }

    // ------------------------------------------------------------------ //
    // Get vehicles by driver
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("getVehiclesByDriver")
    class GetVehiclesByDriver {

        @Test
        @DisplayName("should return vehicles for a driver")
        void shouldReturnVehicles() {
            when(driverRepository.existsById("DRV001")).thenReturn(true);
            when(vehicleRepository.findByDriverId("DRV001")).thenReturn(List.of(buildVehicle()));

            List<VehicleResponse> responses = vehicleService.getVehiclesByDriver("DRV001");
            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("should throw DriverNotFoundException for unknown driver")
        void shouldThrowForUnknownDriver() {
            when(driverRepository.existsById("NONE")).thenReturn(false);

            assertThatThrownBy(() -> vehicleService.getVehiclesByDriver("NONE"))
                    .isInstanceOf(DriverNotFoundException.class);
        }
    }
}

