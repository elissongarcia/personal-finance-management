package com.financemanagement.domain;

import com.financemanagement.domain.commands.CreateAccountCommand;
import com.financemanagement.domain.events.AccountCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Aggregate
@NoArgsConstructor
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private String name;
    private AccountType type;
    private BigDecimal currentBalance;
    private Currency currency;
    private String accountNumber;
    private String institution;
    private AccountStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @CommandHandler
    public AccountAggregate(CreateAccountCommand command) {
        // Validate command
        if (command.getInitialBalance() == null) {
            throw new IllegalArgumentException("Initial balance is required");
        }

        // Publish event
        AggregateLifecycle.apply(AccountCreatedEvent.builder()
                .accountId(command.getAccountId())
                .name(command.getName())
                .accountType(command.getAccountType())
                .initialBalance(command.getInitialBalance())
                .currency(command.getCurrency())
                .accountNumber(command.getAccountNumber())
                .institution(command.getInstitution())
                .status(AccountStatus.ACTIVE.name())
                .notes(command.getNotes())
                .createdAt(LocalDateTime.now())
                .build());
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.name = event.getName();
        this.type = AccountType.valueOf(event.getAccountType());
        this.currentBalance = event.getInitialBalance();
        this.currency = Currency.valueOf(event.getCurrency());
        this.accountNumber = event.getAccountNumber();
        this.institution = event.getInstitution();
        this.status = AccountStatus.valueOf(event.getStatus());
        this.notes = event.getNotes();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getCreatedAt();
    }
} 