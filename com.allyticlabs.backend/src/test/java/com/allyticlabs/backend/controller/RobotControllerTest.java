package com.allyticlabs.backend.controller;

import com.allyticlabs.backend.model.Robot;
import com.allyticlabs.backend.service.RobotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RobotController.class)
public class RobotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RobotService robotService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllRobots() throws Exception {
        Robot robot = new Robot();
        robot.setId("1");
        robot.setName("NeuroBot X1");
        robot.setType("AI Assistant");
        List<Robot> robots = Arrays.asList(robot);

        when(robotService.getAllRobots()).thenReturn(robots);

        mockMvc.perform(get("/api/robots")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("NeuroBot X1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateRobot() throws Exception {
        Robot robot = new Robot();
        robot.setId("1");
        robot.setName("NeuroBot X1");
        robot.setType("AI Assistant");

        when(robotService.createRobot(any(Robot.class))).thenReturn(robot);

        mockMvc.perform(post("/api/robots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(robot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("NeuroBot X1"));
    }
}