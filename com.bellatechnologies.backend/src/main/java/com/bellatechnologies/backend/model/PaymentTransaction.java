package com.bellatechnologies.backend.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String paymentId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentMethod method = PaymentMethod.CARD;

    @Builder.Default
    private Double amount = 0.0;

    @Builder.Default
    private String transactionId = "";

    private String transactionType; // Added to replace 'event'

    private String errorMessage; // Added for error tracking

    private String metadata; // Additional metadata as JSON string

    private String createdAt;

    private String updatedAt;

    // Helper methods for Instant conversion
    public Instant getCreatedAtInstant() {
        if (createdAt == null) return null;
        try {
            return Instant.parse(createdAt);
        } catch (Exception e) {
            return null;
        }
    }

    public void setCreatedAt(Instant instant) {
        this.createdAt = instant != null ? instant.toString() : null;
    }

    public Instant getUpdatedAtInstant() {
        if (updatedAt == null) return null;
        try {
            return Instant.parse(updatedAt);
        } catch (Exception e) {
            return null;
        }
    }

    public void setUpdatedAt(Instant instant) {
        this.updatedAt = instant != null ? instant.toString() : null;
    }
}
