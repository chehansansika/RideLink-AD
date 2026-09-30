package com.ridelink.farepayment.service;

import com.ridelink.farepayment.config.FareProperties;
import com.ridelink.farepayment.dto.request.FareEstimateRequest;
import com.ridelink.farepayment.dto.request.FinalFareCalculateRequest;
import com.ridelink.farepayment.dto.response.FareCalculationResponse;
import com.ridelink.farepayment.dto.response.FareEstimateResponse;
import com.ridelink.farepayment.exception.BadRequestException;
import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.repository.FareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareServiceTest {

    @Mock
    private FareRepository fareRepository;

    @Mock
    private FareProperties fareProperties;

    @InjectMocks
    private FareService fareService;

    @BeforeEach
    void setUp() {
        lenient().when(fareProperties.getBaseFare()).thenReturn(100.0);
        lenient().when(fareProperties.getRatePerKm()).thenReturn(40.0);
        lenient().when(fareProperties.getServiceFee()).thenReturn(25.0);
        lenient().when(fareProperties.getCurrency()).thenReturn("LKR");
    }

    @Test
    @DisplayName("Should estimate fare correctly using configurable formula")
    void testEstimateFare_Successful() {
        FareEstimateRequest request = new FareEstimateRequest("Fort", "Mount Lavinia", 10.0);

        FareEstimateResponse response = fareService.estimateFare(request);

        assertNotNull(response);
        assertEquals(10.0, response.getDistanceKm());
        assertEquals(100.0, response.getBaseFare());
        assertEquals(40.0, response.getRatePerKm());
        assertEquals(400.0, response.getDistanceCharge()); // 10.0 * 40.0
        assertEquals(25.0, response.getServiceFee());
        assertEquals(525.0, response.getEstimatedFare()); // 100.0 + 400.0 + 25.0
        assertEquals("LKR", response.getCurrency());
        assertTrue(response.getCalculationFormula().contains("Base Fare"));
    }

    @Test
    @DisplayName("Should throw BadRequestException when distance is zero or negative during estimation")
    void testEstimateFare_InvalidDistance() {
        FareEstimateRequest zeroDistance = new FareEstimateRequest("A", "B", 0.0);
        FareEstimateRequest negativeDistance = new FareEstimateRequest("A", "B", -3.5);

        assertThrows(BadRequestException.class, () -> fareService.estimateFare(zeroDistance));
        assertThrows(BadRequestException.class, () -> fareService.estimateFare(negativeDistance));
    }

    @Test
    @DisplayName("Should calculate and save final fare for a new ride")
    void testCalculateAndSaveFinalFare_Success() {
        FinalFareCalculateRequest request = new FinalFareCalculateRequest("ride-101", "passenger-501", 5.0);

        when(fareRepository.findByRideId("ride-101")).thenReturn(Optional.empty());

        Fare savedFare = new Fare(
                "fare-id-1",
                "ride-101",
                "passenger-501",
                5.0,
                100.0,
                40.0,
                200.0, // 5.0 * 40.0
                25.0,
                325.0, // 100.0 + 200.0 + 25.0
                "LKR",
                LocalDateTime.now()
        );
        when(fareRepository.save(any(Fare.class))).thenReturn(savedFare);

        FareCalculationResponse response = fareService.calculateAndSaveFinalFare(request);

        assertNotNull(response);
        assertEquals("fare-id-1", response.getFareId());
        assertEquals("ride-101", response.getRideId());
        assertEquals(325.0, response.getTotalFare());
        verify(fareRepository, times(1)).save(any(Fare.class));
    }

    @Test
    @DisplayName("Should return existing fare if already calculated for the ride")
    void testCalculateAndSaveFinalFare_AlreadyCalculated() {
        FinalFareCalculateRequest request = new FinalFareCalculateRequest("ride-101", "passenger-501", 5.0);

        Fare existingFare = new Fare(
                "fare-id-existing",
                "ride-101",
                "passenger-501",
                5.0,
                100.0,
                40.0,
                200.0,
                25.0,
                325.0,
                "LKR",
                LocalDateTime.now()
        );
        when(fareRepository.findByRideId("ride-101")).thenReturn(Optional.of(existingFare));

        FareCalculationResponse response = fareService.calculateAndSaveFinalFare(request);

        assertNotNull(response);
        assertEquals("fare-id-existing", response.getFareId());
        verify(fareRepository, never()).save(any(Fare.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when fare for ride ID is not found")
    void testGetFareByRideId_NotFound() {
        when(fareRepository.findByRideId("unknown-ride")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fareService.getFareByRideId("unknown-ride"));
    }

    @Test
    @DisplayName("Should retrieve fare by unique fare ID")
    void testGetFareById_Success() {
        Fare existingFare = new Fare(
                "fare-id-1",
                "ride-101",
                "passenger-501",
                5.0,
                100.0,
                40.0,
                200.0,
                25.0,
                325.0,
                "LKR",
                LocalDateTime.now()
        );
        when(fareRepository.findById("fare-id-1")).thenReturn(Optional.of(existingFare));

        FareCalculationResponse response = fareService.getFareById("fare-id-1");

        assertNotNull(response);
        assertEquals("ride-101", response.getRideId());
    }
}
