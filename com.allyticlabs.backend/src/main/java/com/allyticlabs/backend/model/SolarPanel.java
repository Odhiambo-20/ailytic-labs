package com.allyticlabs.backend.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@Data
@DynamoDbBean
public class SolarPanel {
    private String id;
    private String name;
    private String type;
    private String description;
    private List<String> features;
    private String image;
    private String power;
    private String efficiency;
    private String warranty;
    private String price;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}