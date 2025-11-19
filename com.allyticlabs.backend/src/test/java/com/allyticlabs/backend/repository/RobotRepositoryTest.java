package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.Robot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class RobotRepositoryTest {

    @Autowired
    private RobotRepository robotRepository;

    @Autowired
    private DynamoDbEnhancedClient enhancedClient;

    @Container
    private static final GenericContainer<?> dynamoDbContainer = new GenericContainer<>("amazon/dynamodb-local:latest")
            .withExposedPorts(8000);

    @BeforeEach
    public void setUp() {
        // Configure DynamoDB client to use Testcontainers
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:" + dynamoDbContainer.getFirstMappedPort()))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .build();

        // Create Robots table
        dynamoDbClient.createTable(CreateTableRequest.builder()
                .tableName("Robots")
                .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build())
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                .build());

        // Ensure table is active
        dynamoDbClient.waiter().waitUntilTableExists(DescribeTableRequest.builder().tableName("Robots").build());
    }

    @Test
    public void testSaveAndFindRobot() {
        Robot robot = new Robot();
        robot.setId("1");
        robot.setName("NeuroBot X1");
        robot.setType("AI Assistant");

        robotRepository.save(robot);

        Robot found = robotRepository.findById("1");
        assertNotNull(found);
        assertEquals("NeuroBot X1", found.getName());
    }

    @Test
    public void testFindAllRobots() {
        Robot robot1 = new Robot();
        robot1.setId("1");
        robot1.setName("NeuroBot X1");
        Robot robot2 = new Robot();
        robot2.setId("2");
        robot2.setName("IndustroMax Pro");

        robotRepository.save(robot1);
        robotRepository.save(robot2);

        List<Robot> robots = robotRepository.findAll();
        assertEquals(2, robots.size());
    }
}