package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.PaymentStatus;
import com.bellatechnologies.backend.model.QRPayment;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface QRPaymentRepository extends JpaRepository<QRPayment, String> {
    Optional<QRPayment> findByQrCode(String qrCode);
    Optional<QRPayment> findByQrCodeToken(String qrCodeToken);
    List<QRPayment> findByMerchantId(String merchantId);
    List<QRPayment> findByStatus(PaymentStatus status);
    Optional<QRPayment> findByPaymentId(String paymentId);
    boolean existsByQrCode(String qrCode);

    default Optional<QRPayment> findByQRCode(String value) {
        return findByQrCodeToken(value).or(() -> findByQrCode(value));
    }

    default Optional<QRPayment> findValidQRCode(String value) {
        return findByQRCode(value).filter(code -> !code.isExpired() && !code.isUsed());
    }

    default List<QRPayment> findByCustomerId(String customerId) {
        return List.of();
    }

    default List<QRPayment> findExpiredQRCodes() {
        return findAll().stream().filter(QRPayment::isExpired).toList();
    }

    default List<QRPayment> findUnusedByMerchantId(String merchantId) {
        return findByMerchantId(merchantId).stream().filter(code -> !code.isUsed()).toList();
    }

    @Transactional
    default Optional<QRPayment> markAsUsed(String qrCode, String paymentId) {
        return findByQRCode(qrCode).map(code -> {
            code.setUsed(true);
            code.setPaymentId(paymentId);
            code.setUsedAt(Instant.now());
            return save(code);
        });
    }

    @Transactional
    default Optional<QRPayment> updateStatus(String id, PaymentStatus status) {
        return findById(id).map(code -> {
            code.setStatus(status);
            code.setUpdatedAt(Instant.now());
            return save(code);
        });
    }

    default List<QRPayment> findByMerchantIdAndDateRange(String merchantId, String startDate, String endDate) {
        Instant start = Instant.parse(startDate);
        Instant end = Instant.parse(endDate);
        return findByMerchantId(merchantId).stream().filter(code -> {
            Instant created = code.getCreatedAtInstant();
            return created != null && !created.isBefore(start) && !created.isAfter(end);
        }).toList();
    }

    default double calculateTotalAmountByMerchantAndDateRange(String merchantId, String start, String end) {
        return findByMerchantIdAndDateRange(merchantId, start, end).stream()
                .mapToDouble(code -> code.getAmount() == null ? 0.0 : code.getAmount()).sum();
    }

    default long countByMerchantIdAndStatus(String merchantId, PaymentStatus status) {
        return findByMerchantId(merchantId).stream().filter(code -> code.getStatus() == status).count();
    }

    default List<QRPayment> findRecentByMerchantId(String merchantId, int limit) {
        return findByMerchantId(merchantId).stream()
                .sorted(Comparator.comparing(QRPayment::getCreatedAtInstant, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit).toList();
    }

    @Transactional
    default int deleteExpiredQRCodes() {
        List<QRPayment> expired = findExpiredQRCodes();
        deleteAll(expired);
        return expired.size();
    }

    default boolean existsByQRCode(String value) {
        return findByQRCode(value).isPresent();
    }

    default boolean isQRCodeValid(String value) {
        return findValidQRCode(value).isPresent();
    }

    @Transactional
    default Optional<QRPayment> extendExpiration(String id, int minutes) {
        return findById(id).map(code -> {
            Instant base = code.getExpiresAtInstant() == null ? Instant.now() : code.getExpiresAtInstant();
            code.setExpiresAt(base.plus(minutes, ChronoUnit.MINUTES));
            return save(code);
        });
    }
}
