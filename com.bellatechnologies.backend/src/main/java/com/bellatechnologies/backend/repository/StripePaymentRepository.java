package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.StripePayment;
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
public class StripePaymentRepository {

    private final DynamoDBMapper dynamoDBMapper;

    /**
     * Save Stripe payment to DynamoDB
     */
    public StripePayment save(StripePayment payment) {
        try {
            dynamoDBMapper.save(payment);
            log.debug("Saved Stripe payment: {}", payment.getId());
            return payment;
        } catch (Exception e) {
            log.error("Error saving Stripe payment", e);
            throw new RuntimeException("Failed to save Stripe payment: " + e.getMessage(), e);
        }
    }

    /**
     * Find Stripe payment by ID
     */
    public Optional<StripePayment> findById(String id) {
        try {
            StripePayment payment = dynamoDBMapper.load(StripePayment.class, id);
            return Optional.ofNullable(payment);
        } catch (Exception e) {
            log.error("Error finding Stripe payment by ID: {}", id, e);
            return Optional.empty();
        }
    }

    /**
     * Find Stripe payment by payment intent ID
     */
    public Optional<StripePayment> findByPaymentIntentId(String paymentIntentId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":paymentIntentId", new AttributeValue().withS(paymentIntentId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("paymentIntentId = :paymentIntentId")
                    .withExpressionAttributeValues(eav)
                    .withLimit(1);

            List<StripePayment> results = dynamoDBMapper.scan(StripePayment.class, scanExpression);

            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            log.error("Error finding Stripe payment by paymentIntentId: {}", paymentIntentId, e);
            return Optional.empty();
        }
    }

    /**
     * Find Stripe payment by Stripe payment intent ID
     */
    public Optional<StripePayment> findByStripePaymentIntentId(String stripePaymentIntentId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":stripePaymentIntentId", new AttributeValue().withS(stripePaymentIntentId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("stripePaymentIntentId = :stripePaymentIntentId")
                    .withExpressionAttributeValues(eav)
                    .withLimit(1);

            List<StripePayment> results = dynamoDBMapper.scan(StripePayment.class, scanExpression);

            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            log.error("Error finding Stripe payment by stripePaymentIntentId: {}", stripePaymentIntentId, e);
            return Optional.empty();
        }
    }

    /**
     * Find Stripe payments by payment ID
     */
    public List<StripePayment> findByPaymentId(String paymentId) {
        try {
            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":paymentId", new AttributeValue().withS(paymentId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("paymentId = :paymentId")
                    .withExpressionAttributeValues(eav);

            return dynamoDBMapper.scan(StripePayment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding Stripe payments by paymentId: {}", paymentId, e);
            return List.of();
        }
    }

    /**
     * Find all Stripe payments with limit
     */
    public List<StripePayment> findAll(int limit) {
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withLimit(limit);

            return dynamoDBMapper.scan(StripePayment.class, scanExpression);
        } catch (Exception e) {
            log.error("Error finding all Stripe payments", e);
            return List.of();
        }
    }

    /**
     * Delete Stripe payment by ID
     */
    public void delete(String id) {
        try {
            StripePayment payment = dynamoDBMapper.load(StripePayment.class, id);
            if (payment != null) {
                dynamoDBMapper.delete(payment);
                log.debug("Deleted Stripe payment: {}", id);
            }
        } catch (Exception e) {
            log.error("Error deleting Stripe payment: {}", id, e);
            throw new RuntimeException("Failed to delete Stripe payment: " + e.getMessage(), e);
        }
    }
}
