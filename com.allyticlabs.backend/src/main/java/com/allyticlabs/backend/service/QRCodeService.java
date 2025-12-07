// ============================================================================
// File: service/QRCodeService.java
// ============================================================================
package com.allyticlabs.backend.service;

import com.allyticlabs.backend.dto.PaymentResponse;
import com.allyticlabs.backend.dto.QRPaymentRequest;
import com.allyticlabs.backend.exception.InvalidQRCodeException;
import com.allyticlabs.backend.exception.PaymentException;
import com.allyticlabs.backend.model.PaymentStatus;
import com.allyticlabs.backend.model.QRPayment;
import com.allyticlabs.backend.repository.QRPaymentRepository;
import com.allyticlabs.backend.security.TokenGenerator;
import com.allyticlabs.backend.util.QRCodeGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QRCodeService {

    private final QRPaymentRepository qrPaymentRepository;
    private final PaymentEncryptionService encryptionService;
    private final TokenGenerator tokenGenerator;
    private final QRCodeGenerator qrCodeGenerator;
    private final MpesaService mpesaService;
    private final StripeService stripeService;
    private final ObjectMapper objectMapper;

    /**
     * Generate QR code for payment
     */
    @Transactional
    public Map<String, Object> generateQRCode(QRPaymentRequest request) {
        log.info("Generating QR code for amount: {} {}", request.getAmount(), request.getCurrency());

        try {
            // Generate unique token and TOTP secret
            String qrCodeToken = UUID.randomUUID().toString();
            String totpSecret = tokenGenerator.generateTOTPSecret();
            String totp = tokenGenerator.generateTOTP(totpSecret);

            // Calculate expiry (default 15 minutes)
            long expiryTimestamp = Instant.now().plusSeconds(request.getExpiryMinutes() * 60).toEpochMilli();

            // Create QR payment data
            Map<String, Object> qrData = new HashMap<>();
            qrData.put("token", qrCodeToken);
            qrData.put("merchantId", request.getMerchantId());
            qrData.put("amount", request.getAmount());
            qrData.put("currency", request.getCurrency());
            qrData.put("description", request.getDescription());
            qrData.put("timestamp", Instant.now().toEpochMilli());
            qrData.put("totp", totp);

            // Encrypt QR data
            String encryptedQRData = encryptionService.encrypt(objectMapper.writeValueAsString(qrData));

            // Generate QR code image
            byte[] qrCodeImage = qrCodeGenerator.generateQRCode(encryptedQRData, 300, 300);
            String qrCodeImageBase64 = java.util.Base64.getEncoder().encodeToString(qrCodeImage);

            // Save QR payment record
            QRPayment qrPayment = QRPayment.builder()
                    .qrCodeToken(qrCodeToken)
                    .expiryTimestamp(expiryTimestamp)
                    .paymentId(UUID.randomUUID().toString())
                    .merchantId(request.getMerchantId())
                    .merchantName(request.getMerchantName())
                    .amount(encryptionService.encrypt(request.getAmount()))
                    .currency(request.getCurrency())
                    .status(PaymentStatus.PENDING)
                    .qrCodeData(encryptedQRData)
                    .qrCodeImage(qrCodeImageBase64)
                    .totp(totp)
                    .totpSecret(encryptionService.encrypt(totpSecret))
                    .description(request.getDescription())
                    .maxScans(request.getMaxScans() != null ? request.getMaxScans() : 1)
                    .singleUse(request.getSingleUse() != null ? request.getSingleUse() : true)
                    .scanCount(0)
                    .ipAddress(request.getIpAddress())
                    .createdAt(Instant.now().toString())
                    .build();

            qrPaymentRepository.save(qrPayment);

            log.info("QR code generated successfully: {}", qrCodeToken);

            return Map.of(
                    "qrCodeToken", qrCodeToken,
                    "qrCodeImage", qrCodeImageBase64,
                    "qrCodeData", encryptedQRData,
                    "totp", totp,
                    "expiryTimestamp", expiryTimestamp,
                    "amount", request.getAmount(),
                    "currency", request.getCurrency(),
                    "merchantName", request.getMerchantName()
            );

        } catch (Exception e) {
            log.error("Error generating QR code", e);
            throw new PaymentException("Failed to generate QR code: " + e.getMessage());
        }
    }

    /**
     * Get QR code image
     */
    public byte[] getQRCodeImage(String qrCodeToken) {
        log.info("Fetching QR code image: {}", qrCodeToken);

        QRPayment qrPayment = qrPaymentRepository.findByQrCodeToken(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException("QR code not found"));

        if (qrPayment.isExpired()) {
            throw new InvalidQRCodeException("QR code has expired");
        }

        return java.util.Base64.getDecoder().decode(qrPayment.getQrCodeImage());
    }

    /**
     * Scan QR code and retrieve payment details
     */
    @Transactional
    public Map<String, Object> scanQRCode(String qrCodeToken, String totp,
                                          String ipAddress, String deviceFingerprint) {
        log.info("Scanning QR code: {}", qrCodeToken);

        QRPayment qrPayment = qrPaymentRepository.findByQrCodeToken(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException("QR code not found"));

        // Validate QR code
        validateQRCodeInternal(qrPayment, totp);

        // Update scan count
        qrPayment.setScanCount(qrPayment.getScanCount() + 1);
        qrPayment.setScannedAt(Instant.now().toString());
        qrPayment.setIpAddress(ipAddress);
        qrPayment.setDeviceFingerprint(deviceFingerprint);
        qrPayment.setUpdatedAt(Instant.now().toString());

        qrPaymentRepository.save(qrPayment);

        // Return payment details
        return Map.of(
                "qrCodeToken", qrCodeToken,
                "paymentId", qrPayment.getPaymentId(),
                "amount", encryptionService.decrypt(qrPayment.getAmount()),
                "currency", qrPayment.getCurrency(),
                "merchantId", qrPayment.getMerchantId(),
                "merchantName", qrPayment.getMerchantName(),
                "description", qrPayment.getDescription(),
                "status", qrPayment.getStatus().name()
        );
    }

    /**
     * Process QR code payment
     */
    @Transactional
    public PaymentResponse processQRPayment(String qrCodeToken, String totp, String paymentMethod,
                                           Map<String, String> paymentDetails,
                                           String ipAddress, String deviceFingerprint) {
        log.info("Processing QR payment: {}", qrCodeToken);

        QRPayment qrPayment = qrPaymentRepository.findByQrCodeToken(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException("QR code not found"));

        // Validate QR code
        validateQRCodeInternal(qrPayment, totp);

        // Check if already used
        if (qrPayment.isUsed() && qrPayment.getSingleUse()) {
            throw new InvalidQRCodeException("QR code has already been used");
        }

        // Update QR payment status
        qrPayment.setStatus(PaymentStatus.PROCESSING);
        qrPayment.setUsedAt(Instant.now().toString());
        qrPayment.setUpdatedAt(Instant.now().toString());
        qrPaymentRepository.save(qrPayment);

        // Process payment based on method
        PaymentResponse response;
        try {
            String amount = encryptionService.decrypt(qrPayment.getAmount());

            if ("MPESA".equalsIgnoreCase(paymentMethod)) {
                // Implement M-Pesa payment
                response = processMpesaQRPayment(qrPayment, paymentDetails, amount);
            } else if ("STRIPE".equalsIgnoreCase(paymentMethod) || "CARD".equalsIgnoreCase(paymentMethod)) {
                // Implement Stripe payment
                response = processStripeQRPayment(qrPayment, paymentDetails, amount);
            } else {
                throw new PaymentException("Unsupported payment method: " + paymentMethod);
            }

            // Update QR payment on success
            qrPayment.setStatus(PaymentStatus.COMPLETED);
            qrPaymentRepository.save(qrPayment);

            return response;

        } catch (Exception e) {
            log.error("Error processing QR payment", e);
            qrPayment.setStatus(PaymentStatus.FAILED);
            qrPaymentRepository.save(qrPayment);
            throw new PaymentException("Failed to process QR payment: " + e.getMessage());
        }
    }

    /**
     * Get QR payment status
     */
    public Map<String, Object> getQRPaymentStatus(String qrCodeToken) {
        QRPayment qrPayment = qrPaymentRepository.findByQrCodeToken(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException("QR code not found"));

        return Map.of(
                "qrCodeToken", qrCodeToken,
                "paymentId", qrPayment.getPaymentId(),
                "status", qrPayment.getStatus().name(),
                "isExpired", qrPayment.isExpired(),
                "isUsed", qrPayment.isUsed(),
                "scanCount", qrPayment.getScanCount(),
                "createdAt", qrPayment.getCreatedAt()
        );
    }

    /**
     * Cancel QR payment
     */
    @Transactional
    public Map<String, String> cancelQRPayment(String qrCodeToken, String reason) {
        QRPayment qrPayment = qrPaymentRepository.findByQrCodeToken(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException("QR code not found"));

        if (qrPayment.isUsed()) {
            throw new PaymentException("Cannot cancel used QR code");
        }

        qrPayment.setStatus(PaymentStatus.CANCELLED);
        qrPayment.setUpdatedAt(Instant.now().toString());
        qrPaymentRepository.save(qrPayment);

        return Map.of(
                "qrCodeToken", qrCodeToken,
                "status", "cancelled",
                "reason", reason != null ? reason : "Cancelled by merchant"
        );
    }

    /**
     * Validate QR code
     */
    public Map<String, Object> validateQRCode(String qrCodeToken, String totp) {
        QRPayment qrPayment = qrPaymentRepository.findByQrCodeToken(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException("QR code not found"));

        try {
            validateQRCodeInternal(qrPayment, totp);
            return Map.of(
                    "valid", true,
                    "message", "QR code is valid"
            );
        } catch (Exception e) {
            return Map.of(
                    "valid", false,
                    "message", e.getMessage()
            );
        }
    }

    /**
     * Get merchant's QR codes
     */
    public Map<String, Object> getMerchantQRCodes(String merchantId, int limit) {
        List<QRPayment> qrPayments = qrPaymentRepository.findByMerchantId(merchantId, limit);

        List<Map<String, Object>> qrCodes = qrPayments.stream()
                .map(qr -> Map.of(
                        "qrCodeToken", qr.getQrCodeToken(),
                        "amount", encryptionService.decrypt(qr.getAmount()),
                        "currency", qr.getCurrency(),
                        "status", qr.getStatus().name(),
                        "isExpired", qr.isExpired(),
                        "isUsed", qr.isUsed(),
                        "createdAt", qr.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return Map.of(
                "merchantId", merchantId,
                "qrCodes", qrCodes,
                "total", qrCodes.size()
        );
    }

    /**
     * Helper methods
     */
    private void validateQRCodeInternal(QRPayment qrPayment, String totp) {
        // Check expiry
        if (qrPayment.isExpired()) {
            throw new InvalidQRCodeException("QR code has expired");
        }

        // Check scan limit
        if (qrPayment.getScanCount() >= qrPayment.getMaxScans()) {
            throw new InvalidQRCodeException("QR code scan limit exceeded");
        }

        // Validate TOTP
        String totpSecret = encryptionService.decrypt(qrPayment.getTotpSecret());
        boolean isValidTOTP = tokenGenerator.validateTOTP(totpSecret, totp);

        if (!isValidTOTP) {
            throw new InvalidQRCodeException("Invalid or expired TOTP");
        }
    }

    private PaymentResponse processMpesaQRPayment(QRPayment qrPayment, Map<String, String> details, String amount) {
        // Implementation for M-Pesa QR payment
        log.info("Processing M-Pesa QR payment");
        // Call MpesaService methods here
        return PaymentResponse.builder()
                .paymentId(qrPayment.getPaymentId())
                .status(PaymentStatus.PROCESSING)
                .message("M-Pesa payment initiated")
                .build();
    }

    private PaymentResponse processStripeQRPayment(QRPayment qrPayment, Map<String, String> details, String amount) {
        // Implementation for Stripe QR payment
        log.info("Processing Stripe QR payment");
        // Call StripeService methods here
        return PaymentResponse.builder()
                .paymentId(qrPayment.getPaymentId())
                .status(PaymentStatus.PROCESSING)
                .message("Stripe payment initiated")
                .build();
    }
}
