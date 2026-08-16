package com.bellatechnologies.backend.model;

import jakarta.persistence.*;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "newsletter_subscriptions")
public class Newsletter {
    @Id
    private String email;
    private LocalDateTime subscribedAt;

    public String getEmail() {
        return email;
    }
}
