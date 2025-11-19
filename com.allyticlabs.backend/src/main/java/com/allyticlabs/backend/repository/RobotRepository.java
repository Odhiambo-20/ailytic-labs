package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.Robot;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RobotRepository {
    private final DynamoDbTable<Robot> robotTable;

    public RobotRepository(DynamoDbEnhancedClient enhancedClient) {
        this.robotTable = enhancedClient.table("Robots", TableSchema.fromBean(Robot.class));
    }

    public List<Robot> findAll() {
        return robotTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<Robot> findByType(String type) {
        return robotTable.scan(ScanEnhancedRequest.builder()
                .filterExpression(Expression.builder()
                    .expression("#type = :type")
                    .putExpressionName("#type", "type")
                    .putExpressionValue(":type", AttributeValue.builder().s(type).build())
                    .build())
                .build())
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public Robot findById(String id) {
        return robotTable.getItem(r -> r.key(k -> k.partitionValue(id)));
    }

    public Robot save(Robot robot) {
        if (robot.getId() == null) {
            robot.setId(UUID.randomUUID().toString());
        }
        robotTable.putItem(robot);
        return robot;
    }

    public Robot update(Robot robot) {
        robotTable.putItem(robot);
        return robot;
    }

    public void delete(String id) {
        robotTable.deleteItem(r -> r.key(k -> k.partitionValue(id)));
    }
}
