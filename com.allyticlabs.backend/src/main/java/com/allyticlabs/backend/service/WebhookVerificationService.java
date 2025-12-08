package com.allyticlabs.backend.service;

import com.allyticlabs.backend.dto.MpesaCallbackRequest;
import com.allyticlabs.backend.exception.PaymentVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookVerificationService {

    @Value("${mpesa.webhook.secret:default-secret}")
    private String mpesaWebhookSecret;

    @Value("${stripe.webhook.secret:default-secret}")
    private String stripeWebhookSecret;

    // Storage for processed webhooks to prevent replay attacks
    private final Set<String> processedWebhooks = Collections.synchronizedSet(new HashSet<>());

    /**
     * Verify Stripe webhook signature
     */
    public boolean verifyStripeSignature(String payload, String signature) {
        if (signature == null || stripeWebhookSecret == null || stripeWebhookSecret.isEmpty()) {
            log.warn("Stripe signature or secret is missing");
            return false;
        }

        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                stripeWebhookSecret.getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256"
            );
            sha256Hmac.init(secretKey);

            byte[] hash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String calculated = bytesToHex(hash);

            return calculated.equalsIgnoreCase(signature);
        } catch (Exception e) {
            log.error("Error verifying Stripe signature", e);
            return false;
        }
    }

    /**
     * Process Stripe webhook
     */
    public Map<String, Object> processStripeWebhook(String eventId, String eventType, 
                                                     Map<String, String> metadata) {
        log.info("Processing Stripe webhook: {} - {}", eventId, eventType);

        // Check for replay attack
        if (processedWebhooks.contains(eventId)) {
            throw PaymentVerificationException.replayAttack(eventId, "stripe", eventId);
        }

        processedWebhooks.add(eventId);

        Map<String, Object> result = new HashMap<>();
        result.put("eventId", eventId);
        result.put("eventType", eventType);
        result.put("status", "processed");
        result.put("timestamp", System.currentTimeMillis());

        return result;
    }

    /**
     * Validate M-Pesa transaction
     */
    public boolean validateMpesaTransaction(MpesaCallbackRequest request, String signature) {
        if (request == null) {
            log.warn("M-Pesa callback request is null");
            return false;
        }

        if (signature == null || mpesaWebhookSecret == null || mpesaWebhookSecret.isEmpty()) {
            log.warn("M-Pesa signature or secret is missing");
            return false;
        }

        try {
            String payload = request.toString();
            return verifyMpesaWebhook(payload, signature);
        } catch (Exception e) {
            log.error("Error validating M-Pesa transaction", e);
            return false;
        }
    }

    /**
     * Process M-Pesa confirmation callback
     */
    public Map<String, Object> processMpesaConfirmation(MpesaCallbackRequest request, 
                                                        String transactionId,
                                                        Map<String, String> metadata) {
        log.info("Processing M-Pesa confirmation: {}", transactionId);

        // Check for replay attack
        if (processedWebhooks.contains(transactionId)) {
            throw PaymentVerificationException.replayAttack(transactionId, "mpesa", transactionId);
        }

        processedWebhooks.add(transactionId);

        Map<String, Object> result = new HashMap<>();
        result.put("transactionId", transactionId);
        result.put("status", "confirmed");
        result.put("timestamp", System.currentTimeMillis());
        result.put("resultCode", request.getResultCode());
        result.put("resultDesc", request.getResultDesc());

        return result;
    }

    /**
     * Process M-Pesa timeout
     */
    public Map<String, Object> processMpesaTimeout(MpesaCallbackRequest request, String transactionId) {
        log.info("Processing M-Pesa timeout: {}", transactionId);

        Map<String, Object> result = new HashMap<>();
        result.put("transactionId", transactionId);
        result.put("status", "timeout");
        result.put("timestamp", System.currentTimeMillis());
        result.put("message", "Transaction timed out");

        return result;
    }

    /**
     * Process M-Pesa result callback
     */
    public Map<String, Object> processMpesaResult(MpesaCallbackRequest request,
                                                   String transactionId,
                                                   Map<String, String> metadata) {
        log.info("Processing M-Pesa result: {}", transactionId);

        Map<String, Object> result = new HashMap<>();
        result.put("transactionId", transactionId);
        result.put("status", "completed");
        result.put("timestamp", System.currentTimeMillis());
        result.put("resultCode", request.getResultCode());
        result.put("resultDesc", request.getResultDesc());

        return result;
    }

    /**
     * Get webhook logs
     */
    public List<Map<String, Object>> getWebhookLogs(String transactionId, int limit) {
        log.info("Fetching webhook logs for transaction: {}, limit: {}", transactionId, limit);

        // This is a placeholder - in production, you'd fetch from database
        List<Map<String, Object>> logs = new ArrayList<>();
        
        Map<String, Object> log = new HashMap<>();
        log.put("transactionId", transactionId);
        log.put("timestamp", System.currentTimeMillis());
        log.put("status", "processed");
        log.put("provider", "mpesa");
        
        logs.add(log);
        
        return logs;
    }

    /**
     * Retry webhook processing
     */
    public Map<String, Object> retryWebhook(String webhookId) {
        log.info("Retrying webhook: {}", webhookId);

        // Remove from processed set to allow retry
        processedWebhooks.remove(webhookId);

        Map<String, Object> result = new HashMap<>();
        result.put("webhookId", webhookId);
        result.put("status", "retry_initiated");
        result.put("timestamp", System.currentTimeMillis());

        return result;
    }

    /**
     * Verify M-Pesa webhook with HMAC
     */
    private boolean verifyMpesaWebhook(String payload, String signature) {
        if (signature == null || mpesaWebhookSecret == null || mpesaWebhookSecret.isEmpty()) {
            return false;
        }

        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                mpesaWebhookSecret.getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256"
            );
            sha256Hmac.init(secretKey);

            byte[] hash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String calculated = bytesToHex(hash);

            return calculated.equalsIgnoreCase(signature);
        } catch (Exception e) {
            log.error("Error verifying M-Pesa webhook", e);
            return false;
        }
    }

    /**
     * Convert byte array to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Clear processed webhooks cache (for testing or maintenance)
     */
    public void clearProcessedWebhooks() {
        processedWebhooks.clear();
        log.info("Cleared processed webhooks cache");
    }
}