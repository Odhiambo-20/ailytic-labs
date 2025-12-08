package com.allyticlabs.backend.service;

import com.allyticlabs.backend.config.MpesaConfig;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentEncryptionService encryptionService;
    private final PaymentValidationService validationService;
    private final MpesaService mpesaService;
    private final StripeService stripeService;
    private final QRCodeService qrCodeService;
    private final MpesaConfig mpesaConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Constructor with @Lazy annotation to break circular dependency
    public PaymentService(
            PaymentRepository paymentRepository,
            TransactionRepository transactionRepository,
            PaymentEncryptionService encryptionService,
            PaymentValidationService validationService,
            @Lazy MpesaService mpesaService,  // Add @Lazy here
            StripeService stripeService,
            QRCodeService qrCodeService,
            MpesaConfig mpesaConfig,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.encryptionService = encryptionService;
        this.validationService = validationService;
        this.mpesaService = mpesaService;
        this.stripeService = stripeService;
        this.qrCodeService = qrCodeService;
        this.mpesaConfig = mpesaConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        log.info("Initiating payment for amount: {} {}", request.getAmount(), request.getCurrency());

        validationService.validatePaymentRequest(request);

        String paymentId = UUID.randomUUID().toString();
        request.setPaymentId(paymentId);

        String idempotencyKey = request.getIdempotencyKey() != null
                ? request.getIdempotencyKey()
                : UUID.randomUUID().toString();

        if (checkDuplicatePayment(idempotencyKey)) {
            log.warn("Duplicate payment detected: {}", idempotencyKey);
            return getExistingPayment(idempotencyKey);
        }

        Payment payment = Payment.builder()
                .id(paymentId)
                .userId(request.getUserId())
                .amount(request.getAmount().doubleValue())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .merchantId(request.getMerchantId())
                .description(request.getDescription())
                .idempotencyKey(idempotencyKey)
                .build();

        payment.setCreatedAt(Instant.now());
        payment.setTransactionHash(calculateTransactionHash(payment));

        paymentRepository.save(payment);

        logPaymentTransaction(paymentId, "INITIATED", "Payment initiated", request.getIpAddress());

        PaymentResponse response = routeToProvider(payment, request);

        log.info("Payment initiated successfully: {}", paymentId);
        return response;
    }

    private PaymentResponse routeToProvider(Payment payment, PaymentRequest request) {
        PaymentMethod method = payment.getPaymentMethod();

        try {
            switch (method) {
                case MPESA:
                    return mpesaService.initiateSTKPush(request);
                case STRIPE:
                case CARD:
                    return stripeService.createPaymentIntent(request);
                default:
                    throw new PaymentException("Unsupported payment method: " + method);
            }
        } catch (Exception e) {
            log.error("Error routing payment to provider", e);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new PaymentException("Payment routing failed: " + e.getMessage());
        }
    }

    @Transactional
    public void completePayment(String paymentId, PaymentStatus status, String message) {
        log.info("Completing payment: {} with status: {}", paymentId, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        payment.setStatus(status);
        payment.setUpdatedAt(Instant.now());

        if (status == PaymentStatus.SUCCESS) {
            payment.setCompletedAt(Instant.now());
        }

        paymentRepository.save(payment);

        logPaymentTransaction(paymentId, status.name(), message, null);

        log.info("Payment completed: {}", paymentId);
    }

    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = getPayment(paymentId);
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus())
                .amount(BigDecimal.valueOf(payment.getAmount()))
                .currency(payment.getCurrency())
                .message("Payment retrieved successfully")
                .build();
    }

    public PaymentStatus getPaymentStatus(String paymentId) {
        Payment payment = getPayment(paymentId);
        return payment.getStatus();
    }

    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
    }

    public List<PaymentResponse> getPaymentsByUserId(String userId, int limit) {
        return paymentRepository.findRecentPaymentsByUserId(userId, limit).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<PaymentResponse> getPaymentsByMerchantId(String merchantId, int limit) {
        List<Payment> payments = paymentRepository.findByMerchantId(merchantId);
        return payments.stream()
                .sorted((p1, p2) -> {
                    Instant c1 = p1.getCreatedAt();
                    Instant c2 = p2.getCreatedAt();
                    if (c1 == null && c2 == null) return 0;
                    if (c1 == null) return 1;
                    if (c2 == null) return -1;
                    return c2.compareTo(c1);
                })
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<Payment> getPaymentsByMerchantId(String merchantId) {
        return paymentRepository.findByMerchantId(merchantId);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @Transactional
    public PaymentResponse cancelPayment(String paymentId, String reason) {
        log.info("Cancelling payment: {}", paymentId);

        Payment payment = getPayment(paymentId);

        if (payment.getStatus() != PaymentStatus.PENDING &&
            payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new PaymentException("Can only cancel pending or processing payments");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);

        logPaymentTransaction(paymentId, "CANCELLED", reason != null ? reason : "Cancelled by user", null);

        return PaymentResponse.builder()
                .paymentId(paymentId)
                .status(PaymentStatus.CANCELLED)
                .message("Payment cancelled")
                .build();
    }

    public PaymentResponse verifyPayment(String paymentId) {
        Payment payment = getPayment(paymentId);
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus())
                .amount(BigDecimal.valueOf(payment.getAmount()))
                .currency(payment.getCurrency())
                .message("Payment verified")
                .build();
    }

    @Transactional
    public PaymentResponse refundPayment(String paymentId, String reason) {
        log.info("Refunding payment: {}", paymentId);

        Payment payment = getPayment(paymentId);

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Can only refund successful payments");
        }

        if (payment.getPaymentMethod() == PaymentMethod.STRIPE || 
            payment.getPaymentMethod() == PaymentMethod.CARD) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setUpdatedAt(Instant.now());
            paymentRepository.save(payment);

            logPaymentTransaction(paymentId, "REFUNDED", reason != null ? reason : "Refund requested", null);

            return PaymentResponse.builder()
                    .paymentId(paymentId)
                    .status(PaymentStatus.REFUNDED)
                    .message("Refund initiated")
                    .build();
        } else {
            throw new PaymentException("Refunds not supported for payment method: " + payment.getPaymentMethod());
        }
    }

    private boolean checkDuplicatePayment(String idempotencyKey) {
        return paymentRepository.findByIdempotencyKey(idempotencyKey).isPresent();
    }

    private PaymentResponse getExistingPayment(String idempotencyKey) {
        Payment payment = paymentRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new PaymentException("Payment not found"));

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus())
                .amount(BigDecimal.valueOf(payment.getAmount()))
                .currency(payment.getCurrency())
                .message("Duplicate payment request")
                .build();
    }

    private String calculateTransactionHash(Payment payment) {
        try {
            Instant createdAt = payment.getCreatedAt();
            String data = payment.getId() + payment.getAmount() + payment.getCurrency() + 
                         payment.getMerchantId() + (createdAt != null ? createdAt.toString() : "");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("Error generating transaction hash", e);
            return UUID.randomUUID().toString();
        }
    }

    private void logPaymentTransaction(String paymentId, String eventType, String message, String ipAddress) {
        try {
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .id(UUID.randomUUID().toString())
                    .paymentId(paymentId)
                    .transactionType(eventType)
                    .metadata(message)
                    .build();
            
            transaction.setCreatedAt(Instant.now());

            transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Error logging transaction", e);
        }
    }

    public Map<String, Object> getPaymentStatistics(String merchantId, String startDate, String endDate) {
        Instant startInstant = startDate != null ? Instant.parse(startDate) : Instant.now().minusSeconds(30 * 24 * 3600);
        Instant endInstant = endDate != null ? Instant.parse(endDate) : Instant.now();
        
        List<Payment> payments = paymentRepository.findByMerchantIdAndDateRange(merchantId, startInstant, endInstant);

        long totalCount = payments.size();
        long successCount = payments.stream().filter(p -> p.getStatus() == PaymentStatus.SUCCESS).count();
        long failedCount = payments.stream().filter(p -> p.getStatus() == PaymentStatus.FAILED).count();

        double totalAmount = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getAmount)
                .sum();

        return Map.of(
                "merchantId", merchantId,
                "totalPayments", totalCount,
                "successfulPayments", successCount,
                "failedPayments", failedCount,
                "totalAmount", totalAmount,
                "startDate", startInstant.toString(),
                "endDate", endInstant.toString()
        );
    }

    private PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus())
                .amount(BigDecimal.valueOf(payment.getAmount()))
                .currency(payment.getCurrency())
                .message("Payment retrieved")
                .build();
    }
}