package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.SolarPanel;
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
public class SolarPanelRepository {
    private final DynamoDbTable<SolarPanel> solarPanelTable;

    public SolarPanelRepository(DynamoDbEnhancedClient enhancedClient) {
        this.solarPanelTable = enhancedClient.table("SolarPanels", TableSchema.fromBean(SolarPanel.class));
    }

    public List<SolarPanel> findAll() {
        return solarPanelTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<SolarPanel> findByType(String type) {
        return solarPanelTable.scan(ScanEnhancedRequest.builder()
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

    public SolarPanel findById(String id) {
        return solarPanelTable.getItem(r -> r.key(k -> k.partitionValue(id)));
    }

    public SolarPanel save(SolarPanel solarPanel) {
        if (solarPanel.getId() == null) {
            solarPanel.setId(UUID.randomUUID().toString());
        }
        solarPanelTable.putItem(solarPanel);
        return solarPanel;
    }

    public SolarPanel update(SolarPanel solarPanel) {
        solarPanelTable.putItem(solarPanel);
        return solarPanel;
    }

    public void delete(String id) {
        solarPanelTable.deleteItem(r -> r.key(k -> k.partitionValue(id)));
    }
}
