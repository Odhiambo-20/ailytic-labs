package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.StripePayment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StripePaymentRepository extends JpaRepository<StripePayment, String> {
    Optional<StripePayment> findByPaymentIntentId(String paymentIntentId);
    Optional<StripePayment> findByStripePaymentIntentId(String stripePaymentIntentId);
    List<StripePayment> findByPaymentId(String paymentId);

    default List<StripePayment> findAll(int limit) {
        return findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
    }

    default void delete(String id) {
        deleteById(id);
    }
}
