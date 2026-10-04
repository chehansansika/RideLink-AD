package com.ridelink.driver.service;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.exception.DriverNotFoundException;
import com.ridelink.driver.exception.DuplicateDriverException;
import com.ridelink.driver.model.Driver;
import com.ridelink.driver.model.DriverAvailability;
import com.ridelink.driver.model.Location;
import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.model.VehicleStatus;
import com.ridelink.driver.repository.DriverRepository;
import com.ridelink.driver.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
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
 * Unit tests for {@link DriverService}.
 *
 * <p>Repositories are mocked with Mockito. No real MongoDB connection required.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DriverService Unit Tests")
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DriverService driverService;

    // ------------------------------------------------------------------ //
    // Fixtures
    // ------------------------------------------------------------------ //

    private Driver buildDriver() {
        return Driver.builder()
                .id("DRV001")
                .accountId("ACC001")
                .name("John Silva")
                .phone("+94771234567")
                .licenseNumber("LIC-12345")
                .availabilityStatus(DriverAvailability.OFFLINE)
                .serviceArea("Colombo")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CreateDriverRequest buildCreateRequest() {
        return CreateDriverRequest.builder()
                .accountId("ACC001")
                .name("John Silva")
                .phone("+94771234567")
                .licenseNumber("LIC-12345")
                .serviceArea("Colombo")
                .build();
    }

    // ------------------------------------------------------------------ //
    // Create Driver
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("createDriver")
    class CreateDriver {

        @Test
        @DisplayName("should create driver successfully when license is unique")
        void shouldCreateDriver() {
            CreateDriverRequest request = buildCreateRequest();
            Driver saved = buildDriver();

            when(driverRepository.existsByLicenseNumber("LIC-12345")).thenReturn(false);
            when(driverRepository.save(any(Driver.class))).thenReturn(saved);

            DriverResponse response = driverService.createDriver(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("DRV001");
            assertThat(response.getName()).isEqualTo("John Silva");
            assertThat(response.getLicenseNumber()).isEqualTo("LIC-12345");
            assertThat(response.getAvailabilityStatus()).isEqualTo(DriverAvailability.OFFLINE);

            verify(driverRepository).save(any(Driver.class));
        }

        @Test
        @DisplayName("should throw DuplicateDriverException when license already exists")
        void shouldThrowDuplicateDriverException() {
            CreateDriverRequest request = buildCreateRequest();

            when(driverRepository.existsByLicenseNumber("LIC-12345")).thenReturn(true);

            assertThatThrownBy(() -> driverService.createDriver(request))
                    .isInstanceOf(DuplicateDriverException.class)
                    .hasMessageContaining("LIC-12345");

            verify(driverRepository, never()).save(any());
        }
    }

    // ------------------------------------------------------------------ //
    // Get Driver
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("getDriver")
    class GetDriver {

        @Test
        @DisplayName("should return driver when found")
        void shouldReturnDriver() {
            Driver driver = buildDriver();
            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));

            DriverResponse response = driverService.getDriver("DRV001");

            assertThat(response.getId()).isEqualTo("DRV001");
            assertThat(response.getName()).isEqualTo("John Silva");
        }

        @Test
        @DisplayName("should return driver by account ID when found")
        void shouldReturnDriverByAccountId() {
            Driver driver = buildDriver();
            when(driverRepository.findByAccountId("ACC001")).thenReturn(Optional.of(driver));

            DriverResponse response = driverService.getDriverByAccountId("ACC001");

            assertThat(response.getId()).isEqualTo("DRV001");
            assertThat(response.getAccountId()).isEqualTo("ACC001");
        }

        @Test
        @DisplayName("should throw DriverNotFoundException when account ID not found")
        void shouldThrowWhenAccountIdNotFound() {
            when(driverRepository.findByAccountId("UNKNOWN_ACC")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> driverService.getDriverByAccountId("UNKNOWN_ACC"))
                    .isInstanceOf(DriverNotFoundException.class);
        }

        @Test
        @DisplayName("should return all drivers")
        void shouldReturnAllDrivers() {
            when(driverRepository.findAll()).thenReturn(List.of(buildDriver()));

            List<DriverResponse> responses = driverService.getAllDrivers();

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getId()).isEqualTo("DRV001");
        }
    }

    // ------------------------------------------------------------------ //
    // Update Driver
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("updateDriver")
    class UpdateDriver {

        @Test
        @DisplayName("should update driver fields successfully")
        void shouldUpdateDriver() {
            Driver driver = buildDriver();
            UpdateDriverRequest request = UpdateDriverRequest.builder()
                    .name("John Updated")
                    .phone("+94779999999")
                    .build();

            Driver updated = buildDriver();
            updated.setName("John Updated");
            updated.setPhone("+94779999999");

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(driverRepository.save(any(Driver.class))).thenReturn(updated);

            DriverResponse response = driverService.updateDriver("DRV001", request);

            assertThat(response.getName()).isEqualTo("John Updated");
            assertThat(response.getPhone()).isEqualTo("+94779999999");
        }

        @Test
        @DisplayName("should throw DuplicateDriverException when new license already used by another driver")
        void shouldThrowDuplicateWhenLicenseConflict() {
            Driver driver = buildDriver();
            UpdateDriverRequest request = UpdateDriverRequest.builder()
                    .licenseNumber("LIC-99999")
                    .build();

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(driverRepository.existsByLicenseNumber("LIC-99999")).thenReturn(true);

            assertThatThrownBy(() -> driverService.updateDriver("DRV001", request))
                    .isInstanceOf(DuplicateDriverException.class);
        }

        @Test
        @DisplayName("should throw VehicleNotFoundException when updating with non-existent vehicleId")
        void shouldThrowWhenVehicleIdNotFound() {
            Driver driver = buildDriver();
            UpdateDriverRequest request = UpdateDriverRequest.builder()
                    .vehicleId("NONEXISTENT_VEH")
                    .build();

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(vehicleRepository.existsById("NONEXISTENT_VEH")).thenReturn(false);

            assertThatThrownBy(() -> driverService.updateDriver("DRV001", request))
                    .isInstanceOf(com.ridelink.driver.exception.VehicleNotFoundException.class);
        }
    }

    // ------------------------------------------------------------------ //
    // Delete Driver
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("deleteDriver")
    class DeleteDriver {

        @Test
        @DisplayName("should delete driver and cascade delete associated vehicles")
        void shouldDeleteDriverAndCascadeVehicles() {
            Driver driver = buildDriver();
            Vehicle vehicle = Vehicle.builder().id("VEH001").driverId("DRV001").build();

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(vehicleRepository.findByDriverId("DRV001")).thenReturn(List.of(vehicle));

            driverService.deleteDriver("DRV001");

            verify(vehicleRepository).deleteAll(List.of(vehicle));
            verify(driverRepository).delete(driver);
        }

        @Test
        @DisplayName("should throw DriverNotFoundException when deleting non-existent driver")
        void shouldThrowWhenDeletingNonExistentDriver() {
            when(driverRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> driverService.deleteDriver("UNKNOWN"))
                    .isInstanceOf(DriverNotFoundException.class);
        }
    }


    // ------------------------------------------------------------------ //
    // Availability
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("updateAvailability")
    class UpdateAvailability {

        @Test
        @DisplayName("should set AVAILABLE when driver has active vehicle")
        void shouldSetAvailable() {
            Driver driver = buildDriver();
            UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(DriverAvailability.AVAILABLE);

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(vehicleRepository.existsByDriverIdAndStatus("DRV001", VehicleStatus.ACTIVE)).thenReturn(true);

            Driver availableDriver = buildDriver();
            availableDriver.setAvailabilityStatus(DriverAvailability.AVAILABLE);
            when(driverRepository.save(any())).thenReturn(availableDriver);

            DriverResponse response = driverService.updateAvailability("DRV001", request);
            assertThat(response.getAvailabilityStatus()).isEqualTo(DriverAvailability.AVAILABLE);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when setting AVAILABLE with no active vehicle")
        void shouldRejectAvailableWithNoActiveVehicle() {
            Driver driver = buildDriver();
            UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(DriverAvailability.AVAILABLE);

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(vehicleRepository.existsByDriverIdAndStatus("DRV001", VehicleStatus.ACTIVE)).thenReturn(false);

            assertThatThrownBy(() -> driverService.updateAvailability("DRV001", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ACTIVE vehicle");
        }

        @Test
        @DisplayName("should set BUSY without checking vehicles")
        void shouldSetBusy() {
            Driver driver = buildDriver();
            UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(DriverAvailability.BUSY);

            Driver busyDriver = buildDriver();
            busyDriver.setAvailabilityStatus(DriverAvailability.BUSY);

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(driverRepository.save(any())).thenReturn(busyDriver);

            DriverResponse response = driverService.updateAvailability("DRV001", request);
            assertThat(response.getAvailabilityStatus()).isEqualTo(DriverAvailability.BUSY);
            verify(vehicleRepository, never()).existsByDriverIdAndStatus(any(), any());
        }

        @Test
        @DisplayName("should set OFFLINE without checking vehicles")
        void shouldSetOffline() {
            Driver driver = buildDriver();
            driver.setAvailabilityStatus(DriverAvailability.AVAILABLE);
            UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(DriverAvailability.OFFLINE);

            Driver offlineDriver = buildDriver();
            offlineDriver.setAvailabilityStatus(DriverAvailability.OFFLINE);

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(driverRepository.save(any())).thenReturn(offlineDriver);

            DriverResponse response = driverService.updateAvailability("DRV001", request);
            assertThat(response.getAvailabilityStatus()).isEqualTo(DriverAvailability.OFFLINE);
            verify(vehicleRepository, never()).existsByDriverIdAndStatus(any(), any());
        }
    }

    // ------------------------------------------------------------------ //
    // Location
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("updateLocation / getLocation")
    class LocationTests {

        @Test
        @DisplayName("should update location with valid coordinates")
        void shouldUpdateLocation() {
            Driver driver = buildDriver();
            UpdateLocationRequest request = new UpdateLocationRequest(6.9271, 79.8612);

            Driver updatedDriver = buildDriver();
            updatedDriver.setCurrentLocation(new Location(6.9271, 79.8612));

            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));
            when(driverRepository.save(any())).thenReturn(updatedDriver);

            DriverResponse response = driverService.updateLocation("DRV001", request);
            assertThat(response.getCurrentLocation()).isNotNull();
            assertThat(response.getCurrentLocation().getLatitude()).isEqualTo(6.9271);
            assertThat(response.getCurrentLocation().getLongitude()).isEqualTo(79.8612);
        }

        @Test
        @DisplayName("should return location for existing driver")
        void shouldGetLocation() {
            Driver driver = buildDriver();
            driver.setCurrentLocation(new Location(6.9271, 79.8612));
            when(driverRepository.findById("DRV001")).thenReturn(Optional.of(driver));

            Location location = driverService.getLocation("DRV001");
            assertThat(location).isNotNull();
            assertThat(location.getLatitude()).isEqualTo(6.9271);
        }

        @Test
        @DisplayName("should throw DriverNotFoundException for unknown driver")
        void shouldThrowForUnknownDriver() {
            when(driverRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> driverService.getLocation("UNKNOWN"))
                    .isInstanceOf(DriverNotFoundException.class);
        }
    }

    // ------------------------------------------------------------------ //
    // Eligible Drivers
    // ------------------------------------------------------------------ //

    @Nested
    @DisplayName("getEligibleDrivers")
    class EligibleDrivers {

        private Driver buildAvailableDriver(String id) {
            Driver d = buildDriver();
            d.setId(id);
            d.setAvailabilityStatus(DriverAvailability.AVAILABLE);
            d.setVehicleId("VEH001");
            return d;
        }

        private Vehicle buildActiveVehicle(String driverId) {
            return Vehicle.builder()
                    .id("VEH001")
                    .driverId(driverId)
                    .vehicleType("CAR")
                    .status(VehicleStatus.ACTIVE)
                    .build();
        }

        @Test
        @DisplayName("should return available driver with active vehicle")
        void shouldReturnEligibleDriver() {
            Driver driver = buildAvailableDriver("DRV001");
            when(driverRepository.findByAvailabilityStatus(DriverAvailability.AVAILABLE))
                    .thenReturn(List.of(driver));
            when(vehicleRepository.existsByDriverIdAndStatus("DRV001", VehicleStatus.ACTIVE))
                    .thenReturn(true);
            when(vehicleRepository.findByDriverIdAndStatus("DRV001", VehicleStatus.ACTIVE))
                    .thenReturn(List.of(buildActiveVehicle("DRV001")));

            List<EligibleDriverResponse> result = driverService.getEligibleDrivers(null);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDriverId()).isEqualTo("DRV001");
        }

        @Test
        @DisplayName("should exclude BUSY drivers")
        void shouldExcludeBusyDrivers() {
            when(driverRepository.findByAvailabilityStatus(DriverAvailability.AVAILABLE))
                    .thenReturn(List.of());

            List<EligibleDriverResponse> result = driverService.getEligibleDrivers(null);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should exclude OFFLINE drivers")
        void shouldExcludeOfflineDrivers() {
            when(driverRepository.findByAvailabilityStatus(DriverAvailability.AVAILABLE))
                    .thenReturn(List.of());

            List<EligibleDriverResponse> result = driverService.getEligibleDrivers(null);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should exclude driver without active vehicle")
        void shouldExcludeDriverWithNoActiveVehicle() {
            Driver driver = buildAvailableDriver("DRV001");
            when(driverRepository.findByAvailabilityStatus(DriverAvailability.AVAILABLE))
                    .thenReturn(List.of(driver));
            when(vehicleRepository.existsByDriverIdAndStatus("DRV001", VehicleStatus.ACTIVE))
                    .thenReturn(false);

            List<EligibleDriverResponse> result = driverService.getEligibleDrivers(null);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should filter by service area")
        void shouldFilterByServiceArea() {
            Driver colomboDriver = buildAvailableDriver("DRV001");
            colomboDriver.setServiceArea("Colombo");

            when(driverRepository.findByAvailabilityStatusAndServiceArea(
                    DriverAvailability.AVAILABLE, "Colombo"))
                    .thenReturn(List.of(colomboDriver));
            when(vehicleRepository.existsByDriverIdAndStatus("DRV001", VehicleStatus.ACTIVE))
                    .thenReturn(true);
            when(vehicleRepository.findByDriverIdAndStatus("DRV001", VehicleStatus.ACTIVE))
                    .thenReturn(List.of(buildActiveVehicle("DRV001")));

            List<EligibleDriverResponse> result = driverService.getEligibleDrivers("Colombo");
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getServiceArea()).isEqualTo("Colombo");
        }

        @Test
        @DisplayName("should return empty list when no eligible drivers")
        void shouldReturnEmptyWhenNone() {
            when(driverRepository.findByAvailabilityStatus(DriverAvailability.AVAILABLE))
                    .thenReturn(List.of());

            List<EligibleDriverResponse> result = driverService.getEligibleDrivers(null);
            assertThat(result).isEmpty();
        }
    }
}
