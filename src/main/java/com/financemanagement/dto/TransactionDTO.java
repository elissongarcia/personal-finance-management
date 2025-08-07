package com.financemanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {

    private String id;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "-999999.99", message = "Amount must be greater than -1,000,000")
    @DecimalMax(value = "999999.99", message = "Amount must be less than 1,000,000")
    @Digits(integer = 6, fraction = 2, message = "Amount must have at most 6 digits before decimal and 2 after")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private String transactionType;

    @NotNull(message = "Category is required")
    private String category;

    @NotBlank(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Transaction date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    @NotNull(message = "Scheduled date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;

    private String status;

    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Computed fields
    private String transactionTypeDisplay;
    private String categoryDisplay;
    private String statusDisplay;
    private BigDecimal absoluteAmount;
    private boolean isIncome;
    private boolean isExpense;
    private boolean isRecurring;
} 