// ============================================================================
// File: model/PaymentMethod.java
// ============================================================================
package com.allyticlabs.backend.model;

public enum PaymentMethod {
    MPESA("M-Pesa"),
    STRIPE("Stripe"),
    QR_CODE("QR Code"),
    CARD("Credit/Debit Card"),
    BANK_TRANSFER("Bank Transfer");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
