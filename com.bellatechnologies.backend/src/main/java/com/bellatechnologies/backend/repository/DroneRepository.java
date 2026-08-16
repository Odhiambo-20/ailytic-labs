package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.Drone;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneRepository extends JpaRepository<Drone, String> {
    List<Drone> findByType(String type);
}
