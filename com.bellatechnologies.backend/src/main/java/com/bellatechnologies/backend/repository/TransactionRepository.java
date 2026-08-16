package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.PaymentMethod;
import com.bellatechnologies.backend.model.PaymentStatus;
import com.bellatechnologies.backend.model.PaymentTransaction;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TransactionRepository extends JpaRepository<PaymentTransaction, String> {
    List<PaymentTransaction> findByPaymentId(String paymentId);
    List<PaymentTransaction> findByMethod(PaymentMethod method);
    List<PaymentTransaction> findByStatus(PaymentStatus status);
    Optional<PaymentTransaction> findByTransactionId(String transactionId);

    default List<PaymentTransaction> findByUserId(String userId) {
        return List.of();
    }

    default List<PaymentTransaction> findByPaymentMethod(PaymentMethod method) {
        return findByMethod(method);
    }

    default Optional<PaymentTransaction> findByExternalReference(String reference) {
        return findByTransactionId(reference);
    }

    default List<PaymentTransaction> findByDateRange(String startDate, String endDate) {
        return within(findAll(), Instant.parse(startDate), Instant.parse(endDate));
    }

    default List<PaymentTransaction> findByUserIdAndDateRange(String userId, String start, String end) {
        return within(findByUserId(userId), Instant.parse(start), Instant.parse(end));
    }

    default List<PaymentTransaction> findFailedTransactions() {
        return findByStatus(PaymentStatus.FAILED);
    }

    default List<PaymentTransaction> findPendingTransactions() {
        return findByStatus(PaymentStatus.PENDING);
    }

    @Transactional
    default Optional<PaymentTransaction> updateTransactionStatus(String id, PaymentStatus status, String error) {
        return findById(id).map(transaction -> {
            transaction.setStatus(status);
            transaction.setErrorMessage(error);
            transaction.setUpdatedAt(Instant.now());
            return save(transaction);
        });
    }

    default double calculateTotalAmountByUserAndStatus(String userId, PaymentStatus status) {
        return findByUserId(userId).stream().filter(transaction -> transaction.getStatus() == status)
                .mapToDouble(transaction -> transaction.getAmount() == null ? 0.0 : transaction.getAmount()).sum();
    }

    default double calculateTotalAmountByMethodAndDateRange(PaymentMethod method, String start, String end) {
        return within(findByMethod(method), Instant.parse(start), Instant.parse(end)).stream()
                .mapToDouble(transaction -> transaction.getAmount() == null ? 0.0 : transaction.getAmount()).sum();
    }

    default long countByStatus(PaymentStatus status) {
        return findByStatus(status).size();
    }

    default List<PaymentTransaction> findRecentTransactionsByUserId(String userId, int limit) {
        return findByUserId(userId).stream()
                .sorted(Comparator.comparing(PaymentTransaction::getCreatedAtInstant, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit).toList();
    }

    default boolean existsByExternalReference(String reference) {
        return findByTransactionId(reference).isPresent();
    }

    private static List<PaymentTransaction> within(List<PaymentTransaction> transactions, Instant start, Instant end) {
        return transactions.stream().filter(transaction -> {
            Instant created = transaction.getCreatedAtInstant();
            return created != null && !created.isBefore(start) && !created.isAfter(end);
        }).toList();
    }
}
