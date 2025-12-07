package com.payment.repository;

import com.payment.model.PaymentTransaction;
import com.payment.model.PaymentMethod;
import com.payment.model.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for PaymentTransaction operations with DynamoDB
 * Handles transaction history, audit trails, and reconciliation
 */
@Repository
public class TransactionRepository {

    private final DynamoDbTable<PaymentTransaction> transactionTable;

    @Autowired
    public TransactionRepository(DynamoDbEnhancedClient enhancedClient) {
        this.transactionTable = enhancedClient.table("PaymentTransactions", 
                                                     TableSchema.fromBean(PaymentTransaction.class));
    }

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
        transactionTable.putItem(transaction);
        return transaction;
    }

    /**
     * Find transaction by ID
     * @param transactionId Transaction identifier
     * @return Optional containing transaction if found
     */
    public Optional<PaymentTransaction> findById(String transactionId) {
        Key key = Key.builder()
                .partitionValue(transactionId)
                .build();
        
        PaymentTransaction transaction = transactionTable.getItem(key);
        return Optional.ofNullable(transaction);
    }

    /**
     * Find all transactions for a specific payment
     * @param paymentId Payment identifier
     * @return List of transactions for the payment
     */
    public List<PaymentTransaction> findByPaymentId(String paymentId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(paymentId).build());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        
        return transactionTable.index("PaymentIdIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    /**
     * Find all transactions by user ID
     * @param userId User identifier
     * @return List of transactions for the user
     */
    public List<PaymentTransaction> findByUserId(String userId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(userId).build());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        
        return transactionTable.index("UserIdIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by payment method
     * @param paymentMethod Payment method type
     * @return List of transactions using specified payment method
     */
    public List<PaymentTransaction> findByPaymentMethod(PaymentMethod paymentMethod) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(paymentMethod.name()).build());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        
        return transactionTable.index("PaymentMethodIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by status
     * @param status Transaction status
     * @return List of transactions with specified status
     */
    public List<PaymentTransaction> findByStatus(PaymentStatus status) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(status.name()).build());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        
        return transactionTable.index("StatusIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by external reference (Mpesa/Stripe transaction ID)
     * Critical for webhook processing and reconciliation
     * @param externalReference External transaction reference
     * @return Optional containing transaction if found
     */
    public Optional<PaymentTransaction> findByExternalReference(String externalReference) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(externalReference).build());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        
        return transactionTable.index("ExternalReferenceIndex")
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    /**
     * Find transactions within a date range
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of transactions within date range
     */
    public List<PaymentTransaction> findByDateRange(Instant startDate, Instant endDate) {
        return findAll().stream()
                .filter(transaction -> {
                    Instant createdAt = transaction.getCreatedAt();
                    return createdAt != null && 
                           !createdAt.isBefore(startDate) && 
                           !createdAt.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by user ID within a date range
     * @param userId User identifier
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of transactions within date range
     */
    public List<PaymentTransaction> findByUserIdAndDateRange(String userId, Instant startDate, Instant endDate) {
        return findByUserId(userId).stream()
                .filter(transaction -> {
                    Instant createdAt = transaction.getCreatedAt();
                    return createdAt != null && 
                           !createdAt.isBefore(startDate) && 
                           !createdAt.isAfter(endDate);
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
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return Total amount
     */
    public double calculateTotalAmountByMethodAndDateRange(
            PaymentMethod paymentMethod, 
            Instant startDate, 
            Instant endDate) {
        
        return findByPaymentMethod(paymentMethod).stream()
                .filter(transaction -> {
                    Instant createdAt = transaction.getCreatedAt();
                    return createdAt != null && 
                           !createdAt.isBefore(startDate) && 
                           !createdAt.isAfter(endDate);
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
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Delete transaction by ID
     * @param transactionId Transaction identifier
     * @return true if deleted, false if not found
     */
    public boolean deleteById(String transactionId) {
        Key key = Key.builder()
                .partitionValue(transactionId)
                .build();
        
        PaymentTransaction deletedTransaction = transactionTable.deleteItem(key);
        return deletedTransaction != null;
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
        List<PaymentTransaction> transactions = new ArrayList<>();
        transactionTable.scan().items().forEach(transactions::add);
        return transactions;
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