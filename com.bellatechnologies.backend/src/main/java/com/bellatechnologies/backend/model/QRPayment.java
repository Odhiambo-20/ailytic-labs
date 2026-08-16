package com.bellatechnologies.backend.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Base64;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qr_payments")
public class QRPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String qrPaymentId;

    private String qrCode;

    private String qrCodeToken;

    private String qrCodeImage; // Changed from byte[] to String (Base64 encoded)

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Builder.Default
    private Double amount = 0.0; // Changed from BigDecimal to Double

    private String currency;

    private String paymentId;

    private String merchantId;

    private String merchantName;

    private String description;

    private String createdAt;

    private String updatedAt;

    private String expiresAt;

    private String usedAt;

    private String scannedAt;

    private Boolean used;

    private Boolean singleUse;

    @Builder.Default
    private Integer scanCount = 0;

    private Integer maxScans;

    private String ipAddress;

    private String deviceFingerprint;

    private String totpSecret;

    // Helper methods for byte[] conversion
    public byte[] getQrCodeImageAsBytes() {
        if (qrCodeImage == null) return null;
        try {
            return Base64.getDecoder().decode(qrCodeImage);
        } catch (Exception e) {
            return null;
        }
    }

    public void setQrCodeImageFromBytes(byte[] bytes) {
        this.qrCodeImage = bytes != null ? Base64.getEncoder().encodeToString(bytes) : null;
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

    public Instant getExpiresAtInstant() {
        if (expiresAt == null) return null;
        try {
            return Instant.parse(expiresAt);
        } catch (Exception e) {
            return null;
        }
    }

    public void setExpiresAt(Instant instant) {
        this.expiresAt = instant != null ? instant.toString() : null;
    }

    public Instant getUsedAtInstant() {
        if (usedAt == null) return null;
        try {
            return Instant.parse(usedAt);
        } catch (Exception e) {
            return null;
        }
    }

    public void setUsedAt(Instant instant) {
        this.usedAt = instant != null ? instant.toString() : null;
    }

    public Instant getScannedAtInstant() {
        if (scannedAt == null) return null;
        try {
            return Instant.parse(scannedAt);
        } catch (Exception e) {
            return null;
        }
    }

    public void setScannedAt(Instant instant) {
        this.scannedAt = instant != null ? instant.toString() : null;
    }

    // Helper methods for backward compatibility
    public boolean isExpired() {
        if (expiresAt == null) return false;
        try {
            return Instant.parse(expiresAt).isBefore(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUsed() {
        return Boolean.TRUE.equals(used);
    }

    public Integer getScanCount() {
        return scanCount != null ? scanCount : 0;
    }
}
