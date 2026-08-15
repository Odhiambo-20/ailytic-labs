// ============================================================================
// File: controller/MpesaController.java
// ============================================================================
package com.bellatechnologies.backend.controller;

import com.bellatechnologies.backend.dto.MpesaCallbackRequest;
import com.bellatechnologies.backend.dto.PaymentRequest;
import com.bellatechnologies.backend.dto.PaymentResponse;
import com.bellatechnologies.backend.service.MpesaService;
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
@RequestMapping("/api/v1/payments/mpesa")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed.origins=https://bellatechnologies-frontend.vercel.app,https://bella-technologies-frontend-git-main-victor-odhiambos-projects.vercel.app,https://bella-technologies-frontend-7l3o6f9fn-victor-odhiambos-projects.vercel.app,http://localhost:3000")
public class MpesaController {

    private final MpesaService mpesaService;

    /**
     * Initiate M-Pesa STK Push
     * POST /api/v1/payments/mpesa/stkpush
     */
    @PostMapping("/stkpush")
    //@PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> initiateSTKPush(
            @Valid @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest request) {

        log.info("M-Pesa STK Push request received for phone: {}",
                maskPhoneNumber(paymentRequest.getPhoneNumber()));

        // Extract client info
        String ipAddress = getClientIpAddress(request);
        paymentRequest.setIpAddress(ipAddress);

        PaymentResponse response = mpesaService.initiateSTKPush(paymentRequest);

        log.info("M-Pesa STK Push initiated: {}", response.getPaymentId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * M-Pesa STK Push Callback (from Safaricom)
     * POST /api/v1/payments/mpesa/callback
     */
    @PostMapping("/callback")
    public ResponseEntity<Map<String, String>> handleSTKCallback(
            @RequestBody MpesaCallbackRequest callbackRequest,
            HttpServletRequest request) {

        log.info("M-Pesa callback received");

        // Extract request metadata
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        try {
            mpesaService.processSTKCallback(callbackRequest, ipAddress, userAgent);

            log.info("M-Pesa callback processed successfully");

            return ResponseEntity.ok(Map.of(
                    "ResultCode", "0",
                    "ResultDesc", "Accepted"
            ));
        } catch (Exception e) {
            log.error("Error processing M-Pesa callback", e);

            return ResponseEntity.ok(Map.of(
                    "ResultCode", "1",
                    "ResultDesc", "Failed to process callback"
            ));
        }
    }

    /**
     * Query M-Pesa transaction status
     * GET /api/v1/payments/mpesa/{checkoutRequestId}/status
     */
    @GetMapping("/{checkoutRequestId}/status")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> queryTransactionStatus(
            @PathVariable @NotBlank String checkoutRequestId) {

        log.info("Querying M-Pesa transaction status: {}", checkoutRequestId);

        Map<String, Object> status = mpesaService.queryTransactionStatus(checkoutRequestId);

        return ResponseEntity.ok(status);
    }

    /**
     * Register M-Pesa C2B URLs
     * POST /api/v1/payments/mpesa/register-urls
     */
    @PostMapping("/register-urls")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> registerC2BUrls(
            @RequestParam @NotBlank String shortCode,
            @RequestParam @NotBlank String validationUrl,
            @RequestParam @NotBlank String confirmationUrl) {

        log.info("Registering M-Pesa C2B URLs for shortcode: {}", shortCode);

        Map<String, String> response = mpesaService.registerC2BUrls(
                shortCode, validationUrl, confirmationUrl);

        return ResponseEntity.ok(response);
    }

    /**
     * Simulate M-Pesa C2B payment (for testing)
     * POST /api/v1/payments/mpesa/simulate-c2b
     */
    @PostMapping("/simulate-c2b")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> simulateC2B(
            @RequestParam @NotBlank String phoneNumber,
            @RequestParam @NotBlank String amount,
            @RequestParam @NotBlank String billRefNumber) {

        log.info("Simulating M-Pesa C2B payment");

        Map<String, String> response = mpesaService.simulateC2B(
                phoneNumber, amount, billRefNumber);

        return ResponseEntity.ok(response);
    }

    /**
     * Get M-Pesa account balance
     * GET /api/v1/payments/mpesa/balance
     */
    @GetMapping("/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAccountBalance() {

        log.info("Fetching M-Pesa account balance");

        Map<String, Object> balance = mpesaService.getAccountBalance();

        return ResponseEntity.ok(balance);
    }

    /**
     * Reverse M-Pesa transaction
     * POST /api/v1/payments/mpesa/reverse
     */
    @PostMapping("/reverse")
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, String>> reverseTransaction(
            @RequestParam @NotBlank String transactionId,
            @RequestParam @NotBlank String amount,
            @RequestParam(required = false) String remarks) {

        log.info("Reversing M-Pesa transaction: {}", transactionId);

        Map<String, String> response = mpesaService.reverseTransaction(
                transactionId, amount, remarks);

        return ResponseEntity.ok(response);
    }

    /**
     * Helper methods
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
