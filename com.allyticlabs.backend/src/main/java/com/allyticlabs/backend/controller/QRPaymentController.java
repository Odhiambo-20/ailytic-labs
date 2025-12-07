// ============================================================================
// File: controller/QRPaymentController.java
// ============================================================================
package com.allyticlabs.backend.controller;

import com.allyticlabs.backend.dto.PaymentResponse;
import com.allyticlabs.backend.dto.QRPaymentRequest;
import com.allyticlabs.backend.service.QRCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/qr")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class QRPaymentController {

    private final QRCodeService qrCodeService;

    /**
     * Generate QR code for payment
     * POST /api/v1/payments/qr/generate
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> generateQRCode(
            @Valid @RequestBody QRPaymentRequest qrRequest,
            HttpServletRequest request) {
        
        log.info("QR code generation request for amount: {} {}", 
                qrRequest.getAmount(), qrRequest.getCurrency());
        
        String ipAddress = getClientIpAddress(request);
        qrRequest.setIpAddress(ipAddress);
        
        Map<String, Object> response = qrCodeService.generateQRCode(qrRequest);
        
        log.info("QR code generated: {}", response.get("qrCodeToken"));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get QR code image
     * GET /api/v1/payments/qr/{qrCodeToken}/image
     */
    @GetMapping(value = "/{qrCodeToken}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCodeImage(
            @PathVariable @NotBlank String qrCodeToken) {
        
        log.info("Fetching QR code image: {}", qrCodeToken);
        
        byte[] qrImage = qrCodeService.getQRCodeImage(qrCodeToken);
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }

    /**
     * Scan QR code and retrieve payment details
     * GET /api/v1/payments/qr/{qrCodeToken}/scan
     */
    @GetMapping("/{qrCodeToken}/scan")
    public ResponseEntity<Map<String, Object>> scanQRCode(
            @PathVariable @NotBlank String qrCodeToken,
            @RequestParam @NotBlank String totp,
            HttpServletRequest request) {
        
        log.info("QR code scan request: {}", qrCodeToken);
        
        String ipAddress = getClientIpAddress(request);
        String deviceFingerprint = request.getHeader("X-Device-Fingerprint");
        
        Map<String, Object> paymentDetails = qrCodeService.scanQRCode(
                qrCodeToken, totp, ipAddress, deviceFingerprint);
        
        log.info("QR code scanned successfully");
        
        return ResponseEntity.ok(paymentDetails);
    }

    /**
     * Process QR code payment
     * POST /api/v1/payments/qr/{qrCodeToken}/pay
     */
    @PostMapping("/{qrCodeToken}/pay")
    @PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN')")
    public ResponseEntity<PaymentResponse> processQRPayment(
            @PathVariable @NotBlank String qrCodeToken,
            @RequestParam @NotBlank String totp,
            @RequestParam @NotBlank String paymentMethod,
            @RequestBody(required = false) Map<String, String> paymentDetails,
            HttpServletRequest request) {
        
        log.info("Processing QR payment: {}", qrCodeToken);
        
        String ipAddress = getClientIpAddress(request);
        String deviceFingerprint = request.getHeader("X-Device-Fingerprint");
        
        PaymentResponse response = qrCodeService.processQRPayment(
                qrCodeToken, totp, paymentMethod, paymentDetails, 
                ipAddress, deviceFingerprint);
        
        log.info("QR payment processed: {}", response.getPaymentId());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get QR payment status
     * GET /api/v1/payments/qr/{qrCodeToken}/status
     */
    @GetMapping("/{qrCodeToken}/status")
    public ResponseEntity<Map<String, Object>> getQRPaymentStatus(
            @PathVariable @NotBlank String qrCodeToken) {
        
        log.info("Checking QR payment status: {}", qrCodeToken);
        
        Map<String, Object> status = qrCodeService.getQRPaymentStatus(qrCodeToken);
        
        return ResponseEntity.ok(status);
    }

    /**
     * Cancel QR payment
     * POST /api/v1/payments/qr/{qrCodeToken}/cancel
     */
    @PostMapping("/{qrCodeToken}/cancel")
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, String>> cancelQRPayment(
            @PathVariable @NotBlank String qrCodeToken,
            @RequestParam(required = false) String reason) {
        
        log.info("Cancelling QR payment: {}", qrCodeToken);
        
        Map<String, String> response = qrCodeService.cancelQRPayment(qrCodeToken, reason);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Validate QR code (check expiry and usage)
     * GET /api/v1/payments/qr/{qrCodeToken}/validate
     */
    @GetMapping("/{qrCodeToken}/validate")
    public ResponseEntity<Map<String, Object>> validateQRCode(
            @PathVariable @NotBlank String qrCodeToken,
            @RequestParam @NotBlank String totp) {
        
        log.info("Validating QR code: {}", qrCodeToken);
        
        Map<String, Object> validation = qrCodeService.validateQRCode(qrCodeToken, totp);
        
        return ResponseEntity.ok(validation);
    }

    /**
     * Get merchant's QR codes
     * GET /api/v1/payments/qr/merchant/{merchantId}
     */
    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getMerchantQRCodes(
            @PathVariable @NotBlank String merchantId,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Fetching QR codes for merchant: {}", merchantId);
        
        Map<String, Object> qrCodes = qrCodeService.getMerchantQRCodes(merchantId, limit);
        
        return ResponseEntity.ok(qrCodes);
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