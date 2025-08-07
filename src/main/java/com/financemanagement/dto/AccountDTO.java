package com.financemanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {

    private String id;

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Account type is required")
    private String accountType;

    @NotNull(message = "Current balance is required")
    @DecimalMin(value = "-999999.99", message = "Balance must be greater than -1,000,000")
    @DecimalMax(value = "999999.99", message = "Balance must be less than 1,000,000")
    @Digits(integer = 6, fraction = 2, message = "Balance must have at most 6 digits before decimal and 2 after")
    private BigDecimal currentBalance;

    @NotNull(message = "Currency is required")
    private String currency;

    private String accountNumber;

    private String institution;

    private String status;

    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Computed fields
    private String accountTypeDisplay;
    private String currencyDisplay;
    private String statusDisplay;
    private BigDecimal absoluteBalance;
    private boolean isActive;
    private boolean isMainAccount;
    private boolean isSpecialCheckAccount;
    private boolean isCreditCard;
} 