package com.allyticlabs.backend.service;

import com.allyticlabs.backend.exception.ResourceNotFoundException;
import com.allyticlabs.backend.model.Robot;
import com.allyticlabs.backend.repository.RobotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RobotService {
    private final RobotRepository robotRepository;

    public RobotService(RobotRepository robotRepository) {
        this.robotRepository = robotRepository;
    }

    public List<Robot> getAllRobots() {
        return robotRepository.findAll();
    }

    public List<Robot> getRobotsByType(String type) {
        return robotRepository.findByType(type);
    }

    public Robot getRobotById(String id) {
        Robot robot = robotRepository.findById(id);
        if (robot == null) {
            throw new ResourceNotFoundException("Robot not found with id: " + id);
        }
        return robot;
    }

    public Robot createRobot(Robot robot) {
        return robotRepository.save(robot);
    }

    public Robot updateRobot(String id, Robot robot) {
        if (robotRepository.findById(id) == null) {
            throw new ResourceNotFoundException("Robot not found with id: " + id);
        }
        robot.setId(id);
        return robotRepository.update(robot);
    }

    public void deleteRobot(String id) {
        if (robotRepository.findById(id) == null) {
            throw new ResourceNotFoundException("Robot not found with id: " + id);
        }
        robotRepository.delete(id);
    }
}