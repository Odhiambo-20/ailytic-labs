package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.Robot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RobotRepository extends JpaRepository<Robot, String> {
    List<Robot> findByType(String type);
}
