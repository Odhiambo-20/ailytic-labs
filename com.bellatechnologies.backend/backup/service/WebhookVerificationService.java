import java.nio.charset.StandardCharsets;
// ============================================================================
// File: service/WebhookVerificationService.java
// ============================================================================
package com.bellatechnologies.backend.service;

import com.bellatechnologies.backend.dto.MpesaCallbackRequest;
import com.bellatechnologies.backend.exception.PaymentException;
import com.bellatechnologies.backend.model.Webhook;
import com.bellatechnologies.backend.repository.WebhookRepository;
import com.bellatechnologies.backend.security.HMACValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookVerificationService {

    private final WebhookRepository webhookRepository;
    private final HMACValidator hmacValidator;
    private final MpesaService mpesaService;
    private final StripeService stripeService;
    private final ObjectMapper objectMapper;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @Value("${mpesa.webhook.secret}")
    private String mpesaWebhookSecret;

    /**
     * Verify Stripe webhook signature
     */
    public boolean verifyStripeSignature(String payload, String signature) {
        log.info("Verifying Stripe webhook signature");
        
        try {
            return hmacValidator.validateStripeSignature(payload, signature, stripeWebhookSecret);
        } catch (Exception e) {
            log.error("Stripe signature verification failed", e);
            return false;
        }
    }

    /**
     * Process Stripe webhook
     */
    @Transactional
    public void processStripeWebhook(String payload, String ipAddress, Map<String, String> headers) {
        log.info("Processing Stripe webhook");
        
        String webhookId = UUID.randomUUID().toString();
        
        try {
            // Log webhook
            Webhook webhook = Webhook.builder()
                    .webhookId(webhookId)
                    .timestamp(Instant.now().toEpochMilli())
                    .provider("STRIPE")
                    .payload(payload)
                    .verified(true)
                    .processed(false)
                    .ipAddress(ipAddress)
                    .headers(objectMapper.writeValueAsString(headers))
                    .createdAt(Instant.now().toString())
                    .build();
            
            webhookRepository.save(webhook);
            
            // Process webhook event
            stripeService.processWebhookEvent(payload, headers.get("Stripe-Signature"));
            
            // Update webhook status
            webhook.setProcessed(true);
            webhook.setProcessingStatus("SUCCESS");
            webhook.setProcessedAt(Instant.now().toString());
            webhookRepository.save(webhook);
            
        } catch (Exception e) {
            log.error("Error processing Stripe webhook", e);
            updateWebhookStatus(webhookId, "FAILED", e.getMessage());
            throw new PaymentException("Webhook processing failed");
        }
    }

    /**
     * Validate M-Pesa transaction
     */
    public boolean validateMpesaTransaction(MpesaCallbackRequest request, String ipAddress) {
        log.info("Validating M-Pesa transaction");
        
        try {
            // Implement M-Pesa specific validation logic
            // Check IP whitelist, verify transaction details, etc.
            return true;
        } catch (Exception e) {
            log.error("M-Pesa validation failed", e);
            return false;
        }
    }

    /**
     * Process M-Pesa confirmation
     */
    @Transactional
    public void processMpesaConfirmation(MpesaCallbackRequest request, 
                                         String ipAddress, Map<String, String> headers) {
        log.info("Processing M-Pesa confirmation");
        
        String webhookId = UUID.randomUUID().toString();
        
        try {
            // Log webhook
            Webhook webhook = Webhook.builder()
                    .webhookId(webhookId)
                    .timestamp(Instant.now().toEpochMilli())
                    .provider("MPESA")
                    .eventType("CONFIRMATION")
                    .payload(objectMapper.writeValueAsString(request))
                    .verified(true)
                    .processed(false)
                    .ipAddress(ipAddress)
                    .headers(objectMapper.writeValueAsString(headers))
                    .createdAt(Instant.now().toString())
                    .build();
            
            webhookRepository.save(webhook);
            
            // Process M-Pesa callback
            mpesaService.processSTKCallback(request, ipAddress, headers.get("User-Agent"));
            
            // Update webhook status
            webhook.setProcessed(true);
            webhook.setProcessingStatus("SUCCESS");
            webhook.setProcessedAt(Instant.now().toString());
            webhookRepository.save(webhook);
            
        } catch (Exception e) {
            log.error("Error processing M-Pesa confirmation", e);
            updateWebhookStatus(webhookId, "FAILED", e.getMessage());
            throw new PaymentException("Webhook processing failed");
        }
    }

    /**
     * Process M-Pesa timeout
     */
    @Transactional
    public void processMpesaTimeout(MpesaCallbackRequest request, String ipAddress) {
        log.info("Processing M-Pesa timeout");
        
        String webhookId = UUID.randomUUID().toString();
        
        try {
            Webhook webhook = Webhook.builder()
                    .webhookId(webhookId)
                    .timestamp(Instant.now().toEpochMilli())
                    .provider("MPESA")
                    .eventType("TIMEOUT")
                    .payload(objectMapper.writeValueAsString(request))
                    .verified(true)
                    .processed(true)
                    .processingStatus("SUCCESS")
                    .ipAddress(ipAddress)
                    .createdAt(Instant.now().toString())
                    .build();
            
            webhookRepository.save(webhook);
            
        } catch (Exception e) {
            log.error("Error processing M-Pesa timeout", e);
        }
    }

    /**
     * Process M-Pesa result
     */
    @Transactional
    public void processMpesaResult(MpesaCallbackRequest request, 
                                   String ipAddress, Map<String, String> headers) {
        log.info("Processing M-Pesa result");
        processMpesaConfirmation(request, ipAddress, headers);
    }

    /**
     * Get webhook logs
     */
    public Map<String, Object> getWebhookLogs(String provider, int limit) {
        List<Webhook> webhooks;
        
        if (provider != null && !provider.isEmpty()) {
            webhooks = webhookRepository.findByProvider(provider, limit);
        } else {
            webhooks = webhookRepository.findAll(limit);
        }
        
        List<Map<String, Object>> logs = webhooks.stream()
                .map(w -> Map.of(
                        "webhookId", w.getWebhookId(),
                        "provider", w.getProvider(),
                        "eventType", w.getEventType() != null ? w.getEventType() : "",
                        "verified", w.getVerified(),
                        "processed", w.getProcessed(),
                        "processingStatus", w.getProcessingStatus() != null ? w.getProcessingStatus() : "",
                        "createdAt", w.getCreatedAt()
                ))
                .collect(Collectors.toList());
        
        return Map.of(
                "webhooks", logs,
                "total", logs.size()
        );
    }

    /**
     * Retry failed webhook
     */
    @Transactional
    public void retryWebhook(String webhookId) {
        Webhook webhook = webhookRepository.findById(webhookId)
                .orElseThrow(() -> new PaymentException("Webhook not found"));
        
        if (webhook.getProcessed() && "SUCCESS".equals(webhook.getProcessingStatus())) {
            throw new PaymentException("Webhook already processed successfully");
        }
        
        webhook.setRetryCount(webhook.getRetryCount() != null ? webhook.getRetryCount() + 1 : 1);
        webhookRepository.save(webhook);
        
        // Retry processing based on provider
        // Implementation depends on webhook type
        log.info("Webhook retry initiated: {}", webhookId);
    }

    /**
     * Helper methods
     */
    private void updateWebhookStatus(String webhookId, String status, String errorMessage) {
        try {
            Webhook webhook = webhookRepository.findById(webhookId).orElse(null);
            if (webhook != null) {
                webhook.setProcessingStatus(status);
                webhook.setErrorMessage(errorMessage);
                webhookRepository.save(webhook);
            }
        } catch (Exception e) {
            log.error("Error updating webhook status", e);
        }
    }
}