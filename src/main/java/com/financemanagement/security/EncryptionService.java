package com.financemanagement.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@Slf4j
public class EncryptionService {

    @Value("${security.encryption.key:your-256-bit-encryption-key-here}")
    private String encryptionKey;

    @Value("${security.encryption.salt:your-encryption-salt-here}")
    private String encryptionSalt;

    private final SecureRandom secureRandom = new SecureRandom();
    private AesBytesEncryptor encryptor;

    public EncryptionService() {
        initializeEncryptor();
    }

    private void initializeEncryptor() {
        try {
            // Generate a secure key from the provided key
            SecretKey key = generateKey(encryptionKey);
            byte[] salt = encryptionSalt.getBytes(StandardCharsets.UTF_8);
            
            this.encryptor = new AesBytesEncryptor(key, salt);
            log.info("Encryption service initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize encryption service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize encryption service", e);
        }
    }

    private SecretKey generateKey(String keyString) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(keyString.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(hash, "AES");
    }

    /**
     * Encrypt sensitive data
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return null;
        }

        try {
            byte[] encryptedBytes = encryptor.encrypt(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Failed to encrypt data: {}", e.getMessage(), e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypt sensitive data
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return null;
        }

        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = encryptor.decrypt(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to decrypt data: {}", e.getMessage(), e);
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Encrypt account number
     */
    public String encryptAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return null;
        }
        
        String sanitized = accountNumber.trim().replaceAll("[^0-9]", "");
        return encrypt(sanitized);
    }

    /**
     * Decrypt account number
     */
    public String decryptAccountNumber(String encryptedAccountNumber) {
        return decrypt(encryptedAccountNumber);
    }

    /**
     * Encrypt notes (for sensitive information)
     */
    public String encryptNotes(String notes) {
        if (notes == null || notes.trim().isEmpty()) {
            return null;
        }
        
        // Only encrypt if notes contain sensitive keywords
        if (containsSensitiveData(notes)) {
            return encrypt(notes);
        }
        
        return notes;
    }

    /**
     * Decrypt notes
     */
    public String decryptNotes(String encryptedNotes) {
        if (encryptedNotes == null) {
            return null;
        }
        
        // Check if notes are encrypted (they will be base64 encoded)
        if (isBase64Encoded(encryptedNotes)) {
            return decrypt(encryptedNotes);
        }
        
        return encryptedNotes;
    }

    /**
     * Generate a secure random token
     */
    public String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Hash sensitive data for comparison (one-way)
     */
    public String hashSensitiveData(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to hash data: {}", e.getMessage(), e);
            throw new RuntimeException("Hashing failed", e);
        }
    }

    /**
     * Check if data contains sensitive information
     */
    private boolean containsSensitiveData(String data) {
        String lowerData = data.toLowerCase();
        return lowerData.contains("password") ||
               lowerData.contains("credit") ||
               lowerData.contains("card") ||
               lowerData.contains("ssn") ||
               lowerData.contains("social") ||
               lowerData.contains("security") ||
               lowerData.contains("pin") ||
               lowerData.contains("cvv");
    }

    /**
     * Check if string is base64 encoded
     */
    private boolean isBase64Encoded(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Mask sensitive data for logging
     */
    public String maskSensitiveData(String data) {
        if (data == null || data.length() < 4) {
            return "***";
        }
        
        return data.substring(0, 2) + "***" + data.substring(data.length() - 2);
    }

    /**
     * Validate encryption key strength
     */
    public void validateEncryptionKey() {
        if (encryptionKey == null || encryptionKey.length() < 32) {
            throw new IllegalArgumentException("Encryption key must be at least 32 characters long");
        }
        
        if (encryptionSalt == null || encryptionSalt.length() < 16) {
            throw new IllegalArgumentException("Encryption salt must be at least 16 characters long");
        }
        
        log.info("Encryption key validation passed");
    }
} 