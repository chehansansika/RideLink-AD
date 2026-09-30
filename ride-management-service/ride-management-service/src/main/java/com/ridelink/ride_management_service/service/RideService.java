package com.ridelink.ride_management_service.service;

import com.ridelink.ride_management_service.model.Ride;
import com.ridelink.ride_management_service.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Optional<Ride> getRideById(String id) {
        return rideRepository.findById(id);
    }

    public Ride createRide(Ride ride) {
        return rideRepository.save(ride);
    }

    public Ride updateRide(String id, Ride ride) {
        return rideRepository.findById(id)
                .map(existingRide -> {
                    existingRide.setPassengerId(ride.getPassengerId());
                    existingRide.setDriverId(ride.getDriverId());
                    existingRide.setPickupLocation(ride.getPickupLocation());
                    existingRide.setDropoffLocation(ride.getDropoffLocation());
                    existingRide.setStatus(ride.getStatus());
                    return rideRepository.save(existingRide);
                })
                .orElse(null);
    }

    public boolean deleteRide(String id) {
        if (rideRepository.existsById(id)) {
            rideRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
