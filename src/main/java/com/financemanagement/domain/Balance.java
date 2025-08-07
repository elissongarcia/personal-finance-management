package com.financemanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "balances")
@Aggregate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Balance {

    @Id
    @AggregateIdentifier
    private String id;

    @NotNull(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Balance date is required")
    private LocalDate balanceDate;

    @NotNull(message = "Opening balance is required")
    @DecimalMin(value = "-999999.99", message = "Balance must be greater than -1,000,000")
    @DecimalMax(value = "999999.99", message = "Balance must be less than 1,000,000")
    @Digits(integer = 6, fraction = 2, message = "Balance must have at most 6 digits before decimal and 2 after")
    private BigDecimal openingBalance;

    @NotNull(message = "Closing balance is required")
    @DecimalMin(value = "-999999.99", message = "Balance must be greater than -1,000,000")
    @DecimalMax(value = "999999.99", message = "Balance must be less than 1,000,000")
    @Digits(integer = 6, fraction = 2, message = "Balance must have at most 6 digits before decimal and 2 after")
    private BigDecimal closingBalance;

    @NotNull(message = "Total income is required")
    @DecimalMin(value = "0.00", message = "Total income cannot be negative")
    @Digits(integer = 6, fraction = 2, message = "Amount must have at most 6 digits before decimal and 2 after")
    private BigDecimal totalIncome;

    @NotNull(message = "Total expenses is required")
    @DecimalMin(value = "0.00", message = "Total expenses cannot be negative")
    @Digits(integer = 6, fraction = 2, message = "Amount must have at most 6 digits before decimal and 2 after")
    private BigDecimal totalExpenses;

    @NotNull(message = "Currency is required")
    @Enumerated(EnumType.STRING)
    private Currency currency;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public BigDecimal getNetChange() {
        return closingBalance.subtract(openingBalance);
    }

    public BigDecimal getNetIncome() {
        return totalIncome.subtract(totalExpenses);
    }

    public boolean isPositive() {
        return closingBalance.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isNegative() {
        return closingBalance.compareTo(BigDecimal.ZERO) < 0;
    }

    public BigDecimal getAbsoluteBalance() {
        return closingBalance.abs();
    }

    public void updateClosingBalance(BigDecimal newClosingBalance) {
        this.closingBalance = newClosingBalance;
        this.updatedAt = LocalDateTime.now();
    }

    public void addIncome(BigDecimal income) {
        this.totalIncome = this.totalIncome.add(income);
        this.closingBalance = this.closingBalance.add(income);
        this.updatedAt = LocalDateTime.now();
    }

    public void addExpense(BigDecimal expense) {
        this.totalExpenses = this.totalExpenses.add(expense);
        this.closingBalance = this.closingBalance.subtract(expense);
        this.updatedAt = LocalDateTime.now();
    }
} 