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
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    private String merchantId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.CARD;

    @Builder.Default
    private Double amount = 0.0;

    @Builder.Default
    private String currency = "USD";

    private String transactionHash;

    private String externalTransactionId;

    private String idempotencyKey;

    private String description;

    private String createdAt;

    private String updatedAt;

    private String completedAt;

    // Helper methods for Instant conversion
    public Instant getCreatedAt() {
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

    public Instant getUpdatedAt() {
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

    public Instant getCompletedAt() {
        if (completedAt == null) return null;
        try {
            return Instant.parse(completedAt);
        } catch (Exception e) {
            return null;
        }
    }

    public void setCompletedAt(Instant instant) {
        this.completedAt = instant != null ? instant.toString() : null;
    }
}
