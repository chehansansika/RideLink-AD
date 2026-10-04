package com.ridelink.driver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.driver.dto.CreateVehicleRequest;
import com.ridelink.driver.dto.UpdateVehicleRequest;
import com.ridelink.driver.dto.VehicleResponse;
import com.ridelink.driver.exception.DriverNotFoundException;
import com.ridelink.driver.exception.DuplicateVehicleException;
import com.ridelink.driver.exception.VehicleNotFoundException;
import com.ridelink.driver.model.VehicleStatus;
import com.ridelink.driver.service.VehicleService;
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

@WebMvcTest(VehicleController.class)
@DisplayName("VehicleController Integration Tests")
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    private VehicleResponse sampleResponse() {
        return VehicleResponse.builder()
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

    @Test
    @DisplayName("POST /api/vehicles - should register vehicle and return 201")
    void createVehicle_Success() throws Exception {
        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .driverId("DRV001")
                .registrationNumber("CAR-1234")
                .vehicleType("CAR")
                .make("Toyota")
                .model("Prius")
                .year(2022)
                .color("White")
                .capacity(4)
                .build();

        when(vehicleService.createVehicle(any(CreateVehicleRequest.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("VEH001"))
                .andExpect(jsonPath("$.registrationNumber").value("CAR-1234"));
    }

    @Test
    @DisplayName("POST /api/vehicles - driver not found should return 404")
    void createVehicle_DriverNotFound() throws Exception {
        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .driverId("NONEXISTENT")
                .registrationNumber("CAR-1234")
                .vehicleType("CAR")
                .make("Toyota")
                .model("Prius")
                .year(2022)
                .color("White")
                .capacity(4)
                .build();

        when(vehicleService.createVehicle(any())).thenThrow(new DriverNotFoundException("NONEXISTENT"));

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/vehicles - duplicate registration should return 409")
    void createVehicle_Duplicate() throws Exception {
        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .driverId("DRV001")
                .registrationNumber("CAR-1234")
                .vehicleType("CAR")
                .make("Toyota")
                .model("Prius")
                .year(2022)
                .color("White")
                .capacity(4)
                .build();

        when(vehicleService.createVehicle(any())).thenThrow(new DuplicateVehicleException("CAR-1234"));

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("GET /api/vehicles/{id} - found should return 200")
    void getVehicle_Success() throws Exception {
        when(vehicleService.getVehicle("VEH001")).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/vehicles/VEH001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("VEH001"));
    }

    @Test
    @DisplayName("GET /api/vehicles/{id} - not found should return 404")
    void getVehicle_NotFound() throws Exception {
        when(vehicleService.getVehicle("NONE")).thenThrow(new VehicleNotFoundException("NONE"));

        mockMvc.perform(get("/api/vehicles/NONE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/drivers/{driverId}/vehicles - should return driver vehicles")
    void getVehiclesByDriver_Success() throws Exception {
        when(vehicleService.getVehiclesByDriver("DRV001")).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/drivers/DRV001/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("VEH001"));
    }

    @Test
    @DisplayName("PUT /api/vehicles/{id} - should return 200")
    void updateVehicle_Success() throws Exception {
        UpdateVehicleRequest request = UpdateVehicleRequest.builder().color("Black").build();
        VehicleResponse updated = sampleResponse();
        updated.setColor("Black");

        when(vehicleService.updateVehicle(eq("VEH001"), any(UpdateVehicleRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/vehicles/VEH001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Black"));
    }

    @Test
    @DisplayName("PATCH /api/vehicles/{id}/activate - should return 200")
    void activateVehicle_Success() throws Exception {
        when(vehicleService.activateVehicle("VEH001")).thenReturn(sampleResponse());

        mockMvc.perform(patch("/api/vehicles/VEH001/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("PATCH /api/vehicles/{id}/deactivate - should return 200")
    void deactivateVehicle_Success() throws Exception {
        VehicleResponse deactivated = sampleResponse();
        deactivated.setStatus(VehicleStatus.INACTIVE);

        when(vehicleService.deactivateVehicle("VEH001")).thenReturn(deactivated);

        mockMvc.perform(patch("/api/vehicles/VEH001/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("DELETE /api/vehicles/{id} - should return 204")
    void deleteVehicle_Success() throws Exception {
        doNothing().when(vehicleService).deleteVehicle("VEH001");

        mockMvc.perform(delete("/api/vehicles/VEH001"))
                .andExpect(status().isNoContent());
    }
}
