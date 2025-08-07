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
@Table(name = "recurring_transactions")
@Aggregate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RecurringTransaction {

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

    @NotNull(message = "Recurrence type is required")
    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @Min(value = 1, message = "Day of month must be between 1 and 31")
    @Max(value = 31, message = "Day of month must be between 1 and 31")
    private Integer dayOfMonth;

    @Min(value = 1, message = "Day of week must be between 1 and 7")
    @Max(value = 7, message = "Day of week must be between 1 and 7")
    private Integer dayOfWeek;

    @Min(value = 1, message = "Interval must be at least 1")
    private Integer interval;

    @Enumerated(EnumType.STRING)
    private RecurringTransactionStatus status;

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
            status = RecurringTransactionStatus.ACTIVE;
        }
        if (interval == null) {
            interval = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == RecurringTransactionStatus.ACTIVE;
    }

    public boolean isMonthly() {
        return recurrenceType == RecurrenceType.MONTHLY;
    }

    public boolean isWeekly() {
        return recurrenceType == RecurrenceType.WEEKLY;
    }

    public boolean isBiWeekly() {
        return recurrenceType == RecurrenceType.BI_WEEKLY;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public LocalDate getNextOccurrence() {
        if (!isActive() || (endDate != null && LocalDate.now().isAfter(endDate))) {
            return null;
        }

        LocalDate nextDate = startDate;
        while (nextDate.isBefore(LocalDate.now())) {
            nextDate = calculateNextDate(nextDate);
        }
        return nextDate;
    }

    private LocalDate calculateNextDate(LocalDate currentDate) {
        return switch (recurrenceType) {
            case DAILY -> currentDate.plusDays(interval);
            case WEEKLY -> currentDate.plusWeeks(interval);
            case BI_WEEKLY -> currentDate.plusWeeks(2 * interval);
            case MONTHLY -> currentDate.plusMonths(interval);
            case YEARLY -> currentDate.plusYears(interval);
        };
    }

    public void deactivate() {
        this.status = RecurringTransactionStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = RecurringTransactionStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
} 