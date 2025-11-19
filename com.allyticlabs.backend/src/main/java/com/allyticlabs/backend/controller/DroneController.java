package com.allyticlabs.backend.controller;

import com.allyticlabs.backend.model.Drone;
import com.allyticlabs.backend.service.DroneService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drones")
public class DroneController {
    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    @GetMapping
    public List<Drone> getAllDrones(@RequestParam(required = false) String type) {
        if (type != null) {
            return droneService.getDronesByType(type);
        }
        return droneService.getAllDrones();
    }

    @GetMapping("/{id}")
    public Drone getDroneById(@PathVariable String id) {
        return droneService.getDroneById(id);
    }

    @PostMapping
    public Drone createDrone(@Valid @RequestBody Drone drone) {
        return droneService.createDrone(drone);
    }

    @PutMapping("/{id}")
    public Drone updateDrone(@PathVariable String id, @Valid @RequestBody Drone drone) {
        return droneService.updateDrone(id, drone);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrone(@PathVariable String id) {
        droneService.deleteDrone(id);
        return ResponseEntity.noContent().build();
    }
}