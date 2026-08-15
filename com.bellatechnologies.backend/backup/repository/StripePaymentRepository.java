import java.time.Instant;
package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.StripePayment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StripePaymentRepository {
    StripePayment save(StripePayment payment);
    Optional<StripePayment> findById(String id);
    Optional<StripePayment> findByPaymentIntentId(String paymentIntentId);
    Optional<StripePayment> findByStripePaymentIntentId(String paymentIntentId);
    List<StripePayment> findByPaymentId(String paymentId);
    List<StripePayment> findAll(int limit);
    void delete(String id);
}
