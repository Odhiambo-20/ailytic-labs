package com.payment.repository;

import com.payment.model.Payment;
import com.payment.model.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Payment operations with DynamoDB
 * Handles CRUD operations and complex queries for payment records
 */
@Repository
public class PaymentRepository {

    private final DynamoDbTable<Payment> paymentTable;

    @Autowired
    public PaymentRepository(DynamoDbEnhancedClient enhancedClient) {
        this.paymentTable = enhancedClient.table("Payments", TableSchema.fromBean(Payment.class));
    }

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
        paymentTable.putItem(payment);
        return payment;
    }

    /**
     * Find payment by ID
     * @param paymentId Payment identifier
     * @return Optional containing payment if found
     */
    public Optional<Payment> findById(String paymentId) {
        Key key = Key.builder()
                .partitionValue(paymentId)
                .build();

        Payment payment = paymentTable.getItem(key);
        return Optional.ofNullable(payment);
    }

    /**
     * Find all payments by user ID
     * Uses GSI (Global Secondary Index) for efficient querying
     * @param userId User identifier
     * @return List of payments for the user
     */
    public List<Payment> findByUserId(String userId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(userId).build());

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        return paymentTable.index("UserIdIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    /**
     * Find payments by status
     * @param status Payment status
     * @return List of payments with specified status
     */
    public List<Payment> findByStatus(PaymentStatus status) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(status.name()).build());

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        return paymentTable.index("StatusIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
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
     * Find payment by external transaction ID (Mpesa/Stripe)
     * @param externalTransactionId External transaction identifier
     * @return Optional containing payment if found
     */
    public Optional<Payment> findByExternalTransactionId(String externalTransactionId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(externalTransactionId).build());

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        return paymentTable.index("ExternalTransactionIdIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
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
        Key key = Key.builder()
                .partitionValue(paymentId)
                .build();

        Payment deletedPayment = paymentTable.deleteItem(key);
        return deletedPayment != null;
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
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
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
        List<Payment> payments = new ArrayList<>();
        paymentTable.scan().items().forEach(payments::add);
        return payments;
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
