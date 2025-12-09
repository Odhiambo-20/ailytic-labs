package com.allyticlabs.backend.service;

import com.allyticlabs.backend.config.MpesaConfig;
import com.allyticlabs.backend.dto.MpesaCallbackRequest;
import com.allyticlabs.backend.dto.PaymentRequest;
import com.allyticlabs.backend.dto.PaymentResponse;
import com.allyticlabs.backend.exception.PaymentException;
import com.allyticlabs.backend.model.MpesaPayment;
import com.allyticlabs.backend.model.PaymentStatus;
import com.allyticlabs.backend.repository.MpesaPaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpesaService {

    private final MpesaConfig mpesaConfig;
    private final MpesaPaymentRepository mpesaPaymentRepository;
    private final PaymentService paymentService;
    private final PaymentEncryptionService encryptionService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private String accessToken;
    private Instant tokenExpiry;

    @Transactional
    public PaymentResponse initiateSTKPush(PaymentRequest request) {
        log.info("Initiating M-Pesa STK Push for phone: {}", maskPhone(request.getPhoneNumber()));

        try {
            String token = getAccessToken();

            Map<String, Object> stkRequest = new HashMap<>();
            stkRequest.put("BusinessShortCode", mpesaConfig.getShortCode());
            stkRequest.put("Password", generatePassword());
            stkRequest.put("Timestamp", getTimestamp());
            stkRequest.put("TransactionType", "CustomerPayBillOnline");
            stkRequest.put("Amount", request.getAmount().toString());
            stkRequest.put("PartyA", formatPhoneNumber(request.getPhoneNumber()));
            stkRequest.put("PartyB", mpesaConfig.getShortCode());
            stkRequest.put("PhoneNumber", formatPhoneNumber(request.getPhoneNumber()));
            stkRequest.put("CallBackURL", mpesaConfig.getCallbackUrl());
            stkRequest.put("AccountReference", request.getMerchantId());
            stkRequest.put("TransactionDesc", request.getDescription());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(stkRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    mpesaConfig.getStkPushUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode responseBody = objectMapper.readTree(response.getBody());
            String checkoutRequestId = responseBody.get("CheckoutRequestID").asText();
            String merchantRequestId = responseBody.get("MerchantRequestID").asText();
            String responseCode = responseBody.get("ResponseCode").asText();

            if (!"0".equals(responseCode)) {
                throw new PaymentException("M-Pesa STK Push failed: " +
                        responseBody.get("ResponseDescription").asText());
            }

            MpesaPayment mpesaPayment = MpesaPayment.builder()
                    .mpesaTransactionId(checkoutRequestId)
                    .paymentId(request.getPaymentId())
                    .checkoutRequestId(checkoutRequestId)
                    .merchantRequestId(merchantRequestId)
                    .phoneNumber(encryptionService.encrypt(request.getPhoneNumber()))
                    .amount(request.getAmount().doubleValue())
                    .accountReference(request.getMerchantId())
                    .transactionDesc(request.getDescription())
                    .status(PaymentStatus.PROCESSING.name())
                    .build();

            mpesaPayment.setCreatedAt(Instant.now());

            mpesaPaymentRepository.save(mpesaPayment);

            log.info("M-Pesa STK Push sent successfully: {}", checkoutRequestId);

            return PaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .status(PaymentStatus.PROCESSING)
                    .providerTransactionId(checkoutRequestId)
                    .message("STK Push sent to " + maskPhone(request.getPhoneNumber()))
                    .build();

        } catch (Exception e) {
            log.error("Error initiating M-Pesa STK Push", e);
            throw new PaymentException("Failed to initiate M-Pesa payment: " + e.getMessage());
        }
    }

    @Transactional
    public void processSTKCallback(MpesaCallbackRequest callback, String ipAddress, String userAgent) {
        log.info("Processing M-Pesa callback");

        try {
            String checkoutRequestId = callback.getCheckoutRequestId();
            String resultCode = String.valueOf(callback.getResultCode());
            String resultDesc = callback.getResultDesc();

            MpesaPayment mpesaPayment = mpesaPaymentRepository.findByCheckoutRequestId(checkoutRequestId)
                    .orElseThrow(() -> new PaymentException("M-Pesa payment not found: " + checkoutRequestId));

            if ("0".equals(resultCode)) {
                mpesaPayment.setStatus(PaymentStatus.SUCCESS);
                mpesaPayment.setResultCode(resultCode);
                mpesaPayment.setResultDesc(resultDesc);
                mpesaPayment.setMpesaReceiptNumber(callback.getMpesaReceiptNumber());

                if (callback.getTransactionDate() != null) {
                    mpesaPayment.setTransactionDate(callback.getTransactionDate());
                }

                mpesaPayment.setCallbackPayload(objectMapper.writeValueAsString(callback));
                mpesaPayment.setUpdatedAt(Instant.now());

                mpesaPaymentRepository.save(mpesaPayment);

                paymentService.completePayment(
                        mpesaPayment.getPaymentId(),
                        PaymentStatus.SUCCESS,
                        "M-Pesa payment completed: " + callback.getMpesaReceiptNumber()
                );

                log.info("M-Pesa payment completed: {}", callback.getMpesaReceiptNumber());

            } else {
                mpesaPayment.setStatus(PaymentStatus.FAILED);
                mpesaPayment.setResultCode(resultCode);
                mpesaPayment.setResultDesc(resultDesc);
                mpesaPayment.setUpdatedAt(Instant.now());

                mpesaPaymentRepository.save(mpesaPayment);

                paymentService.completePayment(
                        mpesaPayment.getPaymentId(),
                        PaymentStatus.FAILED,
                        "M-Pesa payment failed: " + resultDesc
                );

                log.warn("M-Pesa payment failed: {}", resultDesc);
            }

        } catch (Exception e) {
            log.error("Error processing M-Pesa callback", e);
            throw new PaymentException("Failed to process M-Pesa callback: " + e.getMessage());
        }
    }

    public Map<String, Object> queryTransactionStatus(String checkoutRequestId) {
        log.info("Querying M-Pesa transaction status: {}", checkoutRequestId);

        try {
            String token = getAccessToken();

            Map<String, Object> queryRequest = new HashMap<>();
            queryRequest.put("BusinessShortCode", mpesaConfig.getShortCode());
            queryRequest.put("Password", generatePassword());
            queryRequest.put("Timestamp", getTimestamp());
            queryRequest.put("CheckoutRequestID", checkoutRequestId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(queryRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    mpesaConfig.getStkPushQueryUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode responseBody = objectMapper.readTree(response.getBody());

            return Map.of(
                    "checkoutRequestId", checkoutRequestId,
                    "resultCode", responseBody.get("ResultCode").asText(),
                    "resultDesc", responseBody.get("ResultDesc").asText()
            );

        } catch (Exception e) {
            log.error("Error querying M-Pesa transaction", e);
            throw new PaymentException("Failed to query transaction: " + e.getMessage());
        }
    }

    public Map<String, String> registerC2BUrls(String shortCode, String validationUrl, String confirmationUrl) {
        log.info("Registering M-Pesa C2B URLs");

        try {
            String token = getAccessToken();

            Map<String, Object> registerRequest = new HashMap<>();
            registerRequest.put("ShortCode", shortCode);
            registerRequest.put("ResponseType", "Completed");
            registerRequest.put("ConfirmationURL", confirmationUrl);
            registerRequest.put("ValidationURL", validationUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(registerRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    mpesaConfig.getC2bRegisterUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return Map.of("status", "success", "response", response.getBody());

        } catch (Exception e) {
            log.error("Error registering C2B URLs", e);
            throw new PaymentException("Failed to register C2B URLs: " + e.getMessage());
        }
    }

    public Map<String, String> simulateC2B(String phoneNumber, String amount, String billRefNumber) {
        log.info("Simulating M-Pesa C2B payment");

        try {
            String token = getAccessToken();

            Map<String, Object> simulateRequest = new HashMap<>();
            simulateRequest.put("ShortCode", mpesaConfig.getShortCode());
            simulateRequest.put("CommandID", "CustomerPayBillOnline");
            simulateRequest.put("Amount", amount);
            simulateRequest.put("Msisdn", formatPhoneNumber(phoneNumber));
            simulateRequest.put("BillRefNumber", billRefNumber);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(simulateRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    mpesaConfig.getC2bSimulateUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return Map.of("status", "success", "response", response.getBody());

        } catch (Exception e) {
            log.error("Error simulating C2B", e);
            throw new PaymentException("Failed to simulate C2B: " + e.getMessage());
        }
    }

    public Map<String, Object> getAccountBalance() {
        log.info("Fetching M-Pesa account balance");

        try {
            String token = getAccessToken();

            Map<String, Object> balanceRequest = new HashMap<>();
            balanceRequest.put("Initiator", mpesaConfig.getInitiatorName());
            balanceRequest.put("SecurityCredential", mpesaConfig.getSecurityCredential());
            balanceRequest.put("CommandID", "AccountBalance");
            balanceRequest.put("PartyA", mpesaConfig.getShortCode());
            balanceRequest.put("IdentifierType", "4");
            balanceRequest.put("Remarks", "Balance query");
            balanceRequest.put("QueueTimeOutURL", mpesaConfig.getTimeoutUrl());
            balanceRequest.put("ResultURL", mpesaConfig.getResultUrl());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(balanceRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    mpesaConfig.getAccountBalanceUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode responseBody = objectMapper.readTree(response.getBody());

            return Map.of(
                    "conversationId", responseBody.get("ConversationID").asText(),
                    "originatorConversationId", responseBody.get("OriginatorConversationID").asText(),
                    "responseCode", responseBody.get("ResponseCode").asText()
            );

        } catch (Exception e) {
            log.error("Error fetching account balance", e);
            throw new PaymentException("Failed to fetch balance: " + e.getMessage());
        }
    }

    public Map<String, String> reverseTransaction(String transactionId, String amount, String remarks) {
        log.info("Reversing M-Pesa transaction: {}", transactionId);

        try {
            String token = getAccessToken();

            Map<String, Object> reversalRequest = new HashMap<>();
            reversalRequest.put("Initiator", mpesaConfig.getInitiatorName());
            reversalRequest.put("SecurityCredential", mpesaConfig.getSecurityCredential());
            reversalRequest.put("CommandID", "TransactionReversal");
            reversalRequest.put("TransactionID", transactionId);
            reversalRequest.put("Amount", amount);
            reversalRequest.put("ReceiverParty", mpesaConfig.getShortCode());
            reversalRequest.put("RecieverIdentifierType", "11");
            reversalRequest.put("ResultURL", mpesaConfig.getResultUrl());
            reversalRequest.put("QueueTimeOutURL", mpesaConfig.getTimeoutUrl());
            reversalRequest.put("Remarks", remarks != null ? remarks : "Transaction reversal");
            reversalRequest.put("Occasion", "");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(reversalRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    mpesaConfig.getReversalUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return Map.of("status", "success", "response", response.getBody());

        } catch (Exception e) {
            log.error("Error reversing transaction", e);
            throw new PaymentException("Failed to reverse transaction: " + e.getMessage());
        }
    }

    private String getAccessToken() {
        if (accessToken != null && tokenExpiry != null && Instant.now().isBefore(tokenExpiry)) {
            return accessToken;
        }

        try {
            String credentials = mpesaConfig.getConsumerKey() + ":" + mpesaConfig.getConsumerSecret();
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + encodedCredentials);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    mpesaConfig.getOauthUrl(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode responseBody = objectMapper.readTree(response.getBody());
            accessToken = responseBody.get("access_token").asText();
            int expiresIn = responseBody.get("expires_in").asInt();
            tokenExpiry = Instant.now().plusSeconds(expiresIn - 60);

            log.info("M-Pesa access token obtained");
            return accessToken;

        } catch (Exception e) {
            log.error("Error getting access token", e);
            throw new PaymentException("Failed to get access token: " + e.getMessage());
        }
    }

    private String generatePassword() {
        String timestamp = getTimestamp();
        String data = mpesaConfig.getShortCode() + mpesaConfig.getPassKey() + timestamp;
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    private String getTimestamp() {
        return java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(java.time.ZoneId.of("Africa/Nairobi"))
                .format(Instant.now());
    }

    private String formatPhoneNumber(String phone) {
        phone = phone.trim().replaceAll("[^0-9]", "");
        if (phone.startsWith("0")) {
            return "254" + phone.substring(1);
        }
        if (!phone.startsWith("254")) {
            return "254" + phone;
        }
        return phone;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }
}
