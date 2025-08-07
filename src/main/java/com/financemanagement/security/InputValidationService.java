package com.financemanagement.security;

import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class InputValidationService {

    // Patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^-?\\d+(\\.\\d{1,2})?$");
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-_.,()]+$");
    
    // Maximum lengths
    private static final int MAX_DESCRIPTION_LENGTH = 255;
    private static final int MAX_NOTES_LENGTH = 1000;
    private static final int MAX_ACCOUNT_NAME_LENGTH = 100;
    private static final int MAX_INSTITUTION_LENGTH = 100;

    /**
     * Sanitize and validate description
     */
    public String sanitizeDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        
        String sanitized = Encode.forHtml(description.trim());
        
        if (sanitized.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Description contains invalid characters");
        }
        
        log.debug("Sanitized description: {} -> {}", description, sanitized);
        return sanitized;
    }

    /**
     * Sanitize and validate notes
     */
    public String sanitizeNotes(String notes) {
        if (notes == null) {
            return null;
        }
        
        String sanitized = Encode.forHtml(notes.trim());
        
        if (sanitized.length() > MAX_NOTES_LENGTH) {
            throw new IllegalArgumentException("Notes cannot exceed " + MAX_NOTES_LENGTH + " characters");
        }
        
        log.debug("Sanitized notes: {} -> {}", notes, sanitized);
        return sanitized;
    }

    /**
     * Validate and sanitize account name
     */
    public String sanitizeAccountName(String accountName) {
        if (accountName == null || accountName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account name cannot be null or empty");
        }
        
        String sanitized = Encode.forHtml(accountName.trim());
        
        if (sanitized.length() > MAX_ACCOUNT_NAME_LENGTH) {
            throw new IllegalArgumentException("Account name cannot exceed " + MAX_ACCOUNT_NAME_LENGTH + " characters");
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Account name contains invalid characters");
        }
        
        log.debug("Sanitized account name: {} -> {}", accountName, sanitized);
        return sanitized;
    }

    /**
     * Validate and sanitize institution name
     */
    public String sanitizeInstitution(String institution) {
        if (institution == null) {
            return null;
        }
        
        String sanitized = Encode.forHtml(institution.trim());
        
        if (sanitized.length() > MAX_INSTITUTION_LENGTH) {
            throw new IllegalArgumentException("Institution name cannot exceed " + MAX_INSTITUTION_LENGTH + " characters");
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Institution name contains invalid characters");
        }
        
        log.debug("Sanitized institution: {} -> {}", institution, sanitized);
        return sanitized;
    }

    /**
     * Validate amount format
     */
    public void validateAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            throw new IllegalArgumentException("Amount cannot be null or empty");
        }
        
        if (!AMOUNT_PATTERN.matcher(amount.trim()).matches()) {
            throw new IllegalArgumentException("Invalid amount format. Expected format: -123.45 or 123.45");
        }
        
        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue < -999999.99 || amountValue > 999999.99) {
                throw new IllegalArgumentException("Amount must be between -999,999.99 and 999,999.99");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format");
        }
    }

    /**
     * Validate UUID format
     */
    public void validateUUID(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("UUID cannot be null or empty");
        }
        
        if (!UUID_PATTERN.matcher(uuid.trim()).matches()) {
            throw new IllegalArgumentException("Invalid UUID format");
        }
    }

    /**
     * Validate date format (YYYY-MM-DD)
     */
    public void validateDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        
        if (!DATE_PATTERN.matcher(date.trim()).matches()) {
            throw new IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD");
        }
    }

    /**
     * Validate email format
     */
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Sanitize search term to prevent injection
     */
    public String sanitizeSearchTerm(String searchTerm) {
        if (searchTerm == null) {
            return null;
        }
        
        // Remove potentially dangerous characters
        String sanitized = searchTerm.replaceAll("[<>\"'&;]", "");
        
        // Limit length
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }
        
        log.debug("Sanitized search term: {} -> {}", searchTerm, sanitized);
        return sanitized;
    }

    /**
     * Validate transaction type
     */
    public void validateTransactionType(String transactionType) {
        if (transactionType == null || transactionType.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction type cannot be null or empty");
        }
        
        try {
            com.financemanagement.domain.TransactionType.valueOf(transactionType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }
    }

    /**
     * Validate transaction category
     */
    public void validateTransactionCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction category cannot be null or empty");
        }
        
        try {
            com.financemanagement.domain.TransactionCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction category: " + category);
        }
    }

    /**
     * Validate account type
     */
    public void validateAccountType(String accountType) {
        if (accountType == null || accountType.trim().isEmpty()) {
            throw new IllegalArgumentException("Account type cannot be null or empty");
        }
        
        try {
            com.financemanagement.domain.AccountType.valueOf(accountType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account type: " + accountType);
        }
    }

    /**
     * Validate currency
     */
    public void validateCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        
        try {
            com.financemanagement.domain.Currency.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency: " + currency);
        }
    }
} 