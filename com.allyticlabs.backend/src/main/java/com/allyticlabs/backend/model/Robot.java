package com.allyticlabs.backend.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@Data
@DynamoDbBean
public class Robot {
    private String id;
    private String name;
    private String type;
    private String description;
    private List<String> capabilities;
    private String image;
    private String price;
    private Double rating;
    private Integer reviews;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}