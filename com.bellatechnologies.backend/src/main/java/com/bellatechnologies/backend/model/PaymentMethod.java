package com.bellatechnologies.backend.model;

import jakarta.persistence.*;

/**
 * Enum representing different payment methods supported by the system
 */
public enum PaymentMethod {
    CARD("Card Payment"),
    MPESA("M-Pesa Payment"),
    STRIPE("Stripe Payment"),
    QR_CODE("QR Code Payment"),
    BANK_TRANSFER("Bank Transfer"),
    WALLET("Digital Wallet"),
    CASH("Cash Payment");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromString(String method) {
        if (method == null) {
            return CARD;
        }

        for (PaymentMethod pm : PaymentMethod.values()) {
            if (pm.name().equalsIgnoreCase(method) ||
                pm.displayName.equalsIgnoreCase(method)) {
                return pm;
            }
        }

        return CARD; // Default fallback
    }
}
