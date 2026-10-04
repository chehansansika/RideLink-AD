package com.ridelink.driver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.driver.dto.*;
import com.ridelink.driver.exception.DriverNotFoundException;
import com.ridelink.driver.exception.DuplicateDriverException;
import com.ridelink.driver.model.DriverAvailability;
import com.ridelink.driver.model.Location;
import com.ridelink.driver.service.DriverService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
@DisplayName("DriverController Integration Tests")
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

    private DriverResponse sampleResponse() {
        return DriverResponse.builder()
                .id("DRV001")
                .accountId("ACC001")
                .name("John Silva")
                .phone("+94771234567")
                .licenseNumber("LIC-12345")
                .availabilityStatus(DriverAvailability.OFFLINE)
                .serviceArea("Colombo")
                .currentLocation(new Location(6.9271, 79.8612))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/drivers - should create driver and return 201")
    void createDriver_Success() throws Exception {
        CreateDriverRequest request = CreateDriverRequest.builder()
                .accountId("ACC001")
                .name("John Silva")
                .phone("+94771234567")
                .licenseNumber("LIC-12345")
                .serviceArea("Colombo")
                .build();

        when(driverService.createDriver(any(CreateDriverRequest.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("DRV001"))
                .andExpect(jsonPath("$.name").value("John Silva"));
    }

    @Test
    @DisplayName("POST /api/drivers - validation failure should return 400")
    void createDriver_ValidationError() throws Exception {
        CreateDriverRequest invalidRequest = CreateDriverRequest.builder()
                .name("")
                .phone("invalid")
                .licenseNumber("")
                .serviceArea("")
                .build();

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isMap());
    }

    @Test
    @DisplayName("POST /api/drivers - duplicate license should return 409")
    void createDriver_Duplicate() throws Exception {
        CreateDriverRequest request = CreateDriverRequest.builder()
                .accountId("ACC001")
                .name("John Silva")
                .phone("+94771234567")
                .licenseNumber("LIC-12345")
                .serviceArea("Colombo")
                .build();

        when(driverService.createDriver(any())).thenThrow(new DuplicateDriverException("LIC-12345"));

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("GET /api/drivers/{id} - found should return 200")
    void getDriver_Success() throws Exception {
        when(driverService.getDriver("DRV001")).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/drivers/DRV001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("DRV001"));
    }

    @Test
    @DisplayName("GET /api/drivers/{id} - not found should return 404")
    void getDriver_NotFound() throws Exception {
        when(driverService.getDriver("NONEXISTENT")).thenThrow(new DriverNotFoundException("NONEXISTENT"));

        mockMvc.perform(get("/api/drivers/NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/drivers/account/{accountId} - should return 200")
    void getDriverByAccountId_Success() throws Exception {
        when(driverService.getDriverByAccountId("ACC001")).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/drivers/account/ACC001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("ACC001"));
    }

    @Test
    @DisplayName("GET /api/drivers - should return list")
    void getAllDrivers() throws Exception {
        when(driverService.getAllDrivers()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("DRV001"));
    }

    @Test
    @DisplayName("PUT /api/drivers/{id} - update should return 200")
    void updateDriver_Success() throws Exception {
        UpdateDriverRequest request = UpdateDriverRequest.builder().name("John Updated").build();
        DriverResponse updated = sampleResponse();
        updated.setName("John Updated");

        when(driverService.updateDriver(eq("DRV001"), any(UpdateDriverRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/drivers/DRV001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));
    }

    @Test
    @DisplayName("DELETE /api/drivers/{id} - should return 204")
    void deleteDriver_Success() throws Exception {
        doNothing().when(driverService).deleteDriver("DRV001");

        mockMvc.perform(delete("/api/drivers/DRV001"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/drivers/{id}/availability - should return 200")
    void updateAvailability_Success() throws Exception {
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(DriverAvailability.AVAILABLE);
        DriverResponse resp = sampleResponse();
        resp.setAvailabilityStatus(DriverAvailability.AVAILABLE);

        when(driverService.updateAvailability(eq("DRV001"), any())).thenReturn(resp);

        mockMvc.perform(patch("/api/drivers/DRV001/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availabilityStatus").value("AVAILABLE"));
    }

    @Test
    @DisplayName("PATCH /api/drivers/{id}/location - should return 200")
    void updateLocation_Success() throws Exception {
        UpdateLocationRequest request = new UpdateLocationRequest(6.9271, 79.8612);
        when(driverService.updateLocation(eq("DRV001"), any())).thenReturn(sampleResponse());

        mockMvc.perform(patch("/api/drivers/DRV001/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentLocation.latitude").value(6.9271));
    }

    @Test
    @DisplayName("GET /api/drivers/{id}/location - should return 200")
    void getLocation_Success() throws Exception {
        when(driverService.getLocation("DRV001")).thenReturn(new Location(6.9271, 79.8612));

        mockMvc.perform(get("/api/drivers/DRV001/location"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(6.9271));
    }

    @Test
    @DisplayName("PATCH /api/drivers/{id}/service-area - should return 200")
    void updateServiceArea_Success() throws Exception {
        UpdateServiceAreaRequest request = new UpdateServiceAreaRequest("Kandy");
        DriverResponse resp = sampleResponse();
        resp.setServiceArea("Kandy");

        when(driverService.updateServiceArea(eq("DRV001"), any())).thenReturn(resp);

        mockMvc.perform(patch("/api/drivers/DRV001/service-area")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceArea").value("Kandy"));
    }

    @Test
    @DisplayName("GET /api/drivers/eligible - should return eligible list")
    void getEligibleDrivers_Success() throws Exception {
        EligibleDriverResponse eligible = EligibleDriverResponse.builder()
                .driverId("DRV001")
                .name("John Silva")
                .vehicleId("VEH001")
                .vehicleType("CAR")
                .serviceArea("Colombo")
                .availabilityStatus(DriverAvailability.AVAILABLE)
                .build();

        when(driverService.getEligibleDrivers("Colombo")).thenReturn(List.of(eligible));

        mockMvc.perform(get("/api/drivers/eligible?serviceArea=Colombo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].driverId").value("DRV001"))
                .andExpect(jsonPath("$[0].vehicleType").value("CAR"));
    }
}
