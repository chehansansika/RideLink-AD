package com.ridelink.ride_management_service.service;

import com.ridelink.ride_management_service.dto.RideRequest;
import com.ridelink.ride_management_service.dto.RideResponse;
import com.ridelink.ride_management_service.exception.RideNotFoundException;
import com.ridelink.ride_management_service.model.Ride;
import com.ridelink.ride_management_service.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public List<RideResponse> getAllRides() {
        return rideRepository.findAll()
                .stream()
                .map(RideResponse::new)
                .toList();
    }

    public RideResponse getRideById(String id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(id));

        return new RideResponse(ride);
    }

    public RideResponse createRide(RideRequest request) {
        Ride ride = new Ride();

        ride.setPassengerId(request.getPassengerId());
        ride.setDriverId(request.getDriverId());
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDropoffLocation(request.getDropoffLocation());
        ride.setStatus(request.getStatus());

        Ride savedRide = rideRepository.save(ride);

        return new RideResponse(savedRide);
    }

    public RideResponse updateRide(String id, RideRequest request) {
        Ride existingRide = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(id));

        existingRide.setPassengerId(request.getPassengerId());
        existingRide.setDriverId(request.getDriverId());
        existingRide.setPickupLocation(request.getPickupLocation());
        existingRide.setDropoffLocation(request.getDropoffLocation());
        existingRide.setStatus(request.getStatus());

        Ride updatedRide = rideRepository.save(existingRide);

        return new RideResponse(updatedRide);
    }

    public void deleteRide(String id) {
        if (!rideRepository.existsById(id)) {
            throw new RideNotFoundException(id);
        }

        rideRepository.deleteById(id);
    }
}
