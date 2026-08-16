package com.bellatechnologies.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bellatechnologies.backend.model.Robot;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class RobotRepositoryTest {
    @Autowired private RobotRepository repository;

    @Test
    void persistsAndQueriesRobotsByType() {
        Robot robot = new Robot();
        robot.setName("Industrial Arm");
        robot.setType("industrial");
        robot.setCapabilities(List.of("precision", "automation"));

        Robot saved = repository.saveAndFlush(robot);

        assertThat(saved.getId()).isNotBlank();
        assertThat(repository.findByType("industrial"))
                .extracting(Robot::getName)
                .containsExactly("Industrial Arm");
    }
}
