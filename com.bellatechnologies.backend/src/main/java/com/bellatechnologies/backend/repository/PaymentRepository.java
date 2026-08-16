package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.Payment;
import com.bellatechnologies.backend.model.PaymentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByUserId(String userId);
    List<Payment> findByMerchantId(String merchantId);
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByUserIdAndStatus(String userId, PaymentStatus status);
    Optional<Payment> findByExternalTransactionId(String externalTransactionId);

    default List<Payment> findByUserIdAndDateRange(String userId, Instant startDate, Instant endDate) {
        return within(findByUserId(userId), startDate, endDate);
    }

    default List<Payment> findByMerchantIdAndDateRange(String merchantId, Instant startDate, Instant endDate) {
        return within(findByMerchantId(merchantId), startDate, endDate);
    }

    default List<Payment> findRecentPaymentsByUserId(String userId, int limit) {
        return findByUserId(userId).stream()
                .sorted(Comparator.comparing(Payment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .toList();
    }

    @Transactional
    default Optional<Payment> updateStatus(String paymentId, PaymentStatus status) {
        return findById(paymentId).map(payment -> {
            payment.setStatus(status);
            payment.setUpdatedAt(Instant.now());
            return save(payment);
        });
    }

    default double calculateTotalAmountByUserAndStatus(String userId, PaymentStatus status) {
        return findByUserIdAndStatus(userId, status).stream()
                .mapToDouble(payment -> payment.getAmount() == null ? 0.0 : payment.getAmount())
                .sum();
    }

    private static List<Payment> within(List<Payment> payments, Instant start, Instant end) {
        return payments.stream().filter(payment -> {
            Instant createdAt = payment.getCreatedAt();
            return createdAt != null && !createdAt.isBefore(start) && !createdAt.isAfter(end);
        }).toList();
    }
}
