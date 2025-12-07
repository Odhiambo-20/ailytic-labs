package com.allyticlabs.backend.security;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive audit logging for payment transactions and security events
 * Compliant with PCI DSS logging requirements
 */
@Slf4j
@Component
public class PaymentAuditLogger {
    
    private final ObjectMapper objectMapper;
    private final DateTimeFormatter dateTimeFormatter;
    
    public PaymentAuditLogger() {
        this.objectMapper = new ObjectMapper();
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneId.of("UTC"));
    }
    
    /**
     * Log payment initiation
     */
    public void logPaymentInitiated(String transactionId, String paymentMethod, 
                                    Double amount, String currency, String userId) {
        Map<String, Object> auditData = createBaseAuditLog("PAYMENT_INITIATED");
        auditData.put("transactionId", transactionId);
        auditData.put("paymentMethod", paymentMethod);
        auditData.put("amount", amount);
        auditData.put("currency", currency);
        auditData.put("userId", maskSensitiveData(userId));
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log successful payment completion
     */
    public void logPaymentCompleted(String transactionId, String paymentMethod, 
                                    Double amount, String externalReference) {
        Map<String, Object> auditData = createBaseAuditLog("PAYMENT_COMPLETED");
        auditData.put("transactionId", transactionId);
        auditData.put("paymentMethod", paymentMethod);
        auditData.put("amount", amount);
        auditData.put("externalReference", externalReference);
        auditData.put("status", "SUCCESS");
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log payment failure
     */
    public void logPaymentFailed(String transactionId, String paymentMethod, 
                                String failureReason, String errorCode) {
        Map<String, Object> auditData = createBaseAuditLog("PAYMENT_FAILED");
        auditData.put("transactionId", transactionId);
        auditData.put("paymentMethod", paymentMethod);
        auditData.put("failureReason", failureReason);
        auditData.put("errorCode", errorCode);
        auditData.put("status", "FAILED");
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log QR code generation
     */
    public void logQRCodeGenerated(String qrCodeId, String userId, Double amount, 
                                  String currency, long expiryTimestamp) {
        Map<String, Object> auditData = createBaseAuditLog("QR_CODE_GENERATED");
        auditData.put("qrCodeId", qrCodeId);
        auditData.put("userId", maskSensitiveData(userId));
        auditData.put("amount", amount);
        auditData.put("currency", currency);
        auditData.put("expiryTimestamp", expiryTimestamp);
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log QR code scanned
     */
    public void logQRCodeScanned(String qrCodeId, String scannedByUserId, String ipAddress) {
        Map<String, Object> auditData = createBaseAuditLog("QR_CODE_SCANNED");
        auditData.put("qrCodeId", qrCodeId);
        auditData.put("scannedByUserId", maskSensitiveData(scannedByUserId));
        auditData.put("ipAddress", maskIpAddress(ipAddress));
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log webhook received
     */
    public void logWebhookReceived(String webhookId, String source, String eventType, 
                                  boolean signatureValid) {
        Map<String, Object> auditData = createBaseAuditLog("WEBHOOK_RECEIVED");
        auditData.put("webhookId", webhookId);
        auditData.put("source", source);
        auditData.put("eventType", eventType);
        auditData.put("signatureValid", signatureValid);
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log authentication attempt
     */
    public void logAuthenticationAttempt(String userId, String method, 
                                        boolean success, String ipAddress) {
        Map<String, Object> auditData = createBaseAuditLog("AUTHENTICATION_ATTEMPT");
        auditData.put("userId", maskSensitiveData(userId));
        auditData.put("method", method);
        auditData.put("success", success);
        auditData.put("ipAddress", maskIpAddress(ipAddress));
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log refund initiated
     */
    public void logRefundInitiated(String refundId, String originalTransactionId, 
                                  Double amount, String reason, String initiatedBy) {
        Map<String, Object> auditData = createBaseAuditLog("REFUND_INITIATED");
        auditData.put("refundId", refundId);
        auditData.put("originalTransactionId", originalTransactionId);
        auditData.put("amount", amount);
        auditData.put("reason", reason);
        auditData.put("initiatedBy", maskSensitiveData(initiatedBy));
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log security event (suspicious activity)
     */
    public void logSecurityEvent(String eventType, String description, 
                                 String userId, String ipAddress, String severity) {
        Map<String, Object> auditData = createBaseAuditLog("SECURITY_EVENT");
        auditData.put("eventType", eventType);
        auditData.put("description", description);
        auditData.put("userId", maskSensitiveData(userId));
        auditData.put("ipAddress", maskIpAddress(ipAddress));
        auditData.put("severity", severity);
        
        logAuditEvent(auditData);
        
        // Also log to security-specific logger for high/critical severity
        if ("HIGH".equals(severity) || "CRITICAL".equals(severity)) {
            log.warn("SECURITY ALERT: {} - {} - User: {} - IP: {}", 
                eventType, description, maskSensitiveData(userId), maskIpAddress(ipAddress));
        }
    }
    
    /**
     * Log encryption/decryption operation
     */
    public void logEncryptionOperation(String operation, String dataType, 
                                      boolean success, String algorithm) {
        Map<String, Object> auditData = createBaseAuditLog("ENCRYPTION_OPERATION");
        auditData.put("operation", operation);
        auditData.put("dataType", dataType);
        auditData.put("success", success);
        auditData.put("algorithm", algorithm);
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log API key usage
     */
    public void logApiKeyUsage(String apiKeyHash, String endpoint, 
                              String method, int statusCode, String ipAddress) {
        Map<String, Object> auditData = createBaseAuditLog("API_KEY_USAGE");
        auditData.put("apiKeyHash", apiKeyHash);
        auditData.put("endpoint", endpoint);
        auditData.put("method", method);
        auditData.put("statusCode", statusCode);
        auditData.put("ipAddress", maskIpAddress(ipAddress));
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log rate limit exceeded
     */
    public void logRateLimitExceeded(String identifier, String endpoint, 
                                    String ipAddress, int attemptCount) {
        Map<String, Object> auditData = createBaseAuditLog("RATE_LIMIT_EXCEEDED");
        auditData.put("identifier", maskSensitiveData(identifier));
        auditData.put("endpoint", endpoint);
        auditData.put("ipAddress", maskIpAddress(ipAddress));
        auditData.put("attemptCount", attemptCount);
        
        logAuditEvent(auditData);
        log.warn("Rate limit exceeded for {} at endpoint {} from IP {}", 
            maskSensitiveData(identifier), endpoint, maskIpAddress(ipAddress));
    }
    
    /**
     * Log configuration change
     */
    public void logConfigurationChange(String configKey, String oldValue, 
                                      String newValue, String changedBy) {
        Map<String, Object> auditData = createBaseAuditLog("CONFIGURATION_CHANGE");
        auditData.put("configKey", configKey);
        auditData.put("oldValue", maskSensitiveData(oldValue));
        auditData.put("newValue", maskSensitiveData(newValue));
        auditData.put("changedBy", maskSensitiveData(changedBy));
        
        logAuditEvent(auditData);
    }
    
    /**
     * Log data access
     */
    public void logDataAccess(String dataType, String recordId, 
                             String accessedBy, String operation) {
        Map<String, Object> auditData = createBaseAuditLog("DATA_ACCESS");
        auditData.put("dataType", dataType);
        auditData.put("recordId", recordId);
        auditData.put("accessedBy", maskSensitiveData(accessedBy));
        auditData.put("operation", operation);
        
        logAuditEvent(auditData);
    }
    
    /**
     * Create base audit log structure
     */
    private Map<String, Object> createBaseAuditLog(String eventType) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("eventType", eventType);
        auditData.put("timestamp", dateTimeFormatter.format(Instant.now()));
        auditData.put("timestampMillis", System.currentTimeMillis());
        auditData.put("environment", getEnvironment());
        return auditData;
    }
    
    /**
     * Write audit event to log
     */
    private void logAuditEvent(Map<String, Object> auditData) {
        try {
            String jsonLog = objectMapper.writeValueAsString(auditData);
            log.info("AUDIT: {}", jsonLog);
        } catch (Exception e) {
            log.error("Failed to write audit log", e);
            // Fallback to simple logging
            log.info("AUDIT: {}", auditData);
        }
    }
    
    /**
     * Mask sensitive data for logging (show last 4 characters only)
     */
    private String maskSensitiveData(String data) {
        if (data == null || data.length() <= 4) {
            return "****";
        }
        return "****" + data.substring(data.length() - 4);
    }
    
    /**
     * Mask IP address for privacy (show first two octets only)
     */
    private String maskIpAddress(String ipAddress) {
        if (ipAddress == null) {
            return "***.***.***.**";
        }
        String[] parts = ipAddress.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".***.***";
        }
        return "***.***.***.**";
    }
    
    /**
     * Get current environment
     */
    private String getEnvironment() {
        String env = System.getProperty("spring.profiles.active");
        return env != null ? env : "development";
    }
    
    /**
     * Log batch of events (for high-volume scenarios)
     */
    public void logBatchEvents(Map<String, Object>[] events) {
        for (Map<String, Object> event : events) {
            if (event.containsKey("eventType")) {
                logAuditEvent(event);
            }
        }
    }
}