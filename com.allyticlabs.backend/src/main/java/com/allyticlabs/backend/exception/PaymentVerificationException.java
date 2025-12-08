package com.allyticlabs.backend.exception;

import lombok.Getter;
import java.time.Instant;

/**
 * Exception thrown when payment verification fails
 * Handles webhook verification, signature validation, and payment status confirmation errors
 */
@Getter
public class PaymentVerificationException extends PaymentException {

    private final String paymentId;
    private final String providerId;
    private final VerificationErrorReason verificationReason;
    private final String expectedValue;
    private final String actualValue;
    private final String webhookId;

    /**
     * Specific reasons for payment verification failure
     */
    public enum VerificationErrorReason {
        SIGNATURE_MISMATCH,         // Webhook signature doesn't match
        INVALID_TIMESTAMP,          // Timestamp is invalid or too old
        REPLAY_ATTACK,              // Duplicate webhook/request detected
        INVALID_PROVIDER,           // Payment provider not recognized
        STATUS_MISMATCH,            // Payment status doesn't match expected
        AMOUNT_MISMATCH,            // Payment amount doesn't match
        CURRENCY_MISMATCH,          // Currency doesn't match expected
        MERCHANT_MISMATCH,          // Merchant ID doesn't match
        CALLBACK_URL_INVALID,       // Invalid callback URL
        MISSING_SIGNATURE,          // Required signature is missing
        MISSING_PARAMETERS,         // Required verification parameters missing
        CHECKSUM_FAILED,            // Checksum verification failed
        HASH_MISMATCH,              // Hash value doesn't match
        TOKEN_EXPIRED,              // Verification token has expired
        TOKEN_INVALID,              // Verification token is invalid
        PROVIDER_ERROR,             // Error from payment provider
        TIMEOUT,                    // Verification timeout
        UNKNOWN_TRANSACTION         // Transaction not found for verification
    }

    /**
     * Constructor with all parameters
     */
    public PaymentVerificationException(String paymentId, String providerId,
                                       VerificationErrorReason verificationReason,
                                       String message, String expectedValue,
                                       String actualValue, String webhookId, Throwable cause) {
        super(
            "VERIFICATION_FAILED",
            message,
            paymentId,
            PaymentErrorType.WEBHOOK_ERROR,
            cause
        );
        this.paymentId = paymentId;
        this.providerId = providerId;
        this.verificationReason = verificationReason;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
        this.webhookId = webhookId;
    }

    /**
     * Constructor without cause
     */
    public PaymentVerificationException(String paymentId, String providerId,
                                       VerificationErrorReason verificationReason,
                                       String message, String expectedValue, String actualValue) {
        this(paymentId, providerId, verificationReason, message,
             expectedValue, actualValue, null, null);
    }

    /**
     * Constructor with minimal parameters
     */
    public PaymentVerificationException(String paymentId, VerificationErrorReason verificationReason,
                                       String message) {
        this(paymentId, null, verificationReason, message, null, null, null, null);
    }

    /**
     * Static factory methods for common verification errors
     */

    public static PaymentVerificationException signatureMismatch(String webhookId,
                                                                String providerId,
                                                                String expectedSignature,
                                                                String actualSignature) {
        return new PaymentVerificationException(
            null,
            providerId,
            VerificationErrorReason.SIGNATURE_MISMATCH,
            String.format("%s webhook signature verification failed", providerId),
            expectedSignature,
            actualSignature,
            webhookId,
            null
        );
    }

    public static PaymentVerificationException invalidTimestamp(String webhookId,
                                                               String providerId,
                                                               long timestamp) {
        long currentTime = Instant.now().toEpochMilli();
        long difference = Math.abs(currentTime - timestamp);
        return new PaymentVerificationException(
            null,
            providerId,
            VerificationErrorReason.INVALID_TIMESTAMP,
            String.format("Webhook timestamp is invalid or too old. Difference: %d ms", difference),
            String.valueOf(currentTime),
            String.valueOf(timestamp),
            webhookId,
            null
        );
    }

    public static PaymentVerificationException replayAttack(String webhookId,
                                                           String providerId,
                                                           String nonce) {
        return new PaymentVerificationException(
            null,
            providerId,
            VerificationErrorReason.REPLAY_ATTACK,
            String.format("Replay attack detected - webhook already processed. Nonce: %s", nonce),
            null,
            null,
            webhookId,
            null
        );
    }

    public static PaymentVerificationException invalidProvider(String providerId) {
        return new PaymentVerificationException(
            null,
            providerId,
            VerificationErrorReason.INVALID_PROVIDER,
            String.format("Payment provider '%s' is not recognized or not configured", providerId),
            null,
            null,
            null,
            null
        );
    }

    public static PaymentVerificationException statusMismatch(String paymentId,
                                                             String expectedStatus,
                                                             String actualStatus) {
        return new PaymentVerificationException(
            paymentId,
            null,
            VerificationErrorReason.STATUS_MISMATCH,
            "Payment status verification failed",
            expectedStatus,
            actualStatus,
            null,
            null
        );
    }

    public static PaymentVerificationException amountMismatch(String paymentId,
                                                             Double expectedAmount,
                                                             Double actualAmount) {
        return new PaymentVerificationException(
            paymentId,
            null,
            VerificationErrorReason.AMOUNT_MISMATCH,
            String.format("Payment amount verification failed. Expected: %.2f, Actual: %.2f",
                expectedAmount, actualAmount),
            String.valueOf(expectedAmount),
            String.valueOf(actualAmount),
            null,
            null
        );
    }

    public static PaymentVerificationException currencyMismatch(String paymentId,
                                                               String expectedCurrency,
                                                               String actualCurrency) {
        return new PaymentVerificationException(
            paymentId,
            null,
            VerificationErrorReason.CURRENCY_MISMATCH,
            "Currency verification failed",
            expectedCurrency,
            actualCurrency,
            null,
            null
        );
    }

    public static PaymentVerificationException merchantMismatch(String paymentId,
                                                               String expectedMerchantId,
                                                               String actualMerchantId) {
        return new PaymentVerificationException(
            paymentId,
            null,
            VerificationErrorReason.MERCHANT_MISMATCH,
            "Merchant ID verification failed",
            expectedMerchantId,
            actualMerchantId,
            null,
            null
        );
    }

    public static PaymentVerificationException missingSignature(String webhookId, String providerId) {
        return new PaymentVerificationException(
            null,
            providerId,
            VerificationErrorReason.MISSING_SIGNATURE,
            String.format("Required signature is missing from %s webhook", providerId),
            null,
            null,
            webhookId,
            null
        );
    }

    public static PaymentVerificationException missingParameters(String webhookId,
                                                                String providerId,
                                                                String missingParams) {
        return new PaymentVerificationException(
            null,
            providerId,
            VerificationErrorReason.MISSING_PARAMETERS,
            String.format("Required parameters missing: %s", missingParams),
            null,
            null,
            webhookId,
            null
        );
    }

    public static PaymentVerificationException checksumFailed(String paymentId,
                                                             String expectedChecksum,
                                                             String actualChecksum) {
        return new PaymentVerificationException(
            paymentId,
            null,
            VerificationErrorReason.CHECKSUM_FAILED,
            "Checksum verification failed - data may be corrupted",
            expectedChecksum,
            actualChecksum,
            null,
            null
        );
    }

    public static PaymentVerificationException hashMismatch(String paymentId,
                                                           String expectedHash,
                                                           String actualHash) {
        return new PaymentVerificationException(
            paymentId,
            null,
            VerificationErrorReason.HASH_MISMATCH,
            "Hash verification failed - possible data tampering",
            expectedHash,
            actualHash,
            null,
            null
        );
    }

    public static PaymentVerificationException tokenExpired(String paymentId,
                                                           Instant expiryTime) {
        return new PaymentVerificationException(
            paymentId,
            VerificationErrorReason.TOKEN_EXPIRED,
            String.format("Verification token expired at %s", expiryTime)
        );
    }

    public static PaymentVerificationException tokenInvalid(String paymentId, String reason) {
        return new PaymentVerificationException(
            paymentId,
            VerificationErrorReason.TOKEN_INVALID,
            "Verification token is invalid: " + reason
        );
    }

    public static PaymentVerificationException providerError(String paymentId,
                                                            String providerId,
                                                            String errorMessage,
                                                            Throwable cause) {
        return new PaymentVerificationException(
            paymentId,
            providerId,
            VerificationErrorReason.PROVIDER_ERROR,
            String.format("%s provider error: %s", providerId, errorMessage),
            null,
            null,
            null,
            cause
        );
    }

    public static PaymentVerificationException timeout(String paymentId,
                                                      String providerId,
                                                      int timeoutSeconds) {
        return new PaymentVerificationException(
            paymentId,
            providerId,
            VerificationErrorReason.TIMEOUT,
            String.format("Verification timeout after %d seconds", timeoutSeconds),
            null,
            null,
            null,
            null
        );
    }

    public static PaymentVerificationException unknownTransaction(String paymentId) {
        return new PaymentVerificationException(
            paymentId,
            VerificationErrorReason.UNKNOWN_TRANSACTION,
            "Transaction not found or does not exist"
        );
    }

    /**
     * Check if this is a security-related verification error
     */
    public boolean isSecurityError() {
        return verificationReason == VerificationErrorReason.SIGNATURE_MISMATCH ||
               verificationReason == VerificationErrorReason.REPLAY_ATTACK ||
               verificationReason == VerificationErrorReason.HASH_MISMATCH ||
               verificationReason == VerificationErrorReason.CHECKSUM_FAILED ||
               verificationReason == VerificationErrorReason.MERCHANT_MISMATCH;
    }

    /**
     * Check if this error should trigger an alert
     */
    public boolean shouldAlert() {
        return isSecurityError() ||
               verificationReason == VerificationErrorReason.AMOUNT_MISMATCH;
    }

    /**
     * Check if verification can be retried
     */
    public boolean canRetry() {
        return verificationReason == VerificationErrorReason.TIMEOUT ||
               verificationReason == VerificationErrorReason.PROVIDER_ERROR;
    }

    /**
     * Get severity level of the verification error
     */
    public VerificationSeverity getSeverity() {
        if (isSecurityError()) {
            return VerificationSeverity.CRITICAL;
        } else if (verificationReason == VerificationErrorReason.AMOUNT_MISMATCH ||
                   verificationReason == VerificationErrorReason.STATUS_MISMATCH ||
                   verificationReason == VerificationErrorReason.CURRENCY_MISMATCH) {
            return VerificationSeverity.HIGH;
        } else if (verificationReason == VerificationErrorReason.MISSING_PARAMETERS ||
                   verificationReason == VerificationErrorReason.INVALID_TIMESTAMP) {
            return VerificationSeverity.MEDIUM;
        } else {
            return VerificationSeverity.LOW;
        }
    }

    public enum VerificationSeverity {
        LOW,        // Minor validation issues
        MEDIUM,     // Missing or invalid data
        HIGH,       // Payment data mismatch
        CRITICAL    // Security violations
    }

    /**
     * Get recommended action based on error type
     */
    public String getRecommendedAction() {
        switch (verificationReason) {
            case SIGNATURE_MISMATCH:
            case HASH_MISMATCH:
            case CHECKSUM_FAILED:
                return "Block and investigate - possible security breach or data tampering";
            case REPLAY_ATTACK:
                return "Block request - duplicate webhook detected";
            case INVALID_TIMESTAMP:
                return "Reject webhook - timestamp outside acceptable window";
            case AMOUNT_MISMATCH:
            case CURRENCY_MISMATCH:
            case MERCHANT_MISMATCH:
                return "Hold transaction and verify with payment provider";
            case STATUS_MISMATCH:
                return "Query provider for current transaction status";
            case MISSING_SIGNATURE:
            case MISSING_PARAMETERS:
                return "Reject request - required data missing";
            case TOKEN_EXPIRED:
            case TOKEN_INVALID:
                return "Request new verification token";
            case TIMEOUT:
            case PROVIDER_ERROR:
                return "Retry verification after delay";
            case UNKNOWN_TRANSACTION:
                return "Transaction may not exist - verify payment ID";
            default:
                return "Investigate and contact payment provider if needed";
        }
    }

    /**
     * Get user-friendly error message
     */
    @Override
    public String getUserFriendlyMessage() {
        switch (verificationReason) {
            case SIGNATURE_MISMATCH:
            case HASH_MISMATCH:
            case CHECKSUM_FAILED:
                return "Payment verification failed for security reasons";
            case REPLAY_ATTACK:
                return "Duplicate payment request detected";
            case AMOUNT_MISMATCH:
            case CURRENCY_MISMATCH:
                return "Payment amount could not be verified";
            case STATUS_MISMATCH:
                return "Payment status could not be confirmed";
            case TIMEOUT:
                return "Payment verification timed out. Please check status later.";
            case PROVIDER_ERROR:
                return "Payment provider verification failed";
            case UNKNOWN_TRANSACTION:
                return "Payment transaction not found";
            case TOKEN_EXPIRED:
            case TOKEN_INVALID:
                return "Payment verification expired. Please try again.";
            default:
                return "Payment verification failed. Please contact support.";
        }
    }

    @Override
    public String toString() {
        return String.format(
            "PaymentVerificationException[paymentId=%s, provider=%s, reason=%s, severity=%s, message=%s]",
            paymentId, providerId, verificationReason, getSeverity(), getErrorMessage()
        );
    }
}