package com.allyticlabs.backend.service;

import com.allyticlabs.backend.exception.ResourceNotFoundException;
import com.allyticlabs.backend.model.Drone;
import com.allyticlabs.backend.repository.DroneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DroneService {
    private final DroneRepository droneRepository;

    public DroneService(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    public List<Drone> getAllDrones() {
        return droneRepository.findAll();
    }

    public List<Drone> getDronesByType(String type) {
        return droneRepository.findByType(type);
    }

    public Drone getDroneById(String id) {
        Drone drone = droneRepository.findById(id);
        if (drone == null) {
            throw new ResourceNotFoundException("Drone not found with id: " + id);
        }
        return drone;
    }

    public Drone createDrone(Drone drone) {
        return droneRepository.save(drone);
    }

    public Drone updateDrone(String id, Drone drone) {
        if (droneRepository.findById(id) == null) {
            throw new ResourceNotFoundException("Drone not found with id: " + id);
        }
        drone.setId(id);
        return droneRepository.update(drone);
    }

    public void deleteDrone(String id) {
        if (droneRepository.findById(id) == null) {
            throw new ResourceNotFoundException("Drone not found with id: " + id);
        }
        droneRepository.delete(id);
    }
}