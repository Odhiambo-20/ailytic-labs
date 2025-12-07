package com.allyticlabs.backend.config;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for QR Code Payment System
 * Implements WeChat-style QR code payment configuration
 */
@Configuration
@ConfigurationProperties(prefix = "qr.payment")
@Data
public class QRCodeConfig {

    // QR Code Generation Settings
    private int qrCodeWidth = 300;
    private int qrCodeHeight = 300;
    private String imageFormat = "PNG";
    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.H; // Highest error correction
    private String charset = "UTF-8";
    private int margin = 1; // Border/margin around QR code
    
    // QR Code Styling
    private String foregroundColor = "#000000"; // Black
    private String backgroundColor = "#FFFFFF"; // White
    private boolean includeLogo = true;
    private int logoSize = 60; // Logo size in pixels
    private String logoPath = "/static/images/logo.png";
    
    // QR Code Content Settings
    private String qrCodePrefix = "PAY"; // Prefix for QR codes
    private int qrCodeLength = 32; // Length of QR code identifier
    private String qrCodeFormat = "JSON"; // JSON or URL format
    
    // Payment Settings
    private int defaultExpiryMinutes = 15; // QR codes expire after 15 minutes
    private int maxExpiryMinutes = 60; // Maximum expiry time (1 hour)
    private int minExpiryMinutes = 5; // Minimum expiry time
    
    // Static vs Dynamic QR Codes
    private boolean supportStaticQR = true; // Merchant static QR codes
    private boolean supportDynamicQR = true; // Transaction-specific QR codes
    private int staticQRExpiryDays = 365; // Static QR codes valid for 1 year
    
    // Security Settings
    private boolean encryptQRContent = true;
    private boolean includeChecksum = true;
    private String checksumAlgorithm = "SHA-256";
    private boolean requireSecureContext = true; // HTTPS only
    
    // Rate Limiting for QR Generation
    private int maxQRGenerationsPerUser = 100; // Per day
    private int maxQRGenerationsPerMerchant = 1000; // Per day
    private boolean enableRateLimiting = true;
    
    // QR Code Types (WeChat-style)
    private boolean enablePersonalQR = true; // User receives payment
    private boolean enableMerchantQR = true; // Business receives payment
    private boolean enableGroupQR = false; // Group payment (split bill)
    
    // URL Settings
    private String baseUrl; // Base URL for QR code redirects
    private String paymentPath = "/payment/qr"; // Path for QR payment processing
    
    // Storage Settings
    private boolean storeQRImages = false; // Store generated QR images
    private String qrImageStoragePath = "/qr-codes/";
    private boolean cleanupExpiredQR = true;
    private int cleanupIntervalHours = 24;
    
    // Notification Settings
    private boolean notifyOnScan = true;
    private boolean notifyOnPayment = true;
    private int notificationDelayMs = 2000; // 2 seconds delay
    
    // Analytics
    private boolean trackQRScans = true;
    private boolean trackQRPayments = true;
    private boolean enableGeolocation = false;
    
    /**
     * Configure QR Code encoding hints
     */
    @Bean
    public Map<EncodeHintType, Object> qrCodeHints() {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.CHARACTER_SET, charset);
        hints.put(EncodeHintType.MARGIN, margin);
        return hints;
    }
    
    /**
     * Get foreground color as AWT Color
     */
    public Color getForegroundColorAWT() {
        return Color.decode(foregroundColor);
    }
    
    /**
     * Get background color as AWT Color
     */
    public Color getBackgroundColorAWT() {
        return Color.decode(backgroundColor);
    }
    
    /**
     * Generate QR code data format
     * WeChat-style JSON format for QR code content
     */
    public String generateQRDataFormat(
            String qrId, 
            String merchantId, 
            double amount, 
            String currency,
            long expiryTimestamp) {
        
        if ("JSON".equalsIgnoreCase(qrCodeFormat)) {
            return String.format(
                "{\"id\":\"%s\",\"merchant\":\"%s\",\"amount\":%.2f,\"currency\":\"%s\",\"exp\":%d}",
                qrId, merchantId, amount, currency, expiryTimestamp
            );
        } else {
            // URL format
            return String.format(
                "%s%s?id=%s&merchant=%s&amount=%.2f&currency=%s&exp=%d",
                baseUrl, paymentPath, qrId, merchantId, amount, currency, expiryTimestamp
            );
        }
    }
    
    /**
     * Get full payment URL for QR code
     */
    public String getPaymentUrl(String qrId) {
        return baseUrl + paymentPath + "/" + qrId;
    }
    
    /**
     * Calculate QR code expiry timestamp
     */
    public long calculateExpiryTimestamp(int expiryMinutes) {
        if (expiryMinutes < minExpiryMinutes) {
            expiryMinutes = minExpiryMinutes;
        }
        if (expiryMinutes > maxExpiryMinutes) {
            expiryMinutes = maxExpiryMinutes;
        }
        return System.currentTimeMillis() + (expiryMinutes * 60 * 1000L);
    }
    
    /**
     * Validate QR code dimensions
     */
    public boolean validateDimensions(int width, int height) {
        return width >= 100 && width <= 1000 && 
               height >= 100 && height <= 1000;
    }
    
    /**
     * Get recommended QR code size based on display type
     */
    public int getRecommendedSize(String displayType) {
        switch (displayType.toLowerCase()) {
            case "mobile":
                return 250;
            case "tablet":
                return 350;
            case "desktop":
                return 400;
            case "print":
                return 600;
            case "billboard":
                return 1000;
            default:
                return qrCodeWidth;
        }
    }
    
    /**
     * Generate unique QR code identifier
     */
    public String generateQRIdentifier() {
        return qrCodePrefix + "_" + java.util.UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, qrCodeLength);
    }
    
    /**
     * Validate QR code identifier format
     */
    public boolean isValidQRIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return false;
        }
        
        // Check prefix
        if (!identifier.startsWith(qrCodePrefix + "_")) {
            return false;
        }
        
        // Check length
        int expectedLength = qrCodePrefix.length() + 1 + qrCodeLength;
        return identifier.length() == expectedLength;
    }
    
    /**
     * Get QR code configuration for different payment types
     */
    public QRTypeConfig getQRTypeConfig(String type) {
        QRTypeConfig config = new QRTypeConfig();
        
        switch (type.toLowerCase()) {
            case "merchant":
                config.setExpiryMinutes(defaultExpiryMinutes);
                config.setAllowMultipleUse(false);
                config.setRequireAmount(true);
                break;
            case "static_merchant":
                config.setExpiryMinutes(staticQRExpiryDays * 24 * 60);
                config.setAllowMultipleUse(true);
                config.setRequireAmount(false);
                break;
            case "personal":
                config.setExpiryMinutes(defaultExpiryMinutes);
                config.setAllowMultipleUse(false);
                config.setRequireAmount(true);
                break;
            case "group":
                config.setExpiryMinutes(maxExpiryMinutes);
                config.setAllowMultipleUse(true);
                config.setRequireAmount(false);
                break;
            default:
                config.setExpiryMinutes(defaultExpiryMinutes);
                config.setAllowMultipleUse(false);
                config.setRequireAmount(true);
        }
        
        return config;
    }
    
    /**
     * Inner class for QR type-specific configuration
     */
    @Data
    public static class QRTypeConfig {
        private int expiryMinutes;
        private boolean allowMultipleUse;
        private boolean requireAmount;
    }
    
    /**
     * Get file extension for image format
     */
    public String getFileExtension() {
        return "." + imageFormat.toLowerCase();
    }
    
    /**
     * Calculate logo position (centered)
     */
    public int getLogoX() {
        return (qrCodeWidth - logoSize) / 2;
    }
    
    public int getLogoY() {
        return (qrCodeHeight - logoSize) / 2;
    }
    
    /**
     * Validate configuration
     */
    public boolean isConfigurationValid() {
        return qrCodeWidth > 0 && 
               qrCodeHeight > 0 && 
               defaultExpiryMinutes > 0 &&
               baseUrl != null && !baseUrl.isEmpty() &&
               validateDimensions(qrCodeWidth, qrCodeHeight);
    }
    
    /**
     * Get QR code quality settings
     */
    public Map<String, Object> getQualitySettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("width", qrCodeWidth);
        settings.put("height", qrCodeHeight);
        settings.put("errorCorrection", errorCorrectionLevel.name());
        settings.put("format", imageFormat);
        settings.put("charset", charset);
        settings.put("margin", margin);
        return settings;
    }
    
    /**
     * Get display configuration for client applications
     */
    public Map<String, Object> getDisplayConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("width", qrCodeWidth);
        config.put("height", qrCodeHeight);
        config.put("format", imageFormat);
        config.put("expiryMinutes", defaultExpiryMinutes);
        config.put("refreshInterval", defaultExpiryMinutes * 60 * 1000); // milliseconds
        return config;
    }
    
    /**
     * Mask configuration for logging
     */
    public String getMaskedConfig() {
        return String.format(
            "QRCodeConfig[size=%dx%d, format=%s, expiry=%dmin, errorCorrection=%s]",
            qrCodeWidth,
            qrCodeHeight,
            imageFormat,
            defaultExpiryMinutes,
            errorCorrectionLevel.name()
        );
    }
}