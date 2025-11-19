package com.allyticlabs.backend.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@DynamoDbBean
public class Newsletter {
    private String email;
    private LocalDateTime subscribedAt;

    @DynamoDbPartitionKey
    public String getEmail() {
        return email;
    }
}