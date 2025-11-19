package com.allyticlabs.backend.controller;

import com.allyticlabs.backend.model.Robot;
import com.allyticlabs.backend.service.RobotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/robots")
public class RobotController {
    private final RobotService robotService;

    public RobotController(RobotService robotService) {
        this.robotService = robotService;
    }

    @GetMapping
    public List<Robot> getAllRobots(@RequestParam(required = false) String type) {
        if (type != null) {
            return robotService.getRobotsByType(type);
        }
        return robotService.getAllRobots();
    }

    @GetMapping("/{id}")
    public Robot getRobotById(@PathVariable String id) {
        return robotService.getRobotById(id);
    }

    @PostMapping
    public Robot createRobot(@Valid @RequestBody Robot robot) {
        return robotService.createRobot(robot);
    }

    @PutMapping("/{id}")
    public Robot updateRobot(@PathVariable String id, @Valid @RequestBody Robot robot) {
        return robotService.updateRobot(id, robot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRobot(@PathVariable String id) {
        robotService.deleteRobot(id);
        return ResponseEntity.noContent().build();
    }
}