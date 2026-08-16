package com.bellatechnologies.backend.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_users", uniqueConstraints = {@UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "username")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String provider; // "local", "google", "github"
    private String providerId;
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text")
    private List<String> roles;
    private boolean enabled;
    private boolean emailVerified;
    private String profilePictureUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }
}
