package com.financemanagement.security;

import com.financemanagement.dto.AccountDTO;
import com.financemanagement.dto.TransactionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("Security Test Suite")
class SecurityTestSuite {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private InputValidationService inputValidationService;

    @Autowired
    private EncryptionService encryptionService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should reject unauthenticated requests to protected endpoints")
    void shouldRejectUnauthenticatedRequests() throws Exception {
        // Test transaction endpoints
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());

        // Test account endpoints
        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should allow authenticated users to access permitted endpoints")
    void shouldAllowAuthenticatedUsers() throws Exception {
        // Test GET endpoints (should be accessible by USER role)
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should reject users without ADMIN role from restricted operations")
    void shouldRejectNonAdminUsers() throws Exception {
        // Test DELETE operations (require ADMIN role)
        mockMvc.perform(delete("/api/v1/transactions/test-id"))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/accounts/test-id"))
                .andExpect(status().isForbidden());

        // Test statistics endpoints (require ADMIN role)
        mockMvc.perform(get("/api/v1/transactions/account/test-id/statistics"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/accounts/statistics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should allow ADMIN users to access all endpoints")
    void shouldAllowAdminUsers() throws Exception {
        // Test DELETE operations
        mockMvc.perform(delete("/api/v1/transactions/test-id"))
                .andExpect(status().isNotFound()); // Not found is expected since test-id doesn't exist

        mockMvc.perform(delete("/api/v1/accounts/test-id"))
                .andExpect(status().isNotFound());

        // Test statistics endpoints
        mockMvc.perform(get("/api/v1/transactions/account/test-id/statistics"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/accounts/statistics"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should validate input sanitization")
    void shouldValidateInputSanitization() {
        // Test XSS prevention
        String maliciousInput = "<script>alert('xss')</script>Mortgage Payment";
        String sanitized = inputValidationService.sanitizeDescription(maliciousInput);
        
        assert !sanitized.contains("<script>");
        assert !sanitized.contains("alert");
        assert sanitized.contains("Mortgage Payment");

        // Test SQL injection prevention
        String sqlInjection = "'; DROP TABLE transactions; --";
        String sanitizedSql = inputValidationService.sanitizeSearchTerm(sqlInjection);
        
        assert !sanitizedSql.contains("DROP TABLE");
        assert !sanitizedSql.contains("--");
    }

    @Test
    @DisplayName("Should validate amount format")
    void shouldValidateAmountFormat() {
        // Valid amounts
        inputValidationService.validateAmount("123.45");
        inputValidationService.validateAmount("-123.45");
        inputValidationService.validateAmount("0.00");

        // Invalid amounts
        try {
            inputValidationService.validateAmount("abc");
            assert false : "Should throw exception for invalid amount";
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            inputValidationService.validateAmount("123.456");
            assert false : "Should throw exception for too many decimal places";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    @DisplayName("Should validate UUID format")
    void shouldValidateUUIDFormat() {
        // Valid UUID
        inputValidationService.validateUUID("123e4567-e89b-12d3-a456-426614174000");

        // Invalid UUID
        try {
            inputValidationService.validateUUID("invalid-uuid");
            assert false : "Should throw exception for invalid UUID";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    @DisplayName("Should validate date format")
    void shouldValidateDateFormat() {
        // Valid date
        inputValidationService.validateDate("2024-01-15");

        // Invalid date
        try {
            inputValidationService.validateDate("2024/01/15");
            assert false : "Should throw exception for invalid date format";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    @DisplayName("Should validate email format")
    void shouldValidateEmailFormat() {
        // Valid email
        inputValidationService.validateEmail("test@example.com");

        // Invalid email
        try {
            inputValidationService.validateEmail("invalid-email");
            assert false : "Should throw exception for invalid email";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    @DisplayName("Should encrypt and decrypt sensitive data")
    void shouldEncryptAndDecryptData() {
        String originalData = "sensitive-account-number-12345";
        
        // Encrypt
        String encrypted = encryptionService.encrypt(originalData);
        assert encrypted != null;
        assert !encrypted.equals(originalData);
        
        // Decrypt
        String decrypted = encryptionService.decrypt(encrypted);
        assert decrypted.equals(originalData);
    }

    @Test
    @DisplayName("Should handle null and empty values in encryption")
    void shouldHandleNullAndEmptyValues() {
        assert encryptionService.encrypt(null) == null;
        assert encryptionService.encrypt("") == null;
        assert encryptionService.decrypt(null) == null;
        assert encryptionService.decrypt("") == null;
    }

    @Test
    @DisplayName("Should generate secure tokens")
    void shouldGenerateSecureTokens() {
        String token1 = encryptionService.generateSecureToken();
        String token2 = encryptionService.generateSecureToken();
        
        assert token1 != null;
        assert token2 != null;
        assert !token1.equals(token2);
        assert token1.length() > 20;
    }

    @Test
    @DisplayName("Should mask sensitive data for logging")
    void shouldMaskSensitiveData() {
        String masked = encryptionService.maskSensitiveData("1234567890");
        assert masked.equals("12***90");
        
        String shortMasked = encryptionService.maskSensitiveData("123");
        assert shortMasked.equals("***");
    }

    @Test
    @DisplayName("Should validate transaction type")
    void shouldValidateTransactionType() {
        inputValidationService.validateTransactionType("INCOME");
        inputValidationService.validateTransactionType("EXPENSE");
        inputValidationService.validateTransactionType("TRANSFER");

        try {
            inputValidationService.validateTransactionType("INVALID");
            assert false : "Should throw exception for invalid transaction type";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    @DisplayName("Should validate transaction category")
    void shouldValidateTransactionCategory() {
        inputValidationService.validateTransactionCategory("MORTGAGE");
        inputValidationService.validateTransactionCategory("UTILITIES");
        inputValidationService.validateTransactionCategory("SALARY");

        try {
            inputValidationService.validateTransactionCategory("INVALID");
            assert false : "Should throw exception for invalid transaction category";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    @DisplayName("Should validate account type")
    void shouldValidateAccountType() {
        inputValidationService.validateAccountType("MAIN");
        inputValidationService.validateAccountType("CREDIT_CARD");
        inputValidationService.validateAccountType("SAVINGS");

        try {
            inputValidationService.validateAccountType("INVALID");
            assert false : "Should throw exception for invalid account type";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    @DisplayName("Should validate currency")
    void shouldValidateCurrency() {
        inputValidationService.validateCurrency("CAD");
        inputValidationService.validateCurrency("USD");
        inputValidationService.validateCurrency("EUR");

        try {
            inputValidationService.validateCurrency("INVALID");
            assert false : "Should throw exception for invalid currency";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should apply rate limiting")
    void shouldApplyRateLimiting() throws Exception {
        // Make multiple requests quickly to trigger rate limiting
        for (int i = 0; i < 150; i++) {
            mockMvc.perform(get("/api/v1/transactions"));
        }
        
        // The 151st request should be rate limited
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Should include security headers")
    void shouldIncludeSecurityHeaders() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().exists("X-XSS-Protection"));
    }

    @Test
    @DisplayName("Should handle CORS properly")
    void shouldHandleCORS() throws Exception {
        mockMvc.perform(options("/api/v1/transactions")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"));
    }
} 