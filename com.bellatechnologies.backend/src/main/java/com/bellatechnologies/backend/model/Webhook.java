
// ============================================================================
// File: model/Webhook.java
// ============================================================================
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
@Table(name = "webhook_logs")
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String webhookId;

    private Long timestamp;

    private String paymentId;

    private String provider; // MPESA, STRIPE

    private String eventType;

    private String payload; // Raw webhook payload

    private String signature; // Webhook signature

    private Boolean verified;

    private Boolean processed;

    private String processingStatus; // SUCCESS, FAILED, PENDING

    private String errorMessage;

    private Integer retryCount;

    private String ipAddress;

    private String userAgent;

    private String headers; // JSON string of request headers

    private String createdAt;

    private String processedAt;

    @PrePersist
    public void onPreSave() {
        if (this.timestamp == null) {
            this.timestamp = Instant.now().toEpochMilli();
        }
        if (this.createdAt == null) {
            this.createdAt = Instant.now().toString();
        }
    }
}
