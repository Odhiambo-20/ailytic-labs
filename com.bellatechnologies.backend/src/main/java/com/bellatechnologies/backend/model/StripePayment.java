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
@Table(name = "stripe_payments")
public class StripePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String paymentIntentId;

    private String stripePaymentIntentId;

    private Double amount;

    private String currency;

    private String customerId;

    private String status; // String to store PaymentStatus enum name

    private String stripeStatus;

    private String paymentId;

    private String refundId;

    private String refundAmount; // String to store Double

    private String createdAt;

    private String updatedAt;

    // Helper methods for PaymentStatus conversion
    public PaymentStatus getStatusEnum() {
        if (status == null) return PaymentStatus.PENDING;
        try {
            return PaymentStatus.valueOf(status);
        } catch (Exception e) {
            return PaymentStatus.PENDING;
        }
    }

    public void setStatus(PaymentStatus paymentStatus) {
        this.status = paymentStatus != null ? paymentStatus.name() : PaymentStatus.PENDING.name();
    }

    // Helper methods for refund amount conversion
    public Double getRefundAmountDouble() {
        if (refundAmount == null) return null;
        try {
            return Double.parseDouble(refundAmount);
        } catch (Exception e) {
            return null;
        }
    }

    public void setRefundAmount(Double amount) {
        this.refundAmount = amount != null ? String.valueOf(amount) : null;
    }

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
