import java.time.Instant;
package com.bellatechnologies.backend.repository;
import java.util.Map;
import java.util.HashMap;

import com.bellatechnologies.backend.model.QRPayment;
import com.bellatechnologies.backend.model.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for QR Payment operations with DynamoDB
 * Handles QR code generation, validation, and payment tracking
 * Implements WeChat-style QR payment system
 */
@Repository
public class QRPaymentRepository {

    private final DynamoDbTable<QRPayment> qrPaymentTable;
    private static final int DEFAULT_QR_EXPIRY_MINUTES = 15; // QR codes expire after 15 minutes

    @Autowired
    public QRPaymentRepository(DynamoDbEnhancedClient enhancedClient) {
        this.qrPaymentTable = enhancedClient.table("QRPayments", 
                                                    TableSchema.fromBean(QRPayment.class));
    }

    /**
     * Save or update a QR payment record
     * @param qrPayment QR payment entity to save
     * @return Saved QR payment entity
     */
    public QRPayment save(QRPayment qrPayment) {
        if (qrPayment.getCreatedAt() == null) {
            qrPayment.setCreatedAt(Instant.now());
        }
        if (qrPayment.getExpiresAt() == null) {
            qrPayment.setExpiresAt(Instant.now().plusSeconds(DEFAULT_QR_EXPIRY_MINUTES * 60));
        }
        qrPayment.setUpdatedAt(Instant.now());
        qrPaymentTable.putItem(qrPayment);
        return qrPayment;
    }

    /**
     * Find QR payment by ID
     * @param qrPaymentId QR payment identifier
     * @return Optional containing QR payment if found
     */
    public Optional<QRPayment> findById(String qrPaymentId) {
        Key key = Key.builder()
                .partitionValue(qrPaymentId)
                .build();
        
        QRPayment qrPayment = qrPaymentTable.getItem(key);
        return Optional.ofNullable(qrPayment);
    }

    /**
     * Find QR payment by QR code (most critical method for payment processing)
     * This is used when a user scans a QR code to initiate payment
     * @param qrCode The QR code string
     * @return Optional containing QR payment if found
     */
    public Optional<QRPayment> findByQRCode(String qrCode) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(qrCode).build());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        
        return qrPaymentTable.index("QRCodeIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    /**
     * Find QR payment by QR code with validation
     * Checks expiration and usage status
     * @param qrCode The QR code string
     * @return Optional containing valid QR payment if found
     */
    public Optional<QRPayment> findValidQRCode(String qrCode) {
        Optional<QRPayment> qrPaymentOpt = findByQRCode(qrCode);
        
        if (qrPaymentOpt.isPresent()) {
            QRPayment qrPayment = qrPaymentOpt.get();
            
            // Check if QR code has expired
            if (qrPayment.getExpiresAt() != null && 
                Instant.now().isAfter(qrPayment.getExpiresAt())) {
                return Optional.empty();
            }
            
            // Check if QR code has already been used
            if (qrPayment.isUsed()) {
                return Optional.empty();
            }
            
            return qrPaymentOpt;
        }
        
        return Optional.empty();
    }

    /**
     * Find all QR payments for a merchant
     * @param merchantId Merchant identifier
     * @return List of QR payments for the merchant
     */
    /**
     * Find QR payment by token
     */
    public Optional<QRPayment> findByQrCodeToken(String token) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("qrCodeToken", AttributeValue.builder().s(token).build());
        
        GetItemRequest request = GetItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(key)
            .build();
        
        GetItemResponse response = dynamoDbClient.getItem(request);
        
        if (response.hasItem()) {
            return Optional.of(mapToQRPayment(response.item()));
        }
        return Optional.empty();
    }

    public List<QRPayment> findByMerchantId(String merchantId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(merchantId).build());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        
        return qrPaymentTable.index("MerchantIdIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    /**
     * Find all QR payments by customer ID
     * @param customerId Customer identifier
     * @return List of QR payments for the customer
     */
    public List<QRPayment> findByCustomerId(String customerId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(customerId).build());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        
        return qrPaymentTable.index("CustomerIdIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    /**
     * Find QR payments by status
     * @param status Payment status
     * @return List of QR payments with specified status
     */
    public List<QRPayment> findByStatus(PaymentStatus status) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(status.name()).build());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        
        return qrPaymentTable.index("StatusIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    /**
     * Find QR payment by associated payment ID
     * @param paymentId Payment identifier
     * @return Optional containing QR payment if found
     */
    public Optional<QRPayment> findByPaymentId(String paymentId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(paymentId).build());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        
        return qrPaymentTable.index("PaymentIdIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    /**
     * Find expired QR codes that need cleanup
     * @return List of expired QR payments
     */
    public List<QRPayment> findExpiredQRCodes() {
        Instant now = Instant.now();
        return findAll().stream()
                .filter(qr -> qr.getExpiresAt() != null && now.isAfter(qr.getExpiresAt()))
                .filter(qr -> !qr.isUsed())
                .collect(Collectors.toList());
    }

    /**
     * Find unused QR codes for a merchant
     * @param merchantId Merchant identifier
     * @return List of unused QR payments
     */
    public List<QRPayment> findUnusedByMerchantId(String merchantId) {
        return findByMerchantId(merchantId).stream()
                .filter(qr -> !qr.isUsed())
                .filter(qr -> qr.getExpiresAt() == null || 
                             Instant.now().isBefore(qr.getExpiresAt()))
                .collect(Collectors.toList());
    }

    /**
     * Mark QR code as used (critical for preventing double-spending)
     * @param qrCode The QR code string
     * @param paymentId Associated payment ID
     * @return Updated QR payment
     */
    public Optional<QRPayment> markAsUsed(String qrCode, String paymentId) {
        Optional<QRPayment> qrPaymentOpt = findByQRCode(qrCode);
        
        if (qrPaymentOpt.isPresent()) {
            QRPayment qrPayment = qrPaymentOpt.get();
            
            // Double-check it hasn't been used already
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
     * @param qrPaymentId QR payment identifier
     * @param status New payment status
     * @return Updated QR payment
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
     * Find QR payments within a date range for a merchant
     * @param merchantId Merchant identifier
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of QR payments within date range
     */
    public List<QRPayment> findByMerchantIdAndDateRange(
            String merchantId, 
            Instant startDate, 
            Instant endDate) {
        
        return findByMerchantId(merchantId).stream()
                .filter(qr -> {
                    Instant createdAt = qr.getCreatedAt();
                    return createdAt != null && 
                           !createdAt.isBefore(startDate) && 
                           !createdAt.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculate total amount of successful QR payments for merchant
     * @param merchantId Merchant identifier
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return Total amount
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
     * Count QR codes by merchant and status
     * @param merchantId Merchant identifier
     * @param status Payment status
     * @return Count of QR payments
     */
    public long countByMerchantIdAndStatus(String merchantId, PaymentStatus status) {
        return findByMerchantId(merchantId).stream()
                .filter(qr -> qr.getStatus() == status)
                .count();
    }

    /**
     * Find recent QR payments for a merchant
     * @param merchantId Merchant identifier
     * @param limit Number of recent payments
     * @return List of recent QR payments
     */
    public List<QRPayment> findRecentByMerchantId(String merchantId, int limit) {
        List<QRPayment> qrPayments = findByMerchantId(merchantId);
        return qrPayments.stream()
                .sorted((qr1, qr2) -> qr2.getCreatedAt().compareTo(qr1.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Delete QR payment by ID
     * @param qrPaymentId QR payment identifier
     * @return true if deleted, false if not found
     */
    public boolean deleteById(String qrPaymentId) {
        Key key = Key.builder()
                .partitionValue(qrPaymentId)
                .build();
        
        QRPayment deletedQRPayment = qrPaymentTable.deleteItem(key);
        return deletedQRPayment != null;
    }

    /**
     * Delete expired and unused QR codes (cleanup operation)
     * @return Number of deleted QR codes
     */
    public int deleteExpiredQRCodes() {
        List<QRPayment> expiredQRCodes = findExpiredQRCodes();
        int count = 0;
        
        for (QRPayment qrPayment : expiredQRCodes) {
            if (deleteById(qrPayment.getQrPaymentId())) {
                count++;
            }
        }
        
        return count;
    }

    /**
     * Check if QR code exists
     * @param qrCode The QR code string
     * @return true if exists, false otherwise
     */
    public boolean existsByQRCode(String qrCode) {
        return findByQRCode(qrCode).isPresent();
    }

    /**
     * Check if QR code is valid for payment
     * @param qrCode The QR code string
     * @return true if valid (exists, not expired, not used), false otherwise
     */
    public boolean isQRCodeValid(String qrCode) {
        return findValidQRCode(qrCode).isPresent();
    }

    /**
     * Find all QR payments (use with caution on large datasets)
     * @return List of all QR payments
     */
    public List<QRPayment> findAll() {
        List<QRPayment> qrPayments = new ArrayList<>();
        qrPaymentTable.scan().items().forEach(qrPayments::add);
        return qrPayments;
    }

    /**
     * Batch save QR payments
     * @param qrPayments List of QR payments to save
     * @return List of saved QR payments
     */
    public List<QRPayment> saveAll(List<QRPayment> qrPayments) {
        qrPayments.forEach(this::save);
        return qrPayments;
    }

    /**
     * Extend QR code expiration time
     * Useful for merchant static QR codes
     * @param qrPaymentId QR payment identifier
     * @param additionalMinutes Additional minutes to extend
     * @return Updated QR payment
     */
    public Optional<QRPayment> extendExpiration(String qrPaymentId, int additionalMinutes) {
        Optional<QRPayment> qrPaymentOpt = findById(qrPaymentId);
        
        if (qrPaymentOpt.isPresent()) {
            QRPayment qrPayment = qrPaymentOpt.get();
            Instant currentExpiry = qrPayment.getExpiresAt();
            
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