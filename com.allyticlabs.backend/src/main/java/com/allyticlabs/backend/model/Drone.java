package com.allyticlabs.backend.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@Data
@DynamoDbBean
public class Drone {
    private String id;
    private String name;
    private String type;
    private String description;
    private List<String> specifications;
    private String image;
    private String price;
    private Double rating;
    private Integer reviews;
    private String flightTime;
    private String range;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}