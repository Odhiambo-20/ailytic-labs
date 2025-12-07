package com.payment.config;

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
 */
@Configuration
@ConfigurationProperties(prefix = "mpesa")
@Data
public class MpesaConfig {

    // M-Pesa Daraja API Credentials
    private String consumerKey;
    private String consumerSecret;
    private String passkey;
    private String shortCode;
    private String initiatorName;
    private String initiatorPassword;

    // API URLs
    private String oauthUrl;
    private String stkPushUrl;
    private String stkQueryUrl;
    private String b2cUrl;
    private String c2bRegisterUrl;
    private String transactionStatusUrl;
    private String accountBalanceUrl;

    // Callback URLs
    private String callbackUrl;
    private String timeoutUrl;
    private String resultUrl;
    private String validationUrl;
    private String confirmationUrl;

    // Configuration Settings
    private String environment; // sandbox or production
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
        String rawPassword = shortCode + passkey + timestamp;
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
               passkey != null && !passkey.isEmpty() &&
               shortCode != null && !shortCode.isEmpty() &&
               callbackUrl != null && !callbackUrl.isEmpty();
    }

    /**
     * Get full OAuth URL
     * @return Complete OAuth endpoint URL
     */
    public String getFullOauthUrl() {
        return getBaseUrl() + (oauthUrl != null ? oauthUrl : "/oauth/v1/generate?grant_type=client_credentials");
    }

    /**
     * Get full STK Push URL
     * @return Complete STK Push endpoint URL
     */
    public String getFullStkPushUrl() {
        return getBaseUrl() + (stkPushUrl != null ? stkPushUrl : "/mpesa/stkpush/v1/processrequest");
    }

    /**
     * Get full STK Query URL
     * @return Complete STK Query endpoint URL
     */
    public String getFullStkQueryUrl() {
        return getBaseUrl() + (stkQueryUrl != null ? stkQueryUrl : "/mpesa/stkpushquery/v1/query");
    }

    /**
     * Get full Transaction Status URL
     * @return Complete Transaction Status endpoint URL
     */
    public String getFullTransactionStatusUrl() {
        return getBaseUrl() + (transactionStatusUrl != null ? transactionStatusUrl : "/mpesa/transactionstatus/v1/query");
    }

    /**
     * Mask sensitive configuration data for logging
     * @return Masked configuration string
     */
    public String getMaskedConfig() {
        return String.format(
            "MpesaConfig[shortCode=%s, environment=%s, consumerKey=%s, callbackUrl=%s]",
            shortCode,
            environment,
            consumerKey != null ? consumerKey.substring(0, Math.min(4, consumerKey.length())) + "***" : "null",
            callbackUrl
        );
    }
}
