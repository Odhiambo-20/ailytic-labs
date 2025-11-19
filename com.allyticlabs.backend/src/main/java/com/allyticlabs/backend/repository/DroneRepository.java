package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.Drone;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class DroneRepository {
    private final DynamoDbTable<Drone> droneTable;

    public DroneRepository(DynamoDbEnhancedClient enhancedClient) {
        this.droneTable = enhancedClient.table("Drones", TableSchema.fromBean(Drone.class));
    }

    public List<Drone> findAll() {
        return droneTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<Drone> findByType(String type) {
        return droneTable.scan(ScanEnhancedRequest.builder()
                .filterExpression(b -> b.eq("type", type))
                .build())
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public Drone findById(String id) {
        return droneTable.getItem(r -> r.key(k -> k.partitionValue(id)));
    }

    public Drone save(Drone drone) {
        if (drone.getId() == null) {
            drone.setId(UUID.randomUUID().toString());
        }
        droneTable.putItem(drone);
        return drone;
    }

    public Drone update(Drone drone) {
        droneTable.updateItem(drone);
        return drone;
    }

    public void delete(String id) {
        droneTable.deleteItem(r -> r.key(k -> k.partitionValue(id)));
    }
}