package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.Payment;
import com.allyticlabs.backend.model.PaymentStatus;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Payment operations with DynamoDB
 * Handles CRUD operations and complex queries for payment records
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class PaymentRepository {

    private final DynamoDBMapper dynamoDBMapper;

    /**
     * Save or update a payment record
     * @param payment Payment entity to save
     * @return Saved payment entity
     */
    public Payment save(Payment payment) {
        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(Instant.now());
        }
        payment.setUpdatedAt(Instant.now());
        dynamoDBMapper.save(payment);
        return payment;
    }

    /**
     * Find payment by ID
     * @param paymentId Payment identifier
     * @return Optional containing payment if found
     */
    public Optional<Payment> findById(String paymentId) {
        try {
            Payment payment = dynamoDBMapper.load(Payment.class, paymentId);
            return Optional.ofNullable(payment);
        } catch (Exception e) {
            log.error("Error finding payment by ID: {}", paymentId, e);
            return Optional.empty();
        }
    }

    /**
     * Find all payments by user ID
     * @param userId User identifier
     * @return List of payments for the user
     */
    public List<Payment> findByUserId(String userId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":userId", new AttributeValue().withS(userId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("userId = :userId")
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(Payment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding payments by userId: {}", userId, e);
            return List.of();
        }
    }

    /**
     * Find all payments by merchant ID
     * @param merchantId Merchant identifier
     * @return List of payments for the merchant
     */
    public List<Payment> findByMerchantId(String merchantId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":merchantId", new AttributeValue().withS(merchantId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("merchantId = :merchantId")
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(Payment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding payments by merchantId: {}", merchantId, e);
            return List.of();
        }
    }

    /**
     * Find payment by idempotency key
     * @param idempotencyKey Idempotency key
     * @return Optional containing payment if found
     */
    public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":idempotencyKey", new AttributeValue().withS(idempotencyKey));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("idempotencyKey = :idempotencyKey")
                    .withExpressionAttributeValues(eav)
                    .withLimit(1);

            List<Payment> results = dynamoDBMapper.scan(Payment.class, scanExpression);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            log.error("Error finding payment by idempotencyKey: {}", idempotencyKey, e);
            return Optional.empty();
        }
    }

    /**
     * Find payments by status
     * @param status Payment status
     * @return List of payments with specified status
     */
    public List<Payment> findByStatus(PaymentStatus status) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":status", new AttributeValue().withS(status.name()));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("#status = :status")
                    .withExpressionAttributeNames(Map.of("#status", "status"))
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(Payment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding payments by status: {}", status, e);
            return List.of();
        }
    }

    /**
     * Find payments by user ID and status
     * @param userId User identifier
     * @param status Payment status
     * @return List of payments matching criteria
     */
    public List<Payment> findByUserIdAndStatus(String userId, PaymentStatus status) {
        return findByUserId(userId).stream()
                .filter(payment -> payment.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Find payments within a date range for a user
     * @param userId User identifier
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of payments within date range
     */
    public List<Payment> findByUserIdAndDateRange(String userId, Instant startDate, Instant endDate) {
        return findByUserId(userId).stream()
                .filter(payment -> {
                    Instant createdAt = payment.getCreatedAt();
                    return createdAt != null &&
                           !createdAt.isBefore(startDate) &&
                           !createdAt.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Find payments within a date range for a merchant
     * @param merchantId Merchant identifier
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of payments within date range
     */
    public List<Payment> findByMerchantIdAndDateRange(String merchantId, Instant startDate, Instant endDate) {
        return findByMerchantId(merchantId).stream()
                .filter(payment -> {
                    Instant createdAt = payment.getCreatedAt();
                    return createdAt != null &&
                           !createdAt.isBefore(startDate) &&
                           !createdAt.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Find payment by external transaction ID (Mpesa/Stripe)
     * @param externalTransactionId External transaction identifier
     * @return Optional containing payment if found
     */
    public Optional<Payment> findByExternalTransactionId(String externalTransactionId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":externalTransactionId", new AttributeValue().withS(externalTransactionId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("externalTransactionId = :externalTransactionId")
                    .withExpressionAttributeValues(eav)
                    .withLimit(1);

            List<Payment> results = dynamoDBMapper.scan(Payment.class, scanExpression);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            log.error("Error finding payment by externalTransactionId: {}", externalTransactionId, e);
            return Optional.empty();
        }
    }

    /**
     * Update payment status
     * @param paymentId Payment identifier
     * @param status New payment status
     * @return Updated payment entity
     */
    public Optional<Payment> updateStatus(String paymentId, PaymentStatus status) {
        Optional<Payment> paymentOpt = findById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(status);
            payment.setUpdatedAt(Instant.now());
            return Optional.of(save(payment));
        }
        return Optional.empty();
    }

    /**
     * Delete payment by ID
     * @param paymentId Payment identifier
     * @return true if deleted, false if not found
     */
    public boolean deleteById(String paymentId) {
        try {
            Payment payment = dynamoDBMapper.load(Payment.class, paymentId);
            if (payment != null) {
                dynamoDBMapper.delete(payment);
                log.debug("Deleted payment: {}", paymentId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error deleting payment: {}", paymentId, e);
            return false;
        }
    }

    /**
     * Check if payment exists
     * @param paymentId Payment identifier
     * @return true if exists, false otherwise
     */
    public boolean existsById(String paymentId) {
        return findById(paymentId).isPresent();
    }

    /**
     * Find recent payments for a user (last N payments)
     * @param userId User identifier
     * @param limit Number of recent payments to retrieve
     * @return List of recent payments
     */
    public List<Payment> findRecentPaymentsByUserId(String userId, int limit) {
        List<Payment> allPayments = findByUserId(userId);
        return allPayments.stream()
                .sorted((p1, p2) -> {
                    Instant c1 = p1.getCreatedAt();
                    Instant c2 = p2.getCreatedAt();
                    if (c1 == null && c2 == null) return 0;
                    if (c1 == null) return 1;
                    if (c2 == null) return -1;
                    return c2.compareTo(c1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Calculate total amount for user payments with specific status
     * @param userId User identifier
     * @param status Payment status
     * @return Total amount
     */
    public double calculateTotalAmountByUserAndStatus(String userId, PaymentStatus status) {
        return findByUserIdAndStatus(userId, status).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    /**
     * Find all payments (with pagination support)
     * Note: Use with caution on large datasets
     * @return List of all payments
     */
    public List<Payment> findAll() {
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            return dynamoDBMapper.scan(Payment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding all payments", e);
            return List.of();
        }
    }

    /**
     * Batch save payments
     * @param payments List of payments to save
     * @return List of saved payments
     */
    public List<Payment> saveAll(List<Payment> payments) {
        payments.forEach(this::save);
        return payments;
    }
}
