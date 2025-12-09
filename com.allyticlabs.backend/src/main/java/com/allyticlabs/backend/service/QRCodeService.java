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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QRCodeService {

    private final QRPaymentRepository qrPaymentRepository;
    private final PaymentEncryptionService encryptionService;
    private final TokenGenerator tokenGenerator;
    private final QRCodeGenerator qrCodeGenerator;
    private final MpesaService mpesaService;
    private final StripeService stripeService;
    private final ObjectMapper objectMapper;

    // Explicit constructor with @Lazy annotation to break circular dependency
    public QRCodeService(
            QRPaymentRepository qrPaymentRepository,
            PaymentEncryptionService encryptionService,
            TokenGenerator tokenGenerator,
            QRCodeGenerator qrCodeGenerator,
            @Lazy MpesaService mpesaService,
            StripeService stripeService,
            ObjectMapper objectMapper
    ) {
        this.qrPaymentRepository = qrPaymentRepository;
        this.encryptionService = encryptionService;
        this.tokenGenerator = tokenGenerator;
        this.qrCodeGenerator = qrCodeGenerator;
        this.mpesaService = mpesaService;
        this.stripeService = stripeService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Map<String, Object> generateQRCode(QRPaymentRequest request) {
        log.info("Generating QR code for amount: {} {}", request.getAmount(), request.getCurrency());

        try {
            String qrCodeToken = UUID.randomUUID().toString();
            String totpSecret = tokenGenerator.generateTOTPSecret();
            String totp = tokenGenerator.generateTOTP(totpSecret);

            Instant expiryInstant = Instant.now().plusSeconds(request.getExpiryMinutes() * 60);

            Map<String, Object> qrData = new HashMap<>();
            qrData.put("token", qrCodeToken);
            qrData.put("merchantId", request.getMerchantId());
            qrData.put("amount", request.getAmount());
            qrData.put("currency", request.getCurrency());
            qrData.put("description", request.getDescription());
            qrData.put("timestamp", Instant.now().toEpochMilli());
            qrData.put("totp", totp);

            String encryptedQRData = encryptionService.encrypt(objectMapper.writeValueAsString(qrData));

            // QRCodeGenerator returns a Base64 string, not byte[]
            String qrCodeImageBase64 = qrCodeGenerator.generateQRCode(encryptedQRData, 300, 300);

            QRPayment qrPayment = QRPayment.builder()
                    .qrCodeToken(qrCodeToken)
                    .paymentId(UUID.randomUUID().toString())
                    .merchantId(request.getMerchantId())
                    .merchantName(request.getMerchantName())
                    .amount(request.getAmount().doubleValue()) // FIXED: Convert BigDecimal to Double
                    .currency(request.getCurrency())
                    .status(PaymentStatus.PENDING)
                    .qrCode(qrCodeImageBase64) // Store base64 string
                    .qrCodeImage(qrCodeImageBase64) // FIXED: Store as String (Base64), not byte[]
                    .totpSecret(encryptionService.encrypt(totpSecret))
                    .description(request.getDescription())
                    .maxScans(request.getMaxScans() != null ? request.getMaxScans() : 1)
                    .singleUse(request.getSingleUse() != null ? request.getSingleUse() : true)
                    .scanCount(0)
                    .ipAddress(request.getIpAddress())
                    .build();

            qrPayment.setCreatedAt(Instant.now());
            qrPayment.setExpiresAt(expiryInstant);

            qrPaymentRepository.save(qrPayment);

            log.info("QR code generated successfully: {}", qrCodeToken);

            return Map.of(
                    "qrCodeToken", qrCodeToken,
                    "qrCodeImage", qrCodeImageBase64,
                    "qrCodeData", encryptedQRData,
                    "totp", totp,
                    "expiryTimestamp", expiryInstant.toEpochMilli(),
                    "amount", request.getAmount().toString(),
                    "currency", request.getCurrency(),
                    "merchantName", request.getMerchantName()
            );

        } catch (Exception e) {
            log.error("Error generating QR code", e);
            throw new PaymentException("Failed to generate QR code: " + e.getMessage());
        }
    }

    public byte[] getQRCodeImage(String qrCodeToken) {
        log.info("Fetching QR code image: {}", qrCodeToken);

        QRPayment qrPayment = qrPaymentRepository.findByQRCode(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException(
                        qrCodeToken,
                        InvalidQRCodeException.QRErrorReason.NOT_FOUND,
                        "QR code not found"
                ));

        if (qrPayment.isExpired()) {
            throw new InvalidQRCodeException(
                    qrCodeToken,
                    InvalidQRCodeException.QRErrorReason.EXPIRED,
                    "QR code has expired"
            );
        }

        // FIXED: Use the helper method to decode Base64 string to byte[]
        byte[] imageBytes = qrPayment.getQrCodeImageAsBytes();
        if (imageBytes == null) {
            throw new PaymentException("QR code image data is missing or invalid");
        }
        return imageBytes;
    }

    @Transactional
    public Map<String, Object> scanQRCode(String qrCodeToken, String totp,
                                          String ipAddress, String deviceFingerprint) {
        log.info("Scanning QR code: {}", qrCodeToken);

        QRPayment qrPayment = qrPaymentRepository.findByQRCode(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException(
                        qrCodeToken,
                        InvalidQRCodeException.QRErrorReason.NOT_FOUND,
                        "QR code not found"
                ));

        validateQRCodeInternal(qrPayment, totp);

        qrPayment.setScanCount(qrPayment.getScanCount() + 1);
        qrPayment.setScannedAt(Instant.now());
        qrPayment.setIpAddress(ipAddress);
        qrPayment.setDeviceFingerprint(deviceFingerprint);
        qrPayment.setUpdatedAt(Instant.now());

        qrPaymentRepository.save(qrPayment);

        return Map.of(
                "qrCodeToken", qrCodeToken,
                "paymentId", qrPayment.getPaymentId(),
                "amount", String.valueOf(qrPayment.getAmount()), // Convert Double to String
                "currency", qrPayment.getCurrency(),
                "merchantId", qrPayment.getMerchantId(),
                "merchantName", qrPayment.getMerchantName(),
                "description", qrPayment.getDescription(),
                "status", qrPayment.getStatus().name()
        );
    }

    @Transactional
    public PaymentResponse processQRPayment(String qrCodeToken, String totp, String paymentMethod,
                                           Map<String, String> paymentDetails,
                                           String ipAddress, String deviceFingerprint) {
        log.info("Processing QR payment: {}", qrCodeToken);

        QRPayment qrPayment = qrPaymentRepository.findByQRCode(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException(
                        qrCodeToken,
                        InvalidQRCodeException.QRErrorReason.NOT_FOUND,
                        "QR code not found"
                ));

        validateQRCodeInternal(qrPayment, totp);

        if (qrPayment.isUsed() && qrPayment.getSingleUse()) {
            throw new InvalidQRCodeException(
                    qrCodeToken,
                    InvalidQRCodeException.QRErrorReason.ALREADY_USED,
                    "QR code has already been used"
            );
        }

        qrPayment.setStatus(PaymentStatus.PROCESSING);
        qrPayment.setUsedAt(Instant.now());
        qrPayment.setUpdatedAt(Instant.now());
        qrPaymentRepository.save(qrPayment);

        PaymentResponse response;
        try {
            String amount = String.valueOf(qrPayment.getAmount()); // Convert Double to String

            if ("MPESA".equalsIgnoreCase(paymentMethod)) {
                response = processMpesaQRPayment(qrPayment, paymentDetails, amount);
            } else if ("STRIPE".equalsIgnoreCase(paymentMethod) || "CARD".equalsIgnoreCase(paymentMethod)) {
                response = processStripeQRPayment(qrPayment, paymentDetails, amount);
            } else {
                throw new PaymentException("Unsupported payment method: " + paymentMethod);
            }

            qrPayment.setStatus(PaymentStatus.SUCCESS);
            qrPaymentRepository.save(qrPayment);

            return response;

        } catch (Exception e) {
            log.error("Error processing QR payment", e);
            qrPayment.setStatus(PaymentStatus.FAILED);
            qrPaymentRepository.save(qrPayment);
            throw new PaymentException("Failed to process QR payment: " + e.getMessage());
        }
    }

    public Map<String, Object> getQRPaymentStatus(String qrCodeToken) {
        QRPayment qrPayment = qrPaymentRepository.findByQRCode(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException(
                        qrCodeToken,
                        InvalidQRCodeException.QRErrorReason.NOT_FOUND,
                        "QR code not found"
                ));

        return Map.of(
                "qrCodeToken", qrCodeToken,
                "paymentId", qrPayment.getPaymentId(),
                "status", qrPayment.getStatus().name(),
                "isExpired", qrPayment.isExpired(),
                "isUsed", qrPayment.isUsed(),
                "scanCount", qrPayment.getScanCount(),
                "createdAt", qrPayment.getCreatedAtInstant() != null ? qrPayment.getCreatedAtInstant().toString() : "N/A"
        );
    }

    @Transactional
    public Map<String, String> cancelQRPayment(String qrCodeToken, String reason) {
        QRPayment qrPayment = qrPaymentRepository.findByQRCode(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException(
                        qrCodeToken,
                        InvalidQRCodeException.QRErrorReason.NOT_FOUND,
                        "QR code not found"
                ));

        if (qrPayment.isUsed()) {
            throw new PaymentException("Cannot cancel used QR code");
        }

        qrPayment.setStatus(PaymentStatus.CANCELLED);
        qrPayment.setUpdatedAt(Instant.now());
        qrPaymentRepository.save(qrPayment);

        return Map.of(
                "qrCodeToken", qrCodeToken,
                "status", "cancelled",
                "reason", reason != null ? reason : "Cancelled by merchant"
        );
    }

    public Map<String, Object> validateQRCode(String qrCodeToken, String totp) {
        QRPayment qrPayment = qrPaymentRepository.findByQRCode(qrCodeToken)
                .orElseThrow(() -> new InvalidQRCodeException(
                        qrCodeToken,
                        InvalidQRCodeException.QRErrorReason.NOT_FOUND,
                        "QR code not found"
                ));

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

    public Map<String, Object> getMerchantQRCodes(String merchantId, int limit) {
        List<QRPayment> qrPayments = qrPaymentRepository.findRecentByMerchantId(merchantId, limit);

        List<Map<String, Object>> qrCodes = qrPayments.stream()
                .map(qr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("qrCodeToken", qr.getQrCodeToken());
                    map.put("amount", String.valueOf(qr.getAmount())); // Convert Double to String
                    map.put("currency", qr.getCurrency());
                    map.put("status", qr.getStatus().name());
                    map.put("isExpired", qr.isExpired());
                    map.put("isUsed", qr.isUsed());
                    map.put("createdAt", qr.getCreatedAtInstant() != null ? qr.getCreatedAtInstant().toString() : "N/A");
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("merchantId", merchantId);
        result.put("qrCodes", qrCodes);
        result.put("total", qrCodes.size());
        return result;
    }

    private void validateQRCodeInternal(QRPayment qrPayment, String totp) {
        if (qrPayment.isExpired()) {
            throw new InvalidQRCodeException(
                    qrPayment.getQrCodeToken(),
                    InvalidQRCodeException.QRErrorReason.EXPIRED,
                    "QR code has expired"
            );
        }

        if (qrPayment.getScanCount() >= qrPayment.getMaxScans()) {
            throw new InvalidQRCodeException(
                    qrPayment.getQrCodeToken(),
                    InvalidQRCodeException.QRErrorReason.SCAN_LIMIT_EXCEEDED,
                    "QR code scan limit exceeded"
            );
        }

        String totpSecret = encryptionService.decrypt(qrPayment.getTotpSecret());
        boolean isValidTOTP = tokenGenerator.validateTOTP(totpSecret, totp);

        if (!isValidTOTP) {
            throw new InvalidQRCodeException(
                    qrPayment.getQrCodeToken(),
                    InvalidQRCodeException.QRErrorReason.INVALID_TOTP,
                    "Invalid or expired TOTP"
            );
        }
    }

    private PaymentResponse processMpesaQRPayment(QRPayment qrPayment, Map<String, String> details, String amount) {
        log.info("Processing M-Pesa QR payment");
        return PaymentResponse.builder()
                .paymentId(qrPayment.getPaymentId())
                .status(PaymentStatus.PROCESSING)
                .message("M-Pesa payment initiated")
                .build();
    }

    private PaymentResponse processStripeQRPayment(QRPayment qrPayment, Map<String, String> details, String amount) {
        log.info("Processing Stripe QR payment");
        return PaymentResponse.builder()
                .paymentId(qrPayment.getPaymentId())
                .status(PaymentStatus.PROCESSING)
                .message("Stripe payment initiated")
                .build();
    }
}
