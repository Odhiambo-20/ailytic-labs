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
@Table(name = "mpesa_payments")
public class MpesaPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String phoneNumber;

    private Double amount;

    private String checkoutRequestId;

    private String merchantRequestId;

    private String transactionId;

    private String mpesaTransactionId;

    private String status; // String to store PaymentStatus enum name

    private String paymentId;

    private String accountReference;  // ADDED: Missing field

    private String transactionDesc;  // ADDED: Missing field

    private String resultCode;

    private String resultDesc;

    private String mpesaReceiptNumber;

    private String transactionDate;

    private String callbackPayload;

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
