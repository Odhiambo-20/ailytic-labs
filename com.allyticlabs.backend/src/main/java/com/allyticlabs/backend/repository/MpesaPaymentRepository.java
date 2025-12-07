package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.MpesaPayment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MpesaPaymentRepository {
    MpesaPayment save(MpesaPayment payment);
    Optional<MpesaPayment> findById(String id);
    Optional<MpesaPayment> findByCheckoutRequestId(String checkoutRequestId);
    List<MpesaPayment> findByPaymentId(String paymentId);
    List<MpesaPayment> findAll(int limit);
    void delete(String id);
}
