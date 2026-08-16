package com.bellatechnologies.backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.bellatechnologies.backend.model.Robot;
import com.bellatechnologies.backend.repository.RobotRepository;
import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = {
    "payment.encryption.key=01234567890123456789012345678901",
    "jwt.secret=0123456789012345678901234567890101234567890123456789012345678901",
    "stripe.secret.key=test",
    "stripe.public.key=test",
    "stripe.webhook.secret=test",
    "spring.security.oauth2.client.registration.google.client-id=test",
    "spring.security.oauth2.client.registration.google.client-secret=test",
    "mpesa.consumer-key=test",
    "mpesa.consumer-secret=test",
    "mpesa.passkey=test",
    "mpesa.short-code=174379",
    "mpesa.initiator-password=test",
    "mpesa.callback-url=https://example.invalid/callback",
    "mpesa.timeout-url=https://example.invalid/timeout",
    "mpesa.result-url=https://example.invalid/result",
    "mpesa.validation-url=https://example.invalid/validation",
    "mpesa.confirmation-url=https://example.invalid/confirmation",
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.flyway.enabled=true"
})
@Testcontainers(disabledWithoutDocker = true)
class PostgreSqlMigrationIT {
    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("bella_technologies")
                    .withUsername("bella_app")
                    .withPassword("integration-test-only");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired DataSource dataSource;
    @Autowired RobotRepository robotRepository;

    @Test
    void flywayCreatesValidatedProductionSchema() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isValid(5)).isTrue();
        }

        Robot robot = new Robot();
        robot.setName("Migration verification robot");
        robot.setType("industrial");
        assertThat(robotRepository.saveAndFlush(robot).getId()).isNotBlank();
    }
}
