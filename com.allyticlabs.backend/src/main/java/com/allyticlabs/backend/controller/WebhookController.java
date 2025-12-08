package com.allyticlabs.backend.controller;

import com.allyticlabs.backend.dto.MpesaCallbackRequest;
import com.allyticlabs.backend.service.WebhookVerificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final WebhookVerificationService webhookVerificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Stripe webhook endpoint
     * POST /api/v1/webhooks/stripe
     */
    @PostMapping("/stripe")
    public ResponseEntity<Map<String, String>> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature,
            HttpServletRequest request) {

        log.info("Stripe webhook received");

        Map<String, String> headers = extractHeaders(request);

        try {
            // Verify webhook signature
            boolean isValid = webhookVerificationService.verifyStripeSignature(
                    payload, signature);

            if (!isValid) {
                log.error("Invalid Stripe webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid signature"));
            }

            // Parse the JSON payload to extract event details
            JsonNode eventJson = objectMapper.readTree(payload);
            String eventId = eventJson.path("id").asText();
            String eventType = eventJson.path("type").asText();

            // Process webhook with correct parameters
            webhookVerificationService.processStripeWebhook(
                    eventId, eventType, headers);

            log.info("Stripe webhook processed successfully");

            return ResponseEntity.ok(Map.of("status", "success"));

        } catch (Exception e) {
            log.error("Error processing Stripe webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Processing failed"));
        }
    }

    /**
     * M-Pesa validation endpoint
     * POST /api/v1/webhooks/mpesa/validation
     */
    @PostMapping("/mpesa/validation")
    public ResponseEntity<Map<String, String>> validateMpesaTransaction(
            @RequestBody MpesaCallbackRequest validationRequest,
            @RequestHeader(value = "X-Signature", required = false) String signature,
            HttpServletRequest request) {

        log.info("M-Pesa validation request received");

        try {
            // Validate the transaction with signature
            boolean isValid = webhookVerificationService.validateMpesaTransaction(
                    validationRequest, signature);

            if (isValid) {
                return ResponseEntity.ok(Map.of(
                        "ResultCode", "0",
                        "ResultDesc", "Accepted"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "ResultCode", "1",
                        "ResultDesc", "Rejected"
                ));
            }

        } catch (Exception e) {
            log.error("Error validating M-Pesa transaction", e);
            return ResponseEntity.ok(Map.of(
                    "ResultCode", "1",
                    "ResultDesc", "System error"
            ));
        }
    }

    /**
     * M-Pesa confirmation endpoint
     * POST /api/v1/webhooks/mpesa/confirmation
     */
    @PostMapping("/mpesa/confirmation")
    public ResponseEntity<Map<String, String>> confirmMpesaTransaction(
            @RequestBody MpesaCallbackRequest confirmationRequest,
            HttpServletRequest request) {

        log.info("M-Pesa confirmation request received");

        Map<String, String> headers = extractHeaders(request);

        try {
            // Extract transaction ID from request
            String transactionId = extractMpesaTransactionId(confirmationRequest);

            // Process confirmation with correct parameters
            webhookVerificationService.processMpesaConfirmation(
                    confirmationRequest, transactionId, headers);

            log.info("M-Pesa confirmation processed successfully");

            return ResponseEntity.ok(Map.of(
                    "ResultCode", "0",
                    "ResultDesc", "Accepted"
            ));

        } catch (Exception e) {
            log.error("Error processing M-Pesa confirmation", e);
            return ResponseEntity.ok(Map.of(
                    "ResultCode", "1",
                    "ResultDesc", "Processing failed"
            ));
        }
    }

    /**
     * M-Pesa timeout endpoint
     * POST /api/v1/webhooks/mpesa/timeout
     */
    @PostMapping("/mpesa/timeout")
    public ResponseEntity<Map<String, String>> handleMpesaTimeout(
            @RequestBody MpesaCallbackRequest timeoutRequest,
            HttpServletRequest request) {

        log.info("M-Pesa timeout request received");

        try {
            // Extract transaction ID from request
            String transactionId = extractMpesaTransactionId(timeoutRequest);

            webhookVerificationService.processMpesaTimeout(timeoutRequest, transactionId);

            return ResponseEntity.ok(Map.of(
                    "ResultCode", "0",
                    "ResultDesc", "Accepted"
            ));

        } catch (Exception e) {
            log.error("Error processing M-Pesa timeout", e);
            return ResponseEntity.ok(Map.of(
                    "ResultCode", "1",
                    "ResultDesc", "Processing failed"
            ));
        }
    }

    /**
     * M-Pesa result endpoint (for STK Push)
     * POST /api/v1/webhooks/mpesa/result
     */
    @PostMapping("/mpesa/result")
    public ResponseEntity<Map<String, String>> handleMpesaResult(
            @RequestBody MpesaCallbackRequest resultRequest,
            HttpServletRequest request) {

        log.info("M-Pesa result request received");

        Map<String, String> headers = extractHeaders(request);

        try {
            // Extract transaction ID from request
            String transactionId = extractMpesaTransactionId(resultRequest);

            webhookVerificationService.processMpesaResult(
                    resultRequest, transactionId, headers);

            log.info("M-Pesa result processed successfully");

            return ResponseEntity.ok(Map.of(
                    "ResultCode", "0",
                    "ResultDesc", "Accepted"
            ));

        } catch (Exception e) {
            log.error("Error processing M-Pesa result", e);
            return ResponseEntity.ok(Map.of(
                    "ResultCode", "1",
                    "ResultDesc", "Processing failed"
            ));
        }
    }

    /**
     * Generic webhook health check
     * GET /api/v1/webhooks/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "webhook-controller"
        ));
    }

    /**
     * Get webhook logs
     * GET /api/v1/webhooks/logs
     */
    @GetMapping("/logs")
    public ResponseEntity<List<Map<String, Object>>> getWebhookLogs(
            @RequestParam(required = false) String transactionId,
            @RequestParam(defaultValue = "20") int limit) {

        log.info("Fetching webhook logs");

        // Service returns List<Map<String, Object>>
        List<Map<String, Object>> logs = webhookVerificationService.getWebhookLogs(
                transactionId, limit);

        return ResponseEntity.ok(logs);
    }

    /**
     * Retry failed webhook
     * POST /api/v1/webhooks/{webhookId}/retry
     */
    @PostMapping("/{webhookId}/retry")
    public ResponseEntity<Map<String, String>> retryWebhook(
            @PathVariable String webhookId) {

        log.info("Retrying webhook: {}", webhookId);

        try {
            webhookVerificationService.retryWebhook(webhookId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Webhook retry initiated"
            ));

        } catch (Exception e) {
            log.error("Error retrying webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Retry failed"));
        }
    }

    /**
     * Helper methods
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

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        return headers;
    }

    /**
     * Extract transaction ID from M-Pesa callback request
     * Priority: CheckoutRequestID > TransID > MpesaReceiptNumber > generated UUID
     */
    private String extractMpesaTransactionId(MpesaCallbackRequest request) {
        // For STK Push callbacks, use CheckoutRequestID
        if (request.getCheckoutRequestId() != null) {
            return request.getCheckoutRequestId();
        }
        
        // For C2B callbacks, use TransID
        if (request.getTransId() != null) {
            return request.getTransId();
        }
        
        // For confirmed transactions, use MpesaReceiptNumber
        if (request.getMpesaReceiptNumber() != null) {
            return request.getMpesaReceiptNumber();
        }
        
        // Fallback to generating a unique ID if not present
        return UUID.randomUUID().toString();
    }
}