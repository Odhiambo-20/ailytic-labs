// ============================================================================
// File: controller/PaymentController.java
// ============================================================================
package com.allyticlabs.backend.controller;

import com.allyticlabs.backend.dto.PaymentRequest;
import com.allyticlabs.backend.dto.PaymentResponse;
import com.allyticlabs.backend.model.Payment;
import com.allyticlabs.backend.model.PaymentStatus;
import com.allyticlabs.backend.service.PaymentService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Initiate a new payment
     * POST /api/v1/payments
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @Valid @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest request) {
        
        log.info("Payment initiation request received for amount: {} {}", 
                paymentRequest.getAmount(), paymentRequest.getCurrency());
        
        // Extract client info
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        // Add client info to request
        paymentRequest.setIpAddress(ipAddress);
        paymentRequest.setUserAgent(userAgent);
        
        PaymentResponse response = paymentService.initiatePayment(paymentRequest);
        
        log.info("Payment initiated successfully: {}", response.getPaymentId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get payment by ID
     * GET /api/v1/payments/{paymentId}
     */
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable @NotBlank String paymentId) {
        
        log.info("Fetching payment details for: {}", paymentId);
        
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment status
     * GET /api/v1/payments/{paymentId}/status
     */
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(
            @PathVariable @NotBlank String paymentId) {
        
        log.info("Checking payment status for: {}", paymentId);
        
        PaymentStatus status = paymentService.getPaymentStatus(paymentId);
        
        return ResponseEntity.ok(Map.of(
                "paymentId", paymentId,
                "status", status,
                "description", status.getDescription()
        ));
    }

    /**
     * Get all payments for a user
     * GET /api/v1/payments/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getUserPayments(
            @PathVariable @NotBlank String userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Fetching payments for user: {}", userId);
        
        List<PaymentResponse> payments = paymentService.getPaymentsByUserId(userId, limit);
        
        return ResponseEntity.ok(payments);
    }

    /**
     * Get all payments for a merchant
     * GET /api/v1/payments/merchant/{merchantId}
     */
    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getMerchantPayments(
            @PathVariable @NotBlank String merchantId,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Fetching payments for merchant: {}", merchantId);
        
        List<PaymentResponse> payments = paymentService.getPaymentsByMerchantId(merchantId, limit);
        
        return ResponseEntity.ok(payments);
    }

    /**
     * Cancel a pending payment
     * POST /api/v1/payments/{paymentId}/cancel
     */
    @PostMapping("/{paymentId}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @PathVariable @NotBlank String paymentId,
            @RequestParam(required = false) String reason) {
        
        log.info("Cancelling payment: {}", paymentId);
        
        PaymentResponse response = paymentService.cancelPayment(paymentId, reason);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verify payment completion
     * POST /api/v1/payments/{paymentId}/verify
     */
    @PostMapping("/{paymentId}/verify")
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> verifyPayment(
            @PathVariable @NotBlank String paymentId) {
        
        log.info("Verifying payment: {}", paymentId);
        
        PaymentResponse response = paymentService.verifyPayment(paymentId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Refund a completed payment
     * POST /api/v1/payments/{paymentId}/refund
     */
    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable @NotBlank String paymentId,
            @RequestParam(required = false) String amount,
            @RequestParam(required = false) String reason) {
        
        log.info("Processing refund for payment: {}", paymentId);
        
        PaymentResponse response = paymentService.refundPayment(paymentId, amount, reason);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment statistics
     * GET /api/v1/payments/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getPaymentStats(
            @RequestParam(required = false) String merchantId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        log.info("Fetching payment statistics");
        
        Map<String, Object> stats = paymentService.getPaymentStatistics(merchantId, startDate, endDate);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Helper method to extract client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}



