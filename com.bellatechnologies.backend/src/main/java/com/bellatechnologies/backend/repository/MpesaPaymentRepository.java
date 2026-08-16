package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.MpesaPayment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaPaymentRepository extends JpaRepository<MpesaPayment, String> {
    Optional<MpesaPayment> findByCheckoutRequestId(String checkoutRequestId);
    List<MpesaPayment> findByPaymentId(String paymentId);

    default List<MpesaPayment> findAll(int limit) {
        return findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
    }

    default void delete(String id) {
        deleteById(id);
    }
}
