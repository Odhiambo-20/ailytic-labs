import java.nio.charset.StandardCharsets;
// ============================================================================
// File: service/PaymentService.java
// ============================================================================
package com.allyticlabs.backend.service;

import com.allyticlabs.backend.dto.PaymentRequest;
import com.allyticlabs.backend.dto.PaymentResponse;
import com.allyticlabs.backend.exception.PaymentException;
import com.allyticlabs.backend.exception.ResourceNotFoundException;
import com.allyticlabs.backend.model.Payment;
import com.allyticlabs.backend.model.PaymentMethod;
import com.allyticlabs.backend.model.PaymentStatus;
import com.allyticlabs.backend.model.PaymentTransaction;
import com.allyticlabs.backend.repository.PaymentRepository;
import com.allyticlabs.backend.repository.TransactionRepository;
import com.allyticlabs.backend.util.PaymentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentEncryptionService encryptionService;
    private final PaymentValidationService validationService;
    private final MpesaService mpesaService;
    private final StripeService stripeService;
    private final QRCodeService qrCodeService;

    /**
     * Initiate a new payment
     */
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        log.info("Initiating payment for amount: {} {}", request.getAmount(), request.getCurrency());
        
        // Validate payment request
        validationService.validatePaymentRequest(request);
        
        // Generate unique payment ID
        String paymentId = generatePaymentId();
        String idempotencyKey = request.getIdempotencyKey() != null 
                ? request.getIdempotencyKey() 
                : UUID.randomUUID().toString();
        
        // Check for duplicate payment
        if (isDuplicatePayment(idempotencyKey)) {
            log.warn("Duplicate payment detected: {}", idempotencyKey);
            return getPaymentByIdempotencyKey(idempotencyKey);
        }
        
        // Create payment entity
        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .timestamp(Instant.now().toEpochMilli())
                .userId(request.getUserId())
                .amount(encryptionService.encrypt(request.getAmount()))
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .merchantId(request.getMerchantId())
                .merchantName(request.getMerchantName())
                .description(request.getDescription())
                .customerEmail(encryptionService.encrypt(request.getCustomerEmail()))
                .customerPhone(encryptionService.encrypt(request.getCustomerPhone()))
                .ipAddress(request.getIpAddress())
                .deviceFingerprint(request.getDeviceFingerprint())
                .idempotencyKey(idempotencyKey)
                .metadata(request.getMetadata())
                .createdAt(Instant.now().toString())
                .build();
        
        // Generate transaction hash
        payment.setTransactionHash(generateTransactionHash(payment));
        
        // Save payment
        paymentRepository.save(payment);
        
        // Log transaction
        logTransaction(paymentId, "INITIATED", "Payment initiated", request.getIpAddress());
        
        // Route to appropriate payment provider
        PaymentResponse response = routePayment(payment, request);
        
        log.info("Payment initiated successfully: {}", paymentId);
        return response;
    }

    /**
     * Route payment to appropriate provider
     */
    private PaymentResponse routePayment(Payment payment, PaymentRequest request) {
        PaymentMethod method = payment.getPaymentMethod();
        
        try {
            String credentials = mpesaConfig.getConsumerKey() + ":" + mpesaConfig.getConsumerSecret();
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + encodedCredentials);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    mpesaConfig.getOauthUrl(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            
            JsonNode responseBody = objectMapper.readTree(response.getBody());
            accessToken = responseBody.get("access_token").asText();
            int expiresIn = responseBody.get("expires_in").asInt();
            tokenExpiry = Instant.now().plusSeconds(expiresIn - 60); // Refresh 60s before expiry
            
            log.info("M-Pesa access token obtained");
            return accessToken;
            
        } catch (Exception e) {
            log.error("Error getting M-Pesa access token", e);
            throw new PaymentException("Failed to get access token: " + e.getMessage());
        }
    }

    /**
     * Helper methods
     */
    private String generatePassword() {
        String timestamp = getTimestamp();
        String data = mpesaConfig.getShortCode() + mpesaConfig.getPassKey() + timestamp;
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    private String getTimestamp() {
        return java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(java.time.ZoneId.of("Africa/Nairobi"))
                .format(Instant.now());
    }

    private String formatPhoneNumber(String phone) {
        // Format to 254XXXXXXXXX
        phone = phone.trim().replaceAll("[^0-9]", "");
        if (phone.startsWith("0")) {
            return "254" + phone.substring(1);
        }
        if (!phone.startsWith("254")) {
            return "254" + phone;
        }
        return phone;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }
}
