import java.nio.charset.StandardCharsets;
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
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("1.0");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("1000000.0");

    public void validatePaymentRequest(PaymentRequest request) {
        log.debug("Validating payment request");

        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException("Amount is required and must be greater than 0");
        }

        if (request.getCurrency() == null || request.getCurrency().isEmpty()) {
            throw new PaymentException("Currency is required");
        }

        if (request.getPaymentMethod() == null) {
            throw new PaymentException("Payment method is required");
        }

        // Validate amount range
        validateAmount(request.getAmount().toString(), request.getCurrency());

        // Validate email if provided
        if (request.getCustomerEmail() != null && !request.getCustomerEmail().isEmpty()) {
            validateEmail(request.getCustomerEmail());
        }

        // Validate phone if provided
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            validatePhoneNumber(request.getPhoneNumber());
        }
    }

    private void validateAmount(String amount, String currency) {
        try {
            BigDecimal amt = new BigDecimal(amount);
            if (amt.compareTo(MIN_AMOUNT) < 0) {
                throw new PaymentException("Amount must be at least " + MIN_AMOUNT);
            }
            if (amt.compareTo(MAX_AMOUNT) > 0) {
                throw new PaymentException("Amount cannot exceed " + MAX_AMOUNT);
            }
        } catch (NumberFormatException e) {
            throw new PaymentException("Invalid amount format");
        }
    }

    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new PaymentException("Invalid email format");
        }
    }

    private void validatePhoneNumber(String phone) {
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new PaymentException("Invalid phone number format");
        }
    }
}
