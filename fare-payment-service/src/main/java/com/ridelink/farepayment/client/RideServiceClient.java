package com.ridelink.farepayment.client;

import com.ridelink.farepayment.client.dto.RideDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class RideServiceClient {

    private static final Logger log = LoggerFactory.getLogger(RideServiceClient.class);

    private final RestTemplate restTemplate;
    private final String rideServiceUrl;
    private final boolean mockMode;

    public RideServiceClient(RestTemplate restTemplate,
                             @Value("${ridelink.services.ride-management.url:http://localhost:8083}") String rideServiceUrl,
                             @Value("${ridelink.services.ride-management.mock-mode:true}") boolean mockMode) {
        this.restTemplate = restTemplate;
        this.rideServiceUrl = rideServiceUrl;
        this.mockMode = mockMode;
    }

    /**
     * Retrieves ride information via REST from Ride Management Service.
     * Includes graceful fallback/mock support for standalone testing and viva demonstrations.
     */
    public Optional<RideDetailsDto> getRideDetails(String rideId) {
        if (mockMode) {
            log.info("Mock mode enabled for RideServiceClient. Returning simulated ride details for rideId: {}", rideId);
            return Optional.of(new RideDetailsDto(
                    rideId,
                    "passenger-501",
                    "driver-202",
                    "COMPLETED",
                    8.2,
                    "Colombo Fort",
                    "Galle Face Green"
            ));
        }

        try {
            String url = rideServiceUrl + "/api/rides/" + rideId;
            RideDetailsDto dto = restTemplate.getForObject(url, RideDetailsDto.class);
            return Optional.ofNullable(dto);
        } catch (RestClientException ex) {
            log.warn("Failed to reach Ride Management Service at {}. Falling back to default: {}", rideServiceUrl, ex.getMessage());
            return Optional.empty();
        }
    }
}
