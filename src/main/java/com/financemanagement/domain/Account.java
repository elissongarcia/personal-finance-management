package com.financemanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Aggregate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Account {

    @Id
    @AggregateIdentifier
    private String id;

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Account type is required")
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "-999999.99", message = "Balance must be greater than -1,000,000")
    @DecimalMax(value = "999999.99", message = "Balance must be less than 1,000,000")
    @Digits(integer = 6, fraction = 2, message = "Balance must have at most 6 digits before decimal and 2 after")
    private BigDecimal currentBalance;

    @NotNull(message = "Currency is required")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    private String accountNumber;

    private String institution;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = AccountStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateBalance(BigDecimal newBalance) {
        this.currentBalance = newBalance;
        this.updatedAt = LocalDateTime.now();
    }

    public void addToBalance(BigDecimal amount) {
        this.currentBalance = this.currentBalance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void subtractFromBalance(BigDecimal amount) {
        this.currentBalance = this.currentBalance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public boolean isMainAccount() {
        return type == AccountType.MAIN;
    }

    public boolean isSpecialCheckAccount() {
        return type == AccountType.SPECIAL_CHECK;
    }

    public boolean isCreditCard() {
        return type == AccountType.CREDIT_CARD;
    }

    public BigDecimal getAbsoluteBalance() {
        return currentBalance.abs();
    }
} 