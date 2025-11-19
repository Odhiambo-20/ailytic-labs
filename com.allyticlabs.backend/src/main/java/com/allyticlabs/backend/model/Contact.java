package com.allyticlabs.backend.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@DynamoDbBean
public class Contact {
    private String id;
    @NotBlank(message = "First name is required")
    private String firstName;
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    private String helpType;
    private String message;
    private LocalDateTime createdAt;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}