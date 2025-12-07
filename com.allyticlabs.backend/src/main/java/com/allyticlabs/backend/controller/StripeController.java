// ============================================================================
// File: controller/StripeController.java
// ============================================================================
package com.allyticlabs.backend.controller;

import com.allyticlabs.backend.dto.PaymentRequest;
import com.allyticlabs.backend.dto.PaymentResponse;
import com.allyticlabs.backend.service.StripeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/stripe")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class StripeController {

    private final StripeService stripeService;

    /**
     * Create Stripe Payment Intent
     * POST /api/v1/payments/stripe/create-intent
     */
    @PostMapping("/create-intent")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> createPaymentIntent(
            @Valid @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest request) {
        
        log.info("Creating Stripe Payment Intent for amount: {} {}", 
                paymentRequest.getAmount(), paymentRequest.getCurrency());
        
        // Extract client info
        String ipAddress = getClientIpAddress(request);
        paymentRequest.setIpAddress(ipAddress);
        
        PaymentResponse response = stripeService.createPaymentIntent(paymentRequest);
        
        log.info("Stripe Payment Intent created: {}", response.getPaymentId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Confirm Stripe Payment Intent
     * POST /api/v1/payments/stripe/{paymentIntentId}/confirm
     */
    @PostMapping("/{paymentIntentId}/confirm")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> confirmPaymentIntent(
            @PathVariable @NotBlank String paymentIntentId,
            @RequestParam(required = false) String paymentMethodId) {
        
        log.info("Confirming Stripe Payment Intent: {}", paymentIntentId);
        
        PaymentResponse response = stripeService.confirmPaymentIntent(
                paymentIntentId, paymentMethodId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create Stripe Customer
     * POST /api/v1/payments/stripe/customers
     */
    @PostMapping("/customers")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, String>> createCustomer(
            @RequestParam @NotBlank String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone) {
        
        log.info("Creating Stripe customer for email: {}", email);
        
        Map<String, String> response = stripeService.createCustomer(email, name, phone);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Attach Payment Method to Customer
     * POST /api/v1/payments/stripe/customers/{customerId}/payment-methods
     */
    @PostMapping("/customers/{customerId}/payment-methods")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, String>> attachPaymentMethod(
            @PathVariable @NotBlank String customerId,
            @RequestParam @NotBlank String paymentMethodId) {
        
        log.info("Attaching payment method to customer: {}", customerId);
        
        Map<String, String> response = stripeService.attachPaymentMethod(
                customerId, paymentMethodId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get customer's payment methods
     * GET /api/v1/payments/stripe/customers/{customerId}/payment-methods
     */
    @GetMapping("/customers/{customerId}/payment-methods")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getPaymentMethods(
            @PathVariable @NotBlank String customerId) {
        
        log.info("Fetching payment methods for customer: {}", customerId);
        
        Map<String, Object> response = stripeService.getCustomerPaymentMethods(customerId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Detach Payment Method from Customer
     * DELETE /api/v1/payments/stripe/payment-methods/{paymentMethodId}
     */
    @DeleteMapping("/payment-methods/{paymentMethodId}")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, String>> detachPaymentMethod(
            @PathVariable @NotBlank String paymentMethodId) {
        
        log.info("Detaching payment method: {}", paymentMethodId);
        
        Map<String, String> response = stripeService.detachPaymentMethod(paymentMethodId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create refund
     * POST /api/v1/payments/stripe/{paymentIntentId}/refund
     */
    @PostMapping("/{paymentIntentId}/refund")
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> createRefund(
            @PathVariable @NotBlank String paymentIntentId,
            @RequestParam(required = false) Long amount,
            @RequestParam(required = false) String reason) {
        
        log.info("Creating refund for Payment Intent: {}", paymentIntentId);
        
        PaymentResponse response = stripeService.createRefund(
                paymentIntentId, amount, reason);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get Stripe payment details
     * GET /api/v1/payments/stripe/{paymentIntentId}
     */
    @GetMapping("/{paymentIntentId}")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> getPaymentIntent(
            @PathVariable @NotBlank String paymentIntentId) {
        
        log.info("Fetching Stripe Payment Intent: {}", paymentIntentId);
        
        PaymentResponse response = stripeService.getPaymentIntent(paymentIntentId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to extract client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}