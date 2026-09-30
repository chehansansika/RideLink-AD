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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class FareService {

    private final FareRepository fareRepository;
    private final FareProperties fareProperties;

    public FareService(FareRepository fareRepository, FareProperties fareProperties) {
        this.fareRepository = fareRepository;
        this.fareProperties = fareProperties;
    }

    /**
     * Estimates the fare for a trip based on the configurable formula:
     * Total Fare = Base Fare + (Distance * Rate per KM) + Service Fee
     */
    public FareEstimateResponse estimateFare(FareEstimateRequest request) {
        validateDistance(request.getDistanceKm());

        double baseFare = round(fareProperties.getBaseFare());
        double ratePerKm = round(fareProperties.getRatePerKm());
        double serviceFee = round(fareProperties.getServiceFee());
        double distanceKm = round(request.getDistanceKm());

        double distanceCharge = round(distanceKm * ratePerKm);
        double totalFare = round(baseFare + distanceCharge + serviceFee);

        String formula = String.format("Base Fare (%.2f) + [Distance (%.2f km) * Rate (%.2f/km)] + Service Fee (%.2f)",
                baseFare, distanceKm, ratePerKm, serviceFee);

        return new FareEstimateResponse(
                request.getPickupLocation(),
                request.getDropoffLocation(),
                distanceKm,
                baseFare,
                ratePerKm,
                distanceCharge,
                serviceFee,
                totalFare,
                fareProperties.getCurrency(),
                formula,
                LocalDateTime.now()
        );
    }

    /**
     * Calculates the final fare after ride completion and persists it in MongoDB.
     */
    public FareCalculationResponse calculateAndSaveFinalFare(FinalFareCalculateRequest request) {
        validateDistance(request.getDistanceKm());

        if (request.getRideId() == null || request.getRideId().isBlank()) {
            throw new BadRequestException("Ride ID cannot be empty");
        }
        if (request.getPassengerId() == null || request.getPassengerId().isBlank()) {
            throw new BadRequestException("Passenger ID cannot be empty");
        }

        // Return existing calculated fare if already recorded for this ride
        return fareRepository.findByRideId(request.getRideId())
                .map(this::mapToCalculationResponse)
                .orElseGet(() -> {
                    double baseFare = round(fareProperties.getBaseFare());
                    double ratePerKm = round(fareProperties.getRatePerKm());
                    double serviceFee = round(fareProperties.getServiceFee());
                    double distanceKm = round(request.getDistanceKm());

                    double distanceCharge = round(distanceKm * ratePerKm);
                    double totalFare = round(baseFare + distanceCharge + serviceFee);

                    Fare fare = new Fare(
                            null,
                            request.getRideId(),
                            request.getPassengerId(),
                            distanceKm,
                            baseFare,
                            ratePerKm,
                            distanceCharge,
                            serviceFee,
                            totalFare,
                            fareProperties.getCurrency(),
                            LocalDateTime.now()
                    );

                    Fare saved = fareRepository.save(fare);
                    return mapToCalculationResponse(saved);
                });
    }

    /**
     * Retrieves calculated fare details by ride identifier.
     */
    public FareCalculationResponse getFareByRideId(String rideId) {
        return fareRepository.findByRideId(rideId)
                .map(this::mapToCalculationResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Fare record not found for ride ID: " + rideId));
    }

    /**
     * Retrieves calculated fare details by fare document ID.
     */
    public FareCalculationResponse getFareById(String fareId) {
        return fareRepository.findById(fareId)
                .map(this::mapToCalculationResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Fare record not found with ID: " + fareId));
    }

    private void validateDistance(Double distance) {
        if (distance == null || distance <= 0) {
            throw new BadRequestException("Distance must be greater than zero");
        }
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private FareCalculationResponse mapToCalculationResponse(Fare fare) {
        return new FareCalculationResponse(
                fare.getId(),
                fare.getRideId(),
                fare.getPassengerId(),
                fare.getDistanceKm(),
                fare.getBaseFare(),
                fare.getRatePerKm(),
                fare.getDistanceCharge(),
                fare.getServiceFee(),
                fare.getTotalFare(),
                fare.getCurrency(),
                fare.getCalculatedAt()
        );
    }
}
