// ============================================================================
// File: model/PaymentStatus.java
// ============================================================================
package com.allyticlabs.backend.model;

public enum PaymentStatus {
    PENDING("Payment initiated, awaiting confirmation"),
    PROCESSING("Payment is being processed"),
    COMPLETED("Payment completed successfully"),
    FAILED("Payment failed"),
    CANCELLED("Payment cancelled by user"),
    REFUNDED("Payment refunded"),
    EXPIRED("Payment session expired"),
    VERIFIED("Payment verified and settled");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
