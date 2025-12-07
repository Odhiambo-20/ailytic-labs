package com.allyticlabs.backend.exception;

import lombok.Getter;
import java.time.Instant;

/**
 * Base exception class for all payment-related errors
 * Provides detailed error information for debugging and user feedback
 */
@Getter
public class PaymentException extends RuntimeException {
    
    private final String errorCode;
    private final String errorMessage;
    private final String transactionId;
    private final PaymentErrorType errorType;
    private final Instant timestamp;
    private final Object additionalData;
    
    /**
     * Payment error types for categorization
     */
    public enum PaymentErrorType {
        VALIDATION_ERROR,           // Invalid input data
        AUTHENTICATION_ERROR,       // Authentication failed
        AUTHORIZATION_ERROR,        // Insufficient permissions
        INSUFFICIENT_FUNDS,         // Not enough balance
        PAYMENT_GATEWAY_ERROR,      // External gateway error
        NETWORK_ERROR,              // Network connectivity issue
        TIMEOUT_ERROR,              // Request timeout
        DUPLICATE_TRANSACTION,      // Duplicate payment attempt
        TRANSACTION_DECLINED,       // Payment declined by provider
        INVALID_CREDENTIALS,        // Invalid API credentials
        RATE_LIMIT_EXCEEDED,        // Too many requests
        CONFIGURATION_ERROR,        // System configuration issue
        ENCRYPTION_ERROR,           // Encryption/decryption failed
        DATABASE_ERROR,             // Database operation failed
        REFUND_ERROR,               // Refund processing error
        WEBHOOK_ERROR,              // Webhook processing error
        UNKNOWN_ERROR               // Unclassified error
    }
    
    /**
     * Constructor with all parameters
     */
    public PaymentException(String errorCode, String errorMessage, String transactionId, 
                           PaymentErrorType errorType, Object additionalData, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.transactionId = transactionId;
        this.errorType = errorType;
        this.timestamp = Instant.now();
        this.additionalData = additionalData;
    }
    
    /**
     * Constructor without additional data
     */
    public PaymentException(String errorCode, String errorMessage, String transactionId, 
                           PaymentErrorType errorType, Throwable cause) {
        this(errorCode, errorMessage, transactionId, errorType, null, cause);
    }
    
    /**
     * Constructor without cause
     */
    public PaymentException(String errorCode, String errorMessage, String transactionId, 
                           PaymentErrorType errorType) {
        this(errorCode, errorMessage, transactionId, errorType, null, null);
    }
    
    /**
     * Constructor with minimal parameters
     */
    public PaymentException(String errorMessage, PaymentErrorType errorType) {
        this(null, errorMessage, null, errorType, null, null);
    }
    
    /**
     * Constructor with message only
     */
    public PaymentException(String errorMessage) {
        this(null, errorMessage, null, PaymentErrorType.UNKNOWN_ERROR, null, null);
    }
    
    /**
     * Static factory methods for common payment errors
     */
    
    public static PaymentException validationError(String message, String transactionId) {
        return new PaymentException(
            "VALIDATION_ERROR", 
            message, 
            transactionId, 
            PaymentErrorType.VALIDATION_ERROR
        );
    }
    
    public static PaymentException insufficientFunds(String transactionId, Double amount) {
        return new PaymentException(
            "INSUFFICIENT_FUNDS",
            String.format("Insufficient funds for transaction. Amount: %.2f", amount),
            transactionId,
            PaymentErrorType.INSUFFICIENT_FUNDS,
            amount,
            null
        );
    }
    
    public static PaymentException transactionDeclined(String transactionId, String reason) {
        return new PaymentException(
            "TRANSACTION_DECLINED",
            "Transaction declined: " + reason,
            transactionId,
            PaymentErrorType.TRANSACTION_DECLINED,
            reason,
            null
        );
    }
    
    public static PaymentException gatewayError(String gateway, String message, String transactionId) {
        return new PaymentException(
            "GATEWAY_ERROR",
            String.format("%s gateway error: %s", gateway, message),
            transactionId,
            PaymentErrorType.PAYMENT_GATEWAY_ERROR
        );
    }
    
    public static PaymentException networkError(String transactionId, Throwable cause) {
        return new PaymentException(
            "NETWORK_ERROR",
            "Network communication failed",
            transactionId,
            PaymentErrorType.NETWORK_ERROR,
            cause
        );
    }
    
    public static PaymentException timeoutError(String transactionId, int timeoutSeconds) {
        return new PaymentException(
            "TIMEOUT_ERROR",
            String.format("Payment request timeout after %d seconds", timeoutSeconds),
            transactionId,
            PaymentErrorType.TIMEOUT_ERROR,
            timeoutSeconds,
            null
        );
    }
    
    public static PaymentException duplicateTransaction(String transactionId) {
        return new PaymentException(
            "DUPLICATE_TRANSACTION",
            "Duplicate transaction detected",
            transactionId,
            PaymentErrorType.DUPLICATE_TRANSACTION
        );
    }
    
    public static PaymentException authenticationError(String message) {
        return new PaymentException(
            "AUTHENTICATION_ERROR",
            message,
            null,
            PaymentErrorType.AUTHENTICATION_ERROR
        );
    }
    
    public static PaymentException authorizationError(String message, String userId) {
        return new PaymentException(
            "AUTHORIZATION_ERROR",
            message,
            null,
            PaymentErrorType.AUTHORIZATION_ERROR,
            userId,
            null
        );
    }
    
    public static PaymentException rateLimitExceeded(String identifier, int maxRequests) {
        return new PaymentException(
            "RATE_LIMIT_EXCEEDED",
            String.format("Rate limit exceeded. Maximum %d requests allowed", maxRequests),
            null,
            PaymentErrorType.RATE_LIMIT_EXCEEDED,
            maxRequests,
            null
        );
    }
    
    public static PaymentException encryptionError(String message, Throwable cause) {
        return new PaymentException(
            "ENCRYPTION_ERROR",
            "Encryption/decryption failed: " + message,
            null,
            PaymentErrorType.ENCRYPTION_ERROR,
            cause
        );
    }
    
    public static PaymentException configurationError(String message) {
        return new PaymentException(
            "CONFIGURATION_ERROR",
            "System configuration error: " + message,
            null,
            PaymentErrorType.CONFIGURATION_ERROR
        );
    }
    
    public static PaymentException refundError(String transactionId, String reason) {
        return new PaymentException(
            "REFUND_ERROR",
            "Refund failed: " + reason,
            transactionId,
            PaymentErrorType.REFUND_ERROR
        );
    }
    
    public static PaymentException webhookError(String message, String webhookId) {
        return new PaymentException(
            "WEBHOOK_ERROR",
            message,
            webhookId,
            PaymentErrorType.WEBHOOK_ERROR
        );
    }
    
    public static PaymentException databaseError(String operation, Throwable cause) {
        return new PaymentException(
            "DATABASE_ERROR",
            "Database operation failed: " + operation,
            null,
            PaymentErrorType.DATABASE_ERROR,
            cause
        );
    }
    
    /**
     * Check if error is retryable
     */
    public boolean isRetryable() {
        return errorType == PaymentErrorType.NETWORK_ERROR ||
               errorType == PaymentErrorType.TIMEOUT_ERROR ||
               errorType == PaymentErrorType.PAYMENT_GATEWAY_ERROR;
    }
    
    /**
     * Check if error requires user action
     */
    public boolean requiresUserAction() {
        return errorType == PaymentErrorType.INSUFFICIENT_FUNDS ||
               errorType == PaymentErrorType.AUTHENTICATION_ERROR ||
               errorType == PaymentErrorType.VALIDATION_ERROR;
    }
    
    /**
     * Get user-friendly error message
     */
    public String getUserFriendlyMessage() {
        switch (errorType) {
            case INSUFFICIENT_FUNDS:
                return "Insufficient funds. Please check your account balance.";
            case TRANSACTION_DECLINED:
                return "Transaction was declined. Please try another payment method.";
            case NETWORK_ERROR:
            case TIMEOUT_ERROR:
                return "Connection issue. Please try again in a moment.";
            case VALIDATION_ERROR:
                return "Invalid payment information. Please check your details.";
            case RATE_LIMIT_EXCEEDED:
                return "Too many requests. Please wait a moment and try again.";
            case AUTHENTICATION_ERROR:
                return "Authentication failed. Please verify your credentials.";
            default:
                return "Payment processing failed. Please contact support.";
        }
    }
    
    /**
     * Convert to JSON-friendly error response
     */
    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(
            errorCode,
            errorMessage,
            getUserFriendlyMessage(),
            transactionId,
            errorType.name(),
            timestamp.toString()
        );
    }
    
    /**
     * Inner class for structured error response
     */
    @Getter
    public static class ErrorResponse {
        private final String errorCode;
        private final String errorMessage;
        private final String userMessage;
        private final String transactionId;
        private final String errorType;
        private final String timestamp;
        
        public ErrorResponse(String errorCode, String errorMessage, String userMessage,
                           String transactionId, String errorType, String timestamp) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.userMessage = userMessage;
            this.transactionId = transactionId;
            this.errorType = errorType;
            this.timestamp = timestamp;
        }
    }
    
    @Override
    public String toString() {
        return String.format(
            "PaymentException[errorCode=%s, errorType=%s, transactionId=%s, message=%s, timestamp=%s]",
            errorCode, errorType, transactionId, errorMessage, timestamp
        );
    }
}