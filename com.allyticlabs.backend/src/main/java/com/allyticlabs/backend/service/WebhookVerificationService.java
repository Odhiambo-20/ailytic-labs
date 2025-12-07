
// ============================================================================
// File: service/WebhookVerificationService.java
// ============================================================================
package com.allyticlabs.backend.service;

import com.allyticlabs.backend.dto.MpesaCallbackRequest;
import com.allyticlabs.backend.exception.PaymentException;
import com.allyticlabs.backend.model.Webhook;
import com.allyticlabs.backend.repository.WebhookRepository;
import com.allyticlabs.backend.security.HMACValidator;
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
}switch (method) {
                case MPESA:
                    return mpesaService.initiateSTKPush(request);
                    
                case STRIPE:
                case CARD:
                    return stripeService.createPaymentIntent(request);
                    
                case QR_CODE:
                    // QR code payments are handled separately
                    return buildPaymentResponse(payment);
                    
                default:
                    throw new PaymentException("Unsupported payment method: " + method);
            }
        } catch (Exception e) {
            log.error("Error routing payment", e);
            updatePaymentStatus(payment.getPaymentId(), PaymentStatus.FAILED, e.getMessage());
            throw new PaymentException("Failed to process payment: " + e.getMessage());
        }
    }

    /**
     * Get payment by ID
     */
    public PaymentResponse getPaymentById(String paymentId) {
        log.info("Fetching payment: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        
        return buildPaymentResponse(payment);
    }

    /**
     * Get payment status
     */
    public PaymentStatus getPaymentStatus(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        
        return payment.getStatus();
    }

    /**
     * Get payments by user ID
     */
    public List<PaymentResponse> getPaymentsByUserId(String userId, int limit) {
        log.info("Fetching payments for user: {}", userId);
        
        List<Payment> payments = paymentRepository.findByUserId(userId, limit);
        
        return payments.stream()
                .map(this::buildPaymentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get payments by merchant ID
     */
    public List<PaymentResponse> getPaymentsByMerchantId(String merchantId, int limit) {
        log.info("Fetching payments for merchant: {}", merchantId);
        
        List<Payment> payments = paymentRepository.findByMerchantId(merchantId, limit);
        
        return payments.stream()
                .map(this::buildPaymentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cancel payment
     */
    @Transactional
    public PaymentResponse cancelPayment(String paymentId, String reason) {
        log.info("Cancelling payment: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentException("Cannot cancel payment in status: " + payment.getStatus());
        }
        
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setFailureReason(reason);
        payment.setUpdatedAt(Instant.now().toString());
        
        paymentRepository.save(payment);
        
        logTransaction(paymentId, "CANCELLED", "Payment cancelled: " + reason, null);
        
        return buildPaymentResponse(payment);
    }

    /**
     * Verify payment
     */
    @Transactional
    public PaymentResponse verifyPayment(String paymentId) {
        log.info("Verifying payment: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentException("Cannot verify payment in status: " + payment.getStatus());
        }
        
        // Verify transaction hash
        String currentHash = generateTransactionHash(payment);
        if (!currentHash.equals(payment.getTransactionHash())) {
            throw new PaymentException("Transaction hash mismatch - possible tampering");
        }
        
        payment.setStatus(PaymentStatus.VERIFIED);
        payment.setUpdatedAt(Instant.now().toString());
        
        paymentRepository.save(payment);
        
        logTransaction(paymentId, "VERIFIED", "Payment verified", null);
        
        return buildPaymentResponse(payment);
    }

    /**
     * Refund payment
     */
    @Transactional
    public PaymentResponse refundPayment(String paymentId, String amount, String reason) {
        log.info("Processing refund for payment: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED && 
            payment.getStatus() != PaymentStatus.VERIFIED) {
            throw new PaymentException("Cannot refund payment in status: " + payment.getStatus());
        }
        
        // Route refund to appropriate provider
        PaymentMethod method = payment.getPaymentMethod();
        
        try {
            switch (method) {
                case MPESA:
                    mpesaService.reverseTransaction(
                            payment.getProviderTransactionId(), 
                            amount != null ? amount : encryptionService.decrypt(payment.getAmount()),
                            reason);
                    break;
                    
                case STRIPE:
                case CARD:
                    stripeService.createRefund(payment.getProviderTransactionId(), 
                            amount != null ? Long.parseLong(amount) : null, 
                            reason);
                    break;
                    
                default:
                    throw new PaymentException("Refund not supported for: " + method);
            }
            
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setUpdatedAt(Instant.now().toString());
            
            paymentRepository.save(payment);
            
            logTransaction(paymentId, "REFUNDED", "Payment refunded: " + reason, null);
            
            return buildPaymentResponse(payment);
            
        } catch (Exception e) {
            log.error("Error processing refund", e);
            throw new PaymentException("Failed to process refund: " + e.getMessage());
        }
    }

    /**
     * Update payment status
     */
    @Transactional
    public void updatePaymentStatus(String paymentId, PaymentStatus status, String message) {
        log.info("Updating payment status: {} to {}", paymentId, status);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        
        payment.setStatus(status);
        payment.setUpdatedAt(Instant.now().toString());
        
        if (status == PaymentStatus.COMPLETED) {
            payment.setCompletedAt(Instant.now().toString());
        }
        
        if (status == PaymentStatus.FAILED) {
            payment.setFailureReason(message);
        }
        
        paymentRepository.save(payment);
        
        logTransaction(paymentId, status.name(), message, null);
    }

    /**
     * Get payment statistics
     */
    public Map<String, Object> getPaymentStatistics(String merchantId, String startDate, String endDate) {
        log.info("Fetching payment statistics");
        
        // Implementation depends on your analytics requirements
        // This is a placeholder
        return Map.of(
                "totalPayments", 0,
                "successfulPayments", 0,
                "failedPayments", 0,
                "totalAmount", 0.0,
                "averageAmount", 0.0
        );
    }

    /**
     * Helper methods
     */
    private String generatePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private String generateTransactionHash(Payment payment) {
        String data = payment.getPaymentId() + 
                      payment.getAmount() + 
                      payment.getCurrency() + 
                      payment.getMerchantId() + 
                      payment.getTimestamp();
        return encryptionService.hash(data);
    }

    private boolean isDuplicatePayment(String idempotencyKey) {
        return paymentRepository.findByIdempotencyKey(idempotencyKey).isPresent();
    }

    private PaymentResponse getPaymentByIdempotencyKey(String idempotencyKey) {
        Payment payment = paymentRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return buildPaymentResponse(payment);
    }

    private void logTransaction(String paymentId, String eventType, String message, String ipAddress) {
        PaymentTransaction transaction = PaymentTransaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .timestamp(Instant.now().toEpochMilli())
                .paymentId(paymentId)
                .eventType(eventType)
                .payload(encryptionService.encrypt(message))
                .ipAddress(ipAddress)
                .verified(true)
                .createdAt(Instant.now().toString())
                .build();
        
        transactionRepository.save(transaction);
    }

    private PaymentResponse buildPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .amount(encryptionService.decrypt(payment.getAmount()))
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .merchantId(payment.getMerchantId())
                .merchantName(payment.getMerchantName())
                .description(payment.getDescription())
                .transactionHash(payment.getTransactionHash())
                .providerTransactionId(payment.getProviderTransactionId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .completedAt(payment.getCompletedAt())
                .build();
    }
}