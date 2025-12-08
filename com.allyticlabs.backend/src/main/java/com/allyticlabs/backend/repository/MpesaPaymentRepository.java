package com.allyticlabs.backend.repository;

import com.allyticlabs.backend.model.MpesaPayment;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MpesaPaymentRepository {

    private final DynamoDBMapper dynamoDBMapper;

    /**
     * Save M-Pesa payment to DynamoDB
     */
    public MpesaPayment save(MpesaPayment payment) {
        try {
            dynamoDBMapper.save(payment);
            log.debug("Saved M-Pesa payment: {}", payment.getId());
            return payment;
        } catch (Exception e) {
            log.error("Error saving M-Pesa payment", e);
            throw new RuntimeException("Failed to save M-Pesa payment: " + e.getMessage(), e);
        }
    }

    /**
     * Find M-Pesa payment by ID
     */
    public Optional<MpesaPayment> findById(String id) {
        try {
            MpesaPayment payment = dynamoDBMapper.load(MpesaPayment.class, id);
            return Optional.ofNullable(payment);
        } catch (Exception e) {
            log.error("Error finding M-Pesa payment by ID: {}", id, e);
            return Optional.empty();
        }
    }

    /**
     * Find M-Pesa payment by checkout request ID
     */
    public Optional<MpesaPayment> findByCheckoutRequestId(String checkoutRequestId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":checkoutRequestId", new AttributeValue().withS(checkoutRequestId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("checkoutRequestId = :checkoutRequestId")
                    .withExpressionAttributeValues(eav)
                    .withLimit(1);

            List<MpesaPayment> results = dynamoDBMapper.scan(MpesaPayment.class, scanExpression);
            
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            log.error("Error finding M-Pesa payment by checkoutRequestId: {}", checkoutRequestId, e);
            return Optional.empty();
        }
    }

    /**
     * Find M-Pesa payments by payment ID
     */
    public List<MpesaPayment> findByPaymentId(String paymentId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":paymentId", new AttributeValue().withS(paymentId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("paymentId = :paymentId")
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(MpesaPayment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding M-Pesa payments by paymentId: {}", paymentId, e);
            return List.of();
        }
    }

    /**
     * Find all M-Pesa payments with limit
     */
    public List<MpesaPayment> findAll(int limit) {
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withLimit(limit);

            return dynamoDBMapper.scan(MpesaPayment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding all M-Pesa payments", e);
            return List.of();
        }
    }

    /**
     * Delete M-Pesa payment by ID
     */
    public void delete(String id) {
        try {
            MpesaPayment payment = dynamoDBMapper.load(MpesaPayment.class, id);
            if (payment != null) {
                dynamoDBMapper.delete(payment);
                log.debug("Deleted M-Pesa payment: {}", id);
            }
        } catch (Exception e) {
            log.error("Error deleting M-Pesa payment: {}", id, e);
            throw new RuntimeException("Failed to delete M-Pesa payment: " + e.getMessage(), e);
        }
    }
}