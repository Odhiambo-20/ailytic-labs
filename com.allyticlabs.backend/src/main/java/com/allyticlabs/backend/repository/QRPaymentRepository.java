package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.QRPayment;
import com.allyticlabs.backend.model.PaymentStatus;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QRPaymentRepository {

    private final DynamoDBMapper dynamoDBMapper;
    private static final int DEFAULT_QR_EXPIRY_MINUTES = 15;

    /**
     * Save QR payment to DynamoDB
     */
    public QRPayment save(QRPayment qrPayment) {
        try {
            if (qrPayment.getCreatedAtInstant() == null) {
                qrPayment.setCreatedAt(Instant.now());
            }
            if (qrPayment.getExpiresAtInstant() == null) {
                qrPayment.setExpiresAt(Instant.now().plusSeconds(DEFAULT_QR_EXPIRY_MINUTES * 60));
            }
            qrPayment.setUpdatedAt(Instant.now());
            
            dynamoDBMapper.save(qrPayment);
            log.debug("Saved QR payment: {}", qrPayment.getId());
            return qrPayment;
        } catch (Exception e) {
            log.error("Error saving QR payment", e);
            throw new RuntimeException("Failed to save QR payment: " + e.getMessage(), e);
        }
    }

    /**
     * Find QR payment by ID
     */
    public Optional<QRPayment> findById(String id) {
        try {
            QRPayment qrPayment = dynamoDBMapper.load(QRPayment.class, id);
            return Optional.ofNullable(qrPayment);
        } catch (Exception e) {
            log.error("Error finding QR payment by ID: {}", id, e);
            return Optional.empty();
        }
    }

    /**
     * Find QR payment by QR code
     */
    public Optional<QRPayment> findByQRCode(String qrCode) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":qrCode", new AttributeValue().withS(qrCode));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("qrCode = :qrCode")
                    .withExpressionAttributeValues(eav)
                    .withLimit(1);

            List<QRPayment> results = dynamoDBMapper.scan(QRPayment.class, scanExpression);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            log.error("Error finding QR payment by qrCode: {}", qrCode, e);
            return Optional.empty();
        }
    }

    /**
     * Find valid (not expired and not used) QR code
     */
    public Optional<QRPayment> findValidQRCode(String qrCode) {
        Optional<QRPayment> qrPaymentOpt = findByQRCode(qrCode);

        if (qrPaymentOpt.isPresent()) {
            QRPayment qrPayment = qrPaymentOpt.get();

            Instant expiresAt = qrPayment.getExpiresAtInstant();
            if (expiresAt != null && Instant.now().isAfter(expiresAt)) {
                return Optional.empty();
            }

            if (qrPayment.isUsed()) {
                return Optional.empty();
            }

            return qrPaymentOpt;
        }

        return Optional.empty();
    }

    /**
     * Find QR payments by merchant ID
     */
    public List<QRPayment> findByMerchantId(String merchantId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":merchantId", new AttributeValue().withS(merchantId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("merchantId = :merchantId")
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(QRPayment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding QR payments by merchantId: {}", merchantId, e);
            return List.of();
        }
    }

    /**
     * Find QR payments by customer ID
     */
    public List<QRPayment> findByCustomerId(String customerId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":customerId", new AttributeValue().withS(customerId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("customerId = :customerId")
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(QRPayment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding QR payments by customerId: {}", customerId, e);
            return List.of();
        }
    }

    /**
     * Find QR payments by status
     */
    public List<QRPayment> findByStatus(PaymentStatus status) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":status", new AttributeValue().withS(status.name()));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("#status = :status")
                    .withExpressionAttributeNames(Map.of("#status", "status"))
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(QRPayment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding QR payments by status: {}", status, e);
            return List.of();
        }
    }

    /**
     * Find QR payment by payment ID
     */
    public Optional<QRPayment> findByPaymentId(String paymentId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":paymentId", new AttributeValue().withS(paymentId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("paymentId = :paymentId")
                    .withExpressionAttributeValues(eav)
                    .withLimit(1);

            List<QRPayment> results = dynamoDBMapper.scan(QRPayment.class, scanExpression);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            log.error("Error finding QR payment by paymentId: {}", paymentId, e);
            return Optional.empty();
        }
    }

    /**
     * Find expired QR codes
     */
    public List<QRPayment> findExpiredQRCodes() {
        Instant now = Instant.now();
        return findAll().stream()
                .filter(qr -> {
                    Instant expiresAt = qr.getExpiresAtInstant();
                    return expiresAt != null && now.isAfter(expiresAt);
                })
                .filter(qr -> !qr.isUsed())
                .collect(Collectors.toList());
    }

    /**
     * Find unused QR codes by merchant ID
     */
    public List<QRPayment> findUnusedByMerchantId(String merchantId) {
        return findByMerchantId(merchantId).stream()
                .filter(qr -> !qr.isUsed())
                .filter(qr -> {
                    Instant expiresAt = qr.getExpiresAtInstant();
                    return expiresAt == null || Instant.now().isBefore(expiresAt);
                })
                .collect(Collectors.toList());
    }

    /**
     * Mark QR code as used
     */
    public Optional<QRPayment> markAsUsed(String qrCode, String paymentId) {
        Optional<QRPayment> qrPaymentOpt = findByQRCode(qrCode);

        if (qrPaymentOpt.isPresent()) {
            QRPayment qrPayment = qrPaymentOpt.get();

            if (qrPayment.isUsed()) {
                return Optional.empty();
            }

            qrPayment.setUsed(true);
            qrPayment.setPaymentId(paymentId);
            qrPayment.setUsedAt(Instant.now());
            qrPayment.setUpdatedAt(Instant.now());

            return Optional.of(save(qrPayment));
        }

        return Optional.empty();
    }

    /**
     * Update QR payment status
     */
    public Optional<QRPayment> updateStatus(String qrPaymentId, PaymentStatus status) {
        Optional<QRPayment> qrPaymentOpt = findById(qrPaymentId);

        if (qrPaymentOpt.isPresent()) {
            QRPayment qrPayment = qrPaymentOpt.get();
            qrPayment.setStatus(status);
            qrPayment.setUpdatedAt(Instant.now());
            return Optional.of(save(qrPayment));
        }

        return Optional.empty();
    }

    /**
     * Find QR payments by merchant ID and date range
     */
    public List<QRPayment> findByMerchantIdAndDateRange(
            String merchantId,
            Instant startDate,
            Instant endDate) {

        return findByMerchantId(merchantId).stream()
                .filter(qr -> {
                    Instant createdAt = qr.getCreatedAtInstant();
                    return createdAt != null &&
                           !createdAt.isBefore(startDate) &&
                           !createdAt.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculate total amount by merchant and date range
     */
    public double calculateTotalAmountByMerchantAndDateRange(
            String merchantId,
            Instant startDate,
            Instant endDate) {

        return findByMerchantIdAndDateRange(merchantId, startDate, endDate).stream()
                .filter(qr -> qr.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(QRPayment::getAmount)
                .sum();
    }

    /**
     * Count QR payments by merchant ID and status
     */
    public long countByMerchantIdAndStatus(String merchantId, PaymentStatus status) {
        return findByMerchantId(merchantId).stream()
                .filter(qr -> qr.getStatus() == status)
                .count();
    }

    /**
     * Find recent QR payments by merchant ID
     */
    public List<QRPayment> findRecentByMerchantId(String merchantId, int limit) {
        List<QRPayment> qrPayments = findByMerchantId(merchantId);
        return qrPayments.stream()
                .sorted((qr1, qr2) -> {
                    Instant c1 = qr1.getCreatedAtInstant();
                    Instant c2 = qr2.getCreatedAtInstant();
                    if (c1 == null && c2 == null) return 0;
                    if (c1 == null) return 1;
                    if (c2 == null) return -1;
                    return c2.compareTo(c1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Delete QR payment by ID
     */
    public boolean deleteById(String id) {
        try {
            QRPayment qrPayment = dynamoDBMapper.load(QRPayment.class, id);
            if (qrPayment != null) {
                dynamoDBMapper.delete(qrPayment);
                log.debug("Deleted QR payment: {}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error deleting QR payment: {}", id, e);
            return false;
        }
    }

    /**
     * Delete expired QR codes
     */
    public int deleteExpiredQRCodes() {
        List<QRPayment> expiredQRCodes = findExpiredQRCodes();
        int count = 0;

        for (QRPayment qrPayment : expiredQRCodes) {
            if (deleteById(qrPayment.getId())) {
                count++;
            }
        }

        return count;
    }

    /**
     * Check if QR code exists
     */
    public boolean existsByQRCode(String qrCode) {
        return findByQRCode(qrCode).isPresent();
    }

    /**
     * Check if QR code is valid
     */
    public boolean isQRCodeValid(String qrCode) {
        return findValidQRCode(qrCode).isPresent();
    }

    /**
     * Find all QR payments
     */
    public List<QRPayment> findAll() {
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            return dynamoDBMapper.scan(QRPayment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding all QR payments", e);
            return List.of();
        }
    }

    /**
     * Batch save QR payments
     */
    public List<QRPayment> saveAll(List<QRPayment> qrPayments) {
        qrPayments.forEach(this::save);
        return qrPayments;
    }

    /**
     * Extend QR code expiration
     */
    public Optional<QRPayment> extendExpiration(String qrPaymentId, int additionalMinutes) {
        Optional<QRPayment> qrPaymentOpt = findById(qrPaymentId);

        if (qrPaymentOpt.isPresent()) {
            QRPayment qrPayment = qrPaymentOpt.get();
            Instant currentExpiry = qrPayment.getExpiresAtInstant();

            if (currentExpiry != null) {
                qrPayment.setExpiresAt(currentExpiry.plusSeconds(additionalMinutes * 60));
            } else {
                qrPayment.setExpiresAt(Instant.now().plusSeconds(additionalMinutes * 60));
            }

            qrPayment.setUpdatedAt(Instant.now());
            return Optional.of(save(qrPayment));
        }

        return Optional.empty();
    }
}