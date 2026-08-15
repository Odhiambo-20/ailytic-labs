package com.bellatechnologies.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Configuration class for M-Pesa STK Push and Payment Integration
 * Handles Daraja API configuration and authentication
 *
 * PRODUCTION READY - Uses full absolute URLs for all M-Pesa API endpoints
 */
@Configuration
@ConfigurationProperties(prefix = "mpesa")
@Data
public class MpesaConfig {

    // M-Pesa Daraja API Credentials
    private String consumerKey;
    private String consumerSecret;
    private String passKey;
    private String shortCode;
    private String initiatorName;
    private String initiatorPassword;

    // Callback URLs
    private String callbackUrl;
    private String timeoutUrl;
    private String resultUrl;
    private String validationUrl;
    private String confirmationUrl;

    // Configuration Settings
    private String environment = "sandbox"; // sandbox or production
    private int connectionTimeout = 30000; // 30 seconds
    private int readTimeout = 30000;

    // Transaction Settings
    private String transactionType = "CustomerPayBillOnline";
    private String commandId = "TransactionReversal";
    private String partyA; // Customer MSISDN
    private String partyB; // Organization receiving the funds
    private String accountReference = "Payment";
    private String transactionDesc = "Payment for services";

    /**
     * Generate OAuth token for M-Pesa API authentication
     * @return Base64 encoded credentials
     */
    public String generateAuthToken() {
        String credentials = consumerKey + ":" + consumerSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    /**
     * Generate password for STK Push
     * Format: Base64(Shortcode + Passkey + Timestamp)
     * @param timestamp Transaction timestamp
     * @return Base64 encoded password
     */
    public String generatePassword(String timestamp) {
        String rawPassword = shortCode + passKey + timestamp;
        return Base64.getEncoder().encodeToString(rawPassword.getBytes());
    }

    /**
     * Get security credential for B2C and other secured APIs
     * This should be encrypted using M-Pesa public key
     * @return Security credential
     */
    public String getSecurityCredential() {
        // In production, encrypt initiatorPassword with M-Pesa public certificate
        // For now, returning base64 encoded password (update with proper encryption)
        return Base64.getEncoder().encodeToString(initiatorPassword.getBytes());
    }

    /**
     * Configure RestTemplate with timeouts and interceptors for M-Pesa API calls
     * @return Configured RestTemplate
     */
    @Bean(name = "mpesaRestTemplate")
    public RestTemplate mpesaRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);

        RestTemplate restTemplate = new RestTemplate(factory);

        // Add logging interceptor for debugging
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((request, body, execution) -> {
            // Log request details (remove in production or mask sensitive data)
            System.out.println("M-Pesa API Request: " + request.getMethod() + " " + request.getURI());
            return execution.execute(request, body);
        });

        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    /**
     * Check if running in production environment
     * @return true if production, false if sandbox
     */
    public boolean isProduction() {
        return "production".equalsIgnoreCase(environment);
    }

    /**
     * Get appropriate base URL based on environment
     * @return Base URL for M-Pesa API
     */
    public String getBaseUrl() {
        if (isProduction()) {
            return "https://api.safaricom.co.ke";
        } else {
            return "https://sandbox.safaricom.co.ke";
        }
    }

    /**
     * Validate M-Pesa configuration
     * @return true if configuration is valid
     */
    public boolean isConfigurationValid() {
        return consumerKey != null && !consumerKey.isEmpty() &&
               consumerSecret != null && !consumerSecret.isEmpty() &&
               passKey != null && !passKey.isEmpty() &&
               shortCode != null && !shortCode.isEmpty() &&
               callbackUrl != null && !callbackUrl.isEmpty();
    }

    /**
     * Get full OAuth URL - PRODUCTION READY
     * @return Complete OAuth endpoint URL with grant_type parameter
     */
    public String getFullOauthUrl() {
        return getBaseUrl() + "/oauth/v1/generate?grant_type=client_credentials";
    }

    /**
     * Get full STK Push URL - PRODUCTION READY
     * @return Complete STK Push endpoint URL
     */
    public String getFullStkPushUrl() {
        return getBaseUrl() + "/mpesa/stkpush/v1/processrequest";
    }

    /**
     * Get full STK Query URL - PRODUCTION READY
     * @return Complete STK Query endpoint URL
     */
    public String getFullStkQueryUrl() {
        return getBaseUrl() + "/mpesa/stkpushquery/v1/query";
    }

    /**
     * Get full C2B Register URL - PRODUCTION READY
     * @return Complete C2B Register endpoint URL
     */
    public String getFullC2BRegisterUrl() {
        return getBaseUrl() + "/mpesa/c2b/v1/registerurl";
    }

    /**
     * Get full C2B Simulate URL - PRODUCTION READY
     * @return Complete C2B Simulate endpoint URL
     */
    public String getFullC2BSimulateUrl() {
        return getBaseUrl() + "/mpesa/c2b/v1/simulate";
    }

    /**
     * Get full B2C URL - PRODUCTION READY
     * @return Complete B2C endpoint URL
     */
    public String getFullB2CUrl() {
        return getBaseUrl() + "/mpesa/b2c/v1/paymentrequest";
    }

    /**
     * Get full Reversal URL - PRODUCTION READY
     * @return Complete Reversal endpoint URL
     */
    public String getFullReversalUrl() {
        return getBaseUrl() + "/mpesa/reversal/v1/request";
    }

    /**
     * Get full Transaction Status URL - PRODUCTION READY
     * @return Complete Transaction Status endpoint URL
     */
    public String getFullTransactionStatusUrl() {
        return getBaseUrl() + "/mpesa/transactionstatus/v1/query";
    }

    /**
     * Get full Account Balance URL - PRODUCTION READY
     * @return Complete Account Balance endpoint URL
     */
    public String getFullAccountBalanceUrl() {
        return getBaseUrl() + "/mpesa/accountbalance/v1/query";
    }

    /**
     * Mask sensitive configuration data for logging
     * @return Masked configuration string
     */
    public String getMaskedConfig() {
        return String.format(
            "MpesaConfig[shortCode=%s, environment=%s, consumerKey=%s***, callbackUrl=%s, baseUrl=%s]",
            shortCode,
            environment,
            consumerKey != null ? consumerKey.substring(0, Math.min(4, consumerKey.length())) : "null",
            callbackUrl,
            getBaseUrl()
        );
    }

    /**
     * Log configuration on startup for debugging
     */
    public void logConfiguration() {
        System.out.println("=".repeat(80));
        System.out.println("M-PESA CONFIGURATION LOADED");
        System.out.println("=".repeat(80));
        System.out.println("Environment: " + environment);
        System.out.println("Base URL: " + getBaseUrl());
        System.out.println("Short Code: " + shortCode);
        System.out.println("Consumer Key: " + (consumerKey != null ? consumerKey.substring(0, 4) + "***" : "NOT SET"));
        System.out.println("PassKey: " + (passKey != null ? "***SET***" : "NOT SET"));
        System.out.println("Callback URL: " + callbackUrl);
        System.out.println("OAuth URL: " + getFullOauthUrl());
        System.out.println("STK Push URL: " + getFullStkPushUrl());
        System.out.println("Configuration Valid: " + isConfigurationValid());
        System.out.println("=".repeat(80));
    }
}
