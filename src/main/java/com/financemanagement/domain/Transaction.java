package com.financemanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Aggregate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Transaction {

    @Id
    @AggregateIdentifier
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
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    private TransactionCategory category;

    @NotNull(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date cannot be in the future")
    private LocalDate transactionDate;

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

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
            status = TransactionStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    public boolean isRecurring() {
        return category.isRecurring();
    }

    public BigDecimal getAbsoluteAmount() {
        return amount.abs();
    }

    public void markAsCompleted() {
        this.status = TransactionStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCancelled() {
        this.status = TransactionStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAmount(BigDecimal newAmount) {
        this.amount = newAmount;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = LocalDateTime.now();
    }
} 