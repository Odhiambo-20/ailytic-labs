package com.allyticlabs.backend.service;

import com.allyticlabs.backend.exception.ResourceNotFoundException;
import com.allyticlabs.backend.model.Robot;
import com.allyticlabs.backend.repository.RobotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RobotServiceTest {

    @Mock
    private RobotRepository robotRepository;

    @InjectMocks
    private RobotService robotService;

    @Test
    public void testGetAllRobots() {
        Robot robot = new Robot();
        robot.setId("1");
        robot.setName("NeuroBot X1");
        List<Robot> robots = Arrays.asList(robot);

        when(robotRepository.findAll()).thenReturn(robots);

        List<Robot> result = robotService.getAllRobots();

        assertEquals(1, result.size());
        assertEquals("NeuroBot X1", result.get(0).getName());
        verify(robotRepository, times(1)).findAll();
    }

    @Test
    public void testGetRobotById_NotFound() {
        when(robotRepository.findById("1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> robotService.getRobotById("1"));
        verify(robotRepository, times(1)).findById("1");
    }
}