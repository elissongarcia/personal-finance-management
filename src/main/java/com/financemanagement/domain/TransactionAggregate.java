package com.financemanagement.domain;

import com.financemanagement.domain.commands.CreateTransactionCommand;
import com.financemanagement.domain.commands.UpdateTransactionCommand;
import com.financemanagement.domain.events.TransactionCreatedEvent;
import com.financemanagement.domain.events.TransactionUpdatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Aggregate
@NoArgsConstructor
public class TransactionAggregate {

    @AggregateIdentifier
    private String transactionId;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionCategory category;
    private String accountId;
    private LocalDate transactionDate;
    private LocalDate scheduledDate;
    private TransactionStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @CommandHandler
    public TransactionAggregate(CreateTransactionCommand command) {
        // Validate command
        if (command.getAmount() == null || command.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Transaction amount cannot be zero");
        }

        // Publish event
        AggregateLifecycle.apply(TransactionCreatedEvent.builder()
                .transactionId(command.getTransactionId())
                .description(command.getDescription())
                .amount(command.getAmount())
                .transactionType(command.getTransactionType())
                .category(command.getCategory())
                .accountId(command.getAccountId())
                .transactionDate(command.getTransactionDate())
                .scheduledDate(command.getScheduledDate())
                .status(TransactionStatus.PENDING.name())
                .notes(command.getNotes())
                .createdAt(LocalDateTime.now())
                .build());
    }

    @CommandHandler
    public void handle(UpdateTransactionCommand command) {
        // Validate command
        if (command.getAmount() != null && command.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Transaction amount cannot be zero");
        }

        // Publish event
        AggregateLifecycle.apply(TransactionUpdatedEvent.builder()
                .transactionId(command.getTransactionId())
                .description(command.getDescription())
                .amount(command.getAmount())
                .category(command.getCategory())
                .transactionDate(command.getTransactionDate())
                .scheduledDate(command.getScheduledDate())
                .notes(command.getNotes())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @EventSourcingHandler
    public void on(TransactionCreatedEvent event) {
        this.transactionId = event.getTransactionId();
        this.description = event.getDescription();
        this.amount = event.getAmount();
        this.type = TransactionType.valueOf(event.getTransactionType());
        this.category = TransactionCategory.valueOf(event.getCategory());
        this.accountId = event.getAccountId();
        this.transactionDate = event.getTransactionDate();
        this.scheduledDate = event.getScheduledDate();
        this.status = TransactionStatus.valueOf(event.getStatus());
        this.notes = event.getNotes();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getCreatedAt();
    }

    @EventSourcingHandler
    public void on(TransactionUpdatedEvent event) {
        if (event.getDescription() != null) {
            this.description = event.getDescription();
        }
        if (event.getAmount() != null) {
            this.amount = event.getAmount();
        }
        if (event.getCategory() != null) {
            this.category = TransactionCategory.valueOf(event.getCategory());
        }
        if (event.getTransactionDate() != null) {
            this.transactionDate = event.getTransactionDate();
        }
        if (event.getScheduledDate() != null) {
            this.scheduledDate = event.getScheduledDate();
        }
        if (event.getNotes() != null) {
            this.notes = event.getNotes();
        }
        this.updatedAt = event.getUpdatedAt();
    }
} 