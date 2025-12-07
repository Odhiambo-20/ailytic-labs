package com.allyticlabs.backend.exception;

import lombok.Getter;
import java.time.Instant;

/**
 * Exception thrown when QR code validation fails
 * Handles various QR code-related errors including expiry, tampering, and format issues
 */
@Getter
public class InvalidQRCodeException extends PaymentException {
    
    private final String qrCodeId;
    private final QRErrorReason errorReason;
    private final Instant qrExpiryTime;
    private final String scannedData;
    
    /**
     * Specific reasons for QR code validation failure
     */
    public enum QRErrorReason {
        EXPIRED,                    // QR code has expired
        INVALID_FORMAT,             // QR code format is invalid
        INVALID_SIGNATURE,          // QR code signature verification failed
        ALREADY_USED,               // QR code has already been used
        NOT_FOUND,                  // QR code not found in database
        AMOUNT_MISMATCH,            // Amount doesn't match expected value
        MERCHANT_MISMATCH,          // Merchant ID doesn't match
        TAMPERED,                   // QR code data has been tampered with
        MALFORMED_DATA,             // QR code contains malformed data
        INSUFFICIENT_DATA,          // QR code missing required fields
        DECRYPTION_FAILED,          // Failed to decrypt QR code data
        UNSUPPORTED_VERSION,        // QR code version not supported
        SCANNING_ERROR,             // Error during QR code scanning
        GENERATION_ERROR,           // Error during QR code generation
        PAYMENT_LIMIT_EXCEEDED      // Payment amount exceeds QR code limit
    }
    
    /**
     * Constructor with all parameters
     */
    public InvalidQRCodeException(String qrCodeId, QRErrorReason errorReason, 
                                 String message, Instant qrExpiryTime, 
                                 String scannedData, Throwable cause) {
        super(
            "QR_CODE_ERROR",
            message,
            qrCodeId,
            PaymentErrorType.VALIDATION_ERROR,
            cause
        );
        this.qrCodeId = qrCodeId;
        this.errorReason = errorReason;
        this.qrExpiryTime = qrExpiryTime;
        this.scannedData = maskSensitiveQRData(scannedData);
    }
    
    /**
     * Constructor without cause
     */
    public InvalidQRCodeException(String qrCodeId, QRErrorReason errorReason, 
                                 String message, Instant qrExpiryTime, String scannedData) {
        this(qrCodeId, errorReason, message, qrExpiryTime, scannedData, null);
    }
    
    /**
     * Constructor with minimal parameters
     */
    public InvalidQRCodeException(String qrCodeId, QRErrorReason errorReason, String message) {
        this(qrCodeId, errorReason, message, null, null, null);
    }
    
    /**
     * Static factory methods for common QR code errors
     */
    
    public static InvalidQRCodeException expired(String qrCodeId, Instant expiryTime) {
        long secondsExpired = (Instant.now().toEpochMilli() - expiryTime.toEpochMilli()) / 1000;
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.EXPIRED,
            String.format("QR code expired %d seconds ago at %s", secondsExpired, expiryTime),
            expiryTime,
            null
        );
    }
    
    public static InvalidQRCodeException expiredWithMinutes(String qrCodeId, Instant expiryTime) {
        long minutesExpired = (Instant.now().toEpochMilli() - expiryTime.toEpochMilli()) / (1000 * 60);
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.EXPIRED,
            String.format("QR code expired %d minutes ago", minutesExpired),
            expiryTime,
            null
        );
    }
    
    public static InvalidQRCodeException invalidFormat(String qrCodeId, String scannedData) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.INVALID_FORMAT,
            "QR code format is invalid or unrecognized. Expected payment QR format.",
            null,
            scannedData
        );
    }
    
    public static InvalidQRCodeException invalidSignature(String qrCodeId, String scannedData) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.INVALID_SIGNATURE,
            "QR code signature verification failed - possible tampering detected",
            null,
            scannedData
        );
    }
    
    public static InvalidQRCodeException alreadyUsed(String qrCodeId, Instant usedTime) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.ALREADY_USED,
            String.format("QR code was already used at %s. Each QR code can only be used once.", usedTime),
            null,
            null
        );
    }
    
    public static InvalidQRCodeException notFound(String qrCodeId) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.NOT_FOUND,
            String.format("QR code '%s' not found or has been deleted", qrCodeId)
        );
    }
    
    public static InvalidQRCodeException amountMismatch(String qrCodeId, Double expectedAmount, 
                                                       Double actualAmount) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.AMOUNT_MISMATCH,
            String.format("Amount mismatch - QR Code Amount: %.2f, Payment Amount: %.2f", 
                expectedAmount, actualAmount)
        );
    }
    
    public static InvalidQRCodeException merchantMismatch(String qrCodeId, String expectedMerchant, 
                                                         String actualMerchant) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.MERCHANT_MISMATCH,
            String.format("Merchant mismatch - Expected: %s, Actual: %s", 
                expectedMerchant, actualMerchant)
        );
    }
    
    public static InvalidQRCodeException tampered(String qrCodeId, String reason) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.TAMPERED,
            "QR code data integrity check failed: " + reason + ". Do not use this QR code."
        );
    }
    
    public static InvalidQRCodeException malformedData(String qrCodeId, String scannedData, 
                                                      String reason) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.MALFORMED_DATA,
            "QR code contains malformed data: " + reason,
            null,
            scannedData
        );
    }
    
    public static InvalidQRCodeException insufficientData(String qrCodeId, String missingFields) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.INSUFFICIENT_DATA,
            "QR code missing required fields: " + missingFields
        );
    }
    
    public static InvalidQRCodeException decryptionFailed(String qrCodeId, Throwable cause) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.DECRYPTION_FAILED,
            "Failed to decrypt QR code data. The QR code may be corrupted.",
            null,
            null,
            cause
        );
    }
    
    public static InvalidQRCodeException unsupportedVersion(String qrCodeId, String version) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.UNSUPPORTED_VERSION,
            String.format("QR code version '%s' is not supported by this system", version)
        );
    }
    
    public static InvalidQRCodeException scanningError(String message, Throwable cause) {
        return new InvalidQRCodeException(
            null,
            QRErrorReason.SCANNING_ERROR,
            "QR code scanning failed: " + message,
            null,
            null,
            cause
        );
    }
    
    public static InvalidQRCodeException generationError(String message, Throwable cause) {
        return new InvalidQRCodeException(
            null,
            QRErrorReason.GENERATION_ERROR,
            "QR code generation failed: " + message,
            null,
            null,
            cause
        );
    }
    
    public static InvalidQRCodeException paymentLimitExceeded(String qrCodeId, 
                                                             Double amount, 
                                                             Double maxLimit) {
        return new InvalidQRCodeException(
            qrCodeId,
            QRErrorReason.PAYMENT_LIMIT_EXCEEDED,
            String.format("Payment amount %.2f exceeds QR code limit of %.2f", amount, maxLimit)
        );
    }
    
    /**
     * Check if QR code can be regenerated
     */
    public boolean canRegenerate() {
        return errorReason == QRErrorReason.EXPIRED ||
               errorReason == QRErrorReason.NOT_FOUND ||
               errorReason == QRErrorReason.ALREADY_USED;
    }
    
    /**
     * Check if this is a security-related error
     */
    public boolean isSecurityError() {
        return errorReason == QRErrorReason.INVALID_SIGNATURE ||
               errorReason == QRErrorReason.TAMPERED ||
               errorReason == QRErrorReason.DECRYPTION_FAILED ||
               errorReason == QRErrorReason.MERCHANT_MISMATCH ||
               errorReason == QRErrorReason.AMOUNT_MISMATCH;
    }
    
    /**
     * Check if error should be logged as suspicious activity
     */
    public boolean isSuspiciousActivity() {
        return errorReason == QRErrorReason.TAMPERED ||
               errorReason == QRErrorReason.INVALID_SIGNATURE ||
               errorReason == QRErrorReason.MERCHANT_MISMATCH;
    }
    
    /**
     * Get recommended action for the user
     */
    public String getRecommendedAction() {
        switch (errorReason) {
            case EXPIRED:
                return "Please request a new QR code. This one has expired.";
            case ALREADY_USED:
                return "This QR code has already been used. Please generate a new one for your next payment.";
            case NOT_FOUND:
                return "QR code not found. Please verify the code or request a new one from the merchant.";
            case INVALID_SIGNATURE:
            case TAMPERED:
                return "Security validation failed. DO NOT USE this QR code. Please generate a new one and report this incident.";
            case AMOUNT_MISMATCH:
                return "Payment amount doesn't match QR code. Please verify with the merchant.";
            case MERCHANT_MISMATCH:
                return "Merchant information doesn't match. Please verify you're paying the correct merchant.";
            case INVALID_FORMAT:
            case MALFORMED_DATA:
            case INSUFFICIENT_DATA:
                return "Invalid QR code format. Please scan a valid payment QR code or request a new one.";
            case SCANNING_ERROR:
                return "Failed to scan QR code. Please try again in better lighting or request a new QR code.";
            case DECRYPTION_FAILED:
                return "Unable to process QR code. Please request a new one from the merchant.";
            case UNSUPPORTED_VERSION:
                return "This QR code version is not supported. Please update your app or request a compatible QR code.";
            case PAYMENT_LIMIT_EXCEEDED:
                return "Payment amount exceeds QR code limit. Please request a QR code with a higher limit.";
            case GENERATION_ERROR:
                return "Failed to generate QR code. Please try again or contact support.";
            default:
                return "QR code is invalid. Please generate a new one.";
        }
    }
    
    /**
     * Get user-friendly error message
     */
    @Override
    public String getUserFriendlyMessage() {
        switch (errorReason) {
            case EXPIRED:
                return "QR code has expired";
            case ALREADY_USED:
                return "QR code has already been used";
            case NOT_FOUND:
                return "QR code not found";
            case INVALID_SIGNATURE:
            case TAMPERED:
                return "QR code security validation failed";
            case AMOUNT_MISMATCH:
                return "Payment amount doesn't match";
            case MERCHANT_MISMATCH:
                return "Merchant information doesn't match";
            case INVALID_FORMAT:
            case MALFORMED_DATA:
            case INSUFFICIENT_DATA:
                return "Invalid QR code format";
            case SCANNING_ERROR:
                return "Failed to scan QR code";
            case DECRYPTION_FAILED:
                return "Unable to read QR code";
            case UNSUPPORTED_VERSION:
                return "QR code version not supported";
            case GENERATION_ERROR:
                return "Failed to generate QR code";
            case PAYMENT_LIMIT_EXCEEDED:
                return "Payment amount exceeds limit";
            default:
                return "QR code is invalid";
        }
    }
    
    /**
     * Get severity level of the error
     */
    public ErrorSeverity getSeverity() {
        if (isSecurityError()) {
            return ErrorSeverity.CRITICAL;
        } else if (errorReason == QRErrorReason.PAYMENT_LIMIT_EXCEEDED ||
                   errorReason == QRErrorReason.ALREADY_USED) {
            return ErrorSeverity.HIGH;
        } else if (errorReason == QRErrorReason.EXPIRED || 
                   errorReason == QRErrorReason.NOT_FOUND) {
            return ErrorSeverity.MEDIUM;
        } else {
            return ErrorSeverity.LOW;
        }
    }
    
    public enum ErrorSeverity {
        LOW,        // Format or scanning errors
        MEDIUM,     // Expired or not found
        HIGH,       // Already used or limit exceeded
        CRITICAL    // Security-related errors
    }
    
    /**
     * Get time until QR expiry (in seconds), or negative if already expired
     */
    public long getTimeUntilExpiry() {
        if (qrExpiryTime == null) {
            return 0;
        }
        return (qrExpiryTime.toEpochMilli() - Instant.now().toEpochMilli()) / 1000;
    }
    
    /**
     * Check if QR code is expired
     */
    public boolean isExpired() {
        return errorReason == QRErrorReason.EXPIRED || 
               (qrExpiryTime != null && Instant.now().isAfter(qrExpiryTime));
    }
    
    /**
     * Mask sensitive QR data for logging
     */
    private String maskSensitiveQRData(String data) {
        if (data == null || data.length() <= 8) {
            return "****";
        }
        // Show first 4 and last 4 characters only
        return data.substring(0, 4) + "****" + data.substring(data.length() - 4);
    }
    
    /**
     * Get detailed error information for logging
     */
    public String getDetailedErrorInfo() {
        StringBuilder info = new StringBuilder();
        info.append(String.format("QR Code Error - Reason: %s, ", errorReason));
        if (qrCodeId != null) {
            info.append(String.format("ID: %s, ", qrCodeId));
        }
        if (qrExpiryTime != null) {
            info.append(String.format("Expiry: %s, ", qrExpiryTime));
        }
        info.append(String.format("Severity: %s, ", getSeverity()));
        info.append(String.format("Message: %s", getErrorMessage()));
        return info.toString();
    }
    
    @Override
    public String toString() {
        return String.format(
            "InvalidQRCodeException[qrCodeId=%s, errorReason=%s, message=%s, severity=%s, canRegenerate=%s]",
            qrCodeId, errorReason, getErrorMessage(), getSeverity(), canRegenerate()
        );
    }
}