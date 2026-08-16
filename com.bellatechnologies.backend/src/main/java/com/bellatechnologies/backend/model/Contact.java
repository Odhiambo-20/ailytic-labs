package com.bellatechnologies.backend.model;

import jakarta.persistence.*;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    public String getId() {
        return id;
    }
}
