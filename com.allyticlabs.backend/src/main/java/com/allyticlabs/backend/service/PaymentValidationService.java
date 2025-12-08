package com.allyticlabs.backend.service;

import com.allyticlabs.backend.dto.PaymentRequest;
import com.allyticlabs.backend.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentValidationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9,15}$");
    private static final double MIN_AMOUNT = 1.0;
    private static final double MAX_AMOUNT = 1000000.0;

    public void validatePaymentRequest(PaymentRequest request) {
        log.debug("Validating payment request");

        if (request.getAmount() == null) {
            throw new PaymentException("Amount is required");
        }

        if (request.getCurrency() == null || request.getCurrency().isEmpty()) {
            throw new PaymentException("Currency is required");
        }

        if (request.getPaymentMethod() == null) {
            throw new PaymentException("Payment method is required");
        }

        validateAmount(request.getAmount().toString(), request.getCurrency());

        if (request.getCustomerEmail() != null && !request.getCustomerEmail().isEmpty()) {
            validateEmail(request.getCustomerEmail());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            validatePhoneNumber(request.getPhoneNumber());
        }

        if (request.getMerchantId() == null || request.getMerchantId().isEmpty()) {
            throw new PaymentException("Merchant ID is required");
        }

        log.debug("Payment request validation successful");
    }

    public void validateAmount(String amountStr, String currency) {
        try {
            double amount = Double.parseDouble(amountStr);

            if (amount < MIN_AMOUNT) {
                throw new PaymentException(
                        String.format("Amount must be at least %.2f %s", MIN_AMOUNT, currency));
            }

            if (amount > MAX_AMOUNT) {
                throw new PaymentException(
                        String.format("Amount cannot exceed %.2f %s", MAX_AMOUNT, currency));
            }

            String[] parts = amountStr.split("\\.");
            if (parts.length > 1 && parts[1].length() > 2) {
                throw new PaymentException("Amount cannot have more than 2 decimal places");
            }

        } catch (NumberFormatException e) {
            throw new PaymentException("Invalid amount format");
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new PaymentException("Invalid email address format");
        }
    }

    public void validatePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return;
        }

        String digits = phone.replaceAll("[^0-9]", "");

        if (!PHONE_PATTERN.matcher(digits).matches()) {
            throw new PaymentException("Invalid phone number format");
        }
    }

    public void validateCurrency(String currency) {
        if (currency == null || currency.length() != 3) {
            throw new PaymentException("Invalid currency code");
        }

        String[] supportedCurrencies = {"KES", "USD", "EUR", "GBP"};
        boolean isSupported = false;
        for (String supported : supportedCurrencies) {
            if (supported.equalsIgnoreCase(currency)) {
                isSupported = true;
                break;
            }
        }

        if (!isSupported) {
            throw new PaymentException("Currency not supported: " + currency);
        }
    }

    public void validateIdempotencyKey(String key) {
        if (key != null && key.length() > 255) {
            throw new PaymentException("Idempotency key too long (max 255 characters)");
        }
    }
}