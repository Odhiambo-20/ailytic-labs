package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.PaymentTransaction;
import com.allyticlabs.backend.model.PaymentMethod;
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
 * Repository for PaymentTransaction operations with DynamoDB
 * Handles transaction history, audit trails, and reconciliation
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionRepository {

    private final DynamoDBMapper dynamoDBMapper;

    /**
     * Save or update a transaction record
     * @param transaction Transaction entity to save
     * @return Saved transaction entity
     */
    public PaymentTransaction save(PaymentTransaction transaction) {
        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(Instant.now());
        }
        transaction.setUpdatedAt(Instant.now());
        dynamoDBMapper.save(transaction);
        return transaction;
    }

    /**
     * Find transaction by ID
     * @param transactionId Transaction identifier
     * @return Optional containing transaction if found
     */
    public Optional<PaymentTransaction> findById(String transactionId) {
        try {
            PaymentTransaction transaction = dynamoDBMapper.load(PaymentTransaction.class, transactionId);
            return Optional.ofNullable(transaction);
        } catch (Exception e) {
            log.error("Error finding transaction by ID: {}", transactionId, e);
            return Optional.empty();
        }
    }

    /**
     * Find all transactions for a specific payment
     * @param paymentId Payment identifier
     * @return List of transactions for the payment
     */
    public List<PaymentTransaction> findByPaymentId(String paymentId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":paymentId", new AttributeValue().withS(paymentId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("paymentId = :paymentId")
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(PaymentTransaction.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding transactions by paymentId: {}", paymentId, e);
            return List.of();
        }
    }

    /**
     * Find all transactions by user ID
     * @param userId User identifier
     * @return List of transactions for the user
     */
    public List<PaymentTransaction> findByUserId(String userId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":userId", new AttributeValue().withS(userId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("userId = :userId")
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(PaymentTransaction.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding transactions by userId: {}", userId, e);
            return List.of();
        }
    }

    /**
     * Find transactions by payment method
     * @param paymentMethod Payment method type
     * @return List of transactions using specified payment method
     */
    public List<PaymentTransaction> findByPaymentMethod(PaymentMethod paymentMethod) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":method", new AttributeValue().withS(paymentMethod.name()));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("method = :method")
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(PaymentTransaction.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding transactions by paymentMethod: {}", paymentMethod, e);
            return List.of();
        }
    }

    /**
     * Find transactions by status
     * @param status Transaction status
     * @return List of transactions with specified status
     */
    public List<PaymentTransaction> findByStatus(PaymentStatus status) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":status", new AttributeValue().withS(status.name()));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("#status = :status")
                    .withExpressionAttributeNames(Map.of("#status", "status"))
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(PaymentTransaction.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding transactions by status: {}", status, e);
            return List.of();
        }
    }

    /**
     * Find transactions by external reference (Mpesa/Stripe transaction ID)
     * Critical for webhook processing and reconciliation
     * @param externalReference External transaction reference
     * @return Optional containing transaction if found
     */
    public Optional<PaymentTransaction> findByExternalReference(String externalReference) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":transactionId", new AttributeValue().withS(externalReference));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("transactionId = :transactionId")
                    .withExpressionAttributeValues(eav)
                    .withLimit(1);

            List<PaymentTransaction> results = dynamoDBMapper.scan(PaymentTransaction.class, scanExpression);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            log.error("Error finding transaction by externalReference: {}", externalReference, e);
            return Optional.empty();
        }
    }

    /**
     * Find transactions within a date range
     * @param startDate Start of date range (ISO-8601 format)
     * @param endDate End of date range (ISO-8601 format)
     * @return List of transactions within date range
     */
    public List<PaymentTransaction> findByDateRange(String startDate, String endDate) {
        Instant start = Instant.parse(startDate);
        Instant end = Instant.parse(endDate);

        return findAll().stream()
                .filter(transaction -> {
                    Instant createdAt = transaction.getCreatedAtInstant();
                    return createdAt != null &&
                           !createdAt.isBefore(start) &&
                           !createdAt.isAfter(end);
                })
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by user ID within a date range
     * @param userId User identifier
     * @param startDate Start of date range (ISO-8601 format)
     * @param endDate End of date range (ISO-8601 format)
     * @return List of transactions within date range
     */
    public List<PaymentTransaction> findByUserIdAndDateRange(String userId, String startDate, String endDate) {
        Instant start = Instant.parse(startDate);
        Instant end = Instant.parse(endDate);

        return findByUserId(userId).stream()
                .filter(transaction -> {
                    Instant createdAt = transaction.getCreatedAtInstant();
                    return createdAt != null &&
                           !createdAt.isBefore(start) &&
                           !createdAt.isAfter(end);
                })
                .collect(Collectors.toList());
    }

    /**
     * Find failed transactions for retry processing
     * @return List of failed transactions
     */
    public List<PaymentTransaction> findFailedTransactions() {
        return findByStatus(PaymentStatus.FAILED);
    }

    /**
     * Find pending transactions that need status verification
     * @return List of pending transactions
     */
    public List<PaymentTransaction> findPendingTransactions() {
        return findByStatus(PaymentStatus.PENDING);
    }

    /**
     * Update transaction status with error details
     * @param transactionId Transaction identifier
     * @param status New status
     * @param errorMessage Error message if any
     * @return Updated transaction
     */
    public Optional<PaymentTransaction> updateTransactionStatus(
            String transactionId,
            PaymentStatus status,
            String errorMessage) {

        Optional<PaymentTransaction> transactionOpt = findById(transactionId);
        if (transactionOpt.isPresent()) {
            PaymentTransaction transaction = transactionOpt.get();
            transaction.setStatus(status);
            transaction.setErrorMessage(errorMessage);
            transaction.setUpdatedAt(Instant.now());
            return Optional.of(save(transaction));
        }
        return Optional.empty();
    }

    /**
     * Calculate total transaction amount by user and status
     * @param userId User identifier
     * @param status Transaction status
     * @return Total amount
     */
    public double calculateTotalAmountByUserAndStatus(String userId, PaymentStatus status) {
        return findByUserId(userId).stream()
                .filter(transaction -> transaction.getStatus() == status)
                .mapToDouble(PaymentTransaction::getAmount)
                .sum();
    }

    /**
     * Calculate total transaction amount by payment method
     * @param paymentMethod Payment method
     * @param startDate Start of date range (ISO-8601 format)
     * @param endDate End of date range (ISO-8601 format)
     * @return Total amount
     */
    public double calculateTotalAmountByMethodAndDateRange(
            PaymentMethod paymentMethod,
            String startDate,
            String endDate) {

        Instant start = Instant.parse(startDate);
        Instant end = Instant.parse(endDate);

        return findByPaymentMethod(paymentMethod).stream()
                .filter(transaction -> {
                    Instant createdAt = transaction.getCreatedAtInstant();
                    return createdAt != null &&
                           !createdAt.isBefore(start) &&
                           !createdAt.isAfter(end);
                })
                .filter(transaction -> transaction.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(PaymentTransaction::getAmount)
                .sum();
    }

    /**
     * Count transactions by status
     * @param status Transaction status
     * @return Count of transactions
     */
    public long countByStatus(PaymentStatus status) {
        return findByStatus(status).size();
    }

    /**
     * Find recent transactions for a user
     * @param userId User identifier
     * @param limit Number of recent transactions
     * @return List of recent transactions
     */
    public List<PaymentTransaction> findRecentTransactionsByUserId(String userId, int limit) {
        List<PaymentTransaction> transactions = findByUserId(userId);
        return transactions.stream()
                .sorted((t1, t2) -> {
                    Instant c1 = t1.getCreatedAtInstant();
                    Instant c2 = t2.getCreatedAtInstant();
                    if (c1 == null && c2 == null) return 0;
                    if (c1 == null) return 1;
                    if (c2 == null) return -1;
                    return c2.compareTo(c1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Delete transaction by ID
     * @param transactionId Transaction identifier
     * @return true if deleted, false if not found
     */
    public boolean deleteById(String transactionId) {
        try {
            PaymentTransaction transaction = dynamoDBMapper.load(PaymentTransaction.class, transactionId);
            if (transaction != null) {
                dynamoDBMapper.delete(transaction);
                log.debug("Deleted transaction: {}", transactionId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error deleting transaction: {}", transactionId, e);
            return false;
        }
    }

    /**
     * Check if transaction exists
     * @param transactionId Transaction identifier
     * @return true if exists, false otherwise
     */
    public boolean existsById(String transactionId) {
        return findById(transactionId).isPresent();
    }

    /**
     * Check if external reference exists (prevent duplicate processing)
     * @param externalReference External reference
     * @return true if exists, false otherwise
     */
    public boolean existsByExternalReference(String externalReference) {
        return findByExternalReference(externalReference).isPresent();
    }

    /**
     * Find all transactions (use with caution on large datasets)
     * @return List of all transactions
     */
    public List<PaymentTransaction> findAll() {
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            return dynamoDBMapper.scan(PaymentTransaction.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding all transactions", e);
            return List.of();
        }
    }

    /**
     * Batch save transactions
     * @param transactions List of transactions to save
     * @return List of saved transactions
     */
    public List<PaymentTransaction> saveAll(List<PaymentTransaction> transactions) {
        transactions.forEach(this::save);
        return transactions;
    }
}
