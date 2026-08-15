package com.bellatechnologies.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class User {

    private String userId;
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String provider; // "local", "google", "github"
    private String providerId;
    private List<String> roles;
    private boolean enabled;
    private boolean emailVerified;
    private String profilePictureUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute("username")
    public String getUsername() {
        return username;
    }

    @DynamoDbAttribute("password")
    public String getPassword() {
        return password;
    }

    @DynamoDbAttribute("firstName")
    public String getFirstName() {
        return firstName;
    }

    @DynamoDbAttribute("lastName")
    public String getLastName() {
        return lastName;
    }

    @DynamoDbAttribute("provider")
    public String getProvider() {
        return provider;
    }

    @DynamoDbAttribute("providerId")
    public String getProviderId() {
        return providerId;
    }

    @DynamoDbAttribute("roles")
    public List<String> getRoles() {
        return roles;
    }

    @DynamoDbAttribute("enabled")
    public boolean isEnabled() {
        return enabled;
    }

    @DynamoDbAttribute("emailVerified")
    public boolean isEmailVerified() {
        return emailVerified;
    }

    @DynamoDbAttribute("profilePictureUrl")
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    @DynamoDbAttribute("createdAt")
    public Instant getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute("updatedAt")
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @DynamoDbAttribute("lastLoginAt")
    public Instant getLastLoginAt() {
        return lastLoginAt;
    }
}
