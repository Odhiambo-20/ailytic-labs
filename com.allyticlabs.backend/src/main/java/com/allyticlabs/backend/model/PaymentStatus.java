package com.allyticlabs.backend.model;

/**
 * Enum representing different payment statuses
 */
public enum PaymentStatus {
    PENDING("Payment is pending"),
    PROCESSING("Payment is being processed"),
    SUCCESS("Payment completed successfully"),
    FAILED("Payment failed"),
    CANCELLED("Payment was cancelled"),
    REFUNDED("Payment was refunded"),
    EXPIRED("Payment has expired");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminalStatus() {
        return this == SUCCESS || this == FAILED || this == CANCELLED || this == REFUNDED || this == EXPIRED;
    }

    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    public boolean canBeRefunded() {
        return this == SUCCESS;
    }

    public boolean canBeCancelled() {
        return this == PENDING || this == PROCESSING;
    }
}