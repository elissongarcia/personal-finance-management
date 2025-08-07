package com.financemanagement.domain.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionCommand {

    @TargetAggregateIdentifier
    private String transactionId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private String transactionType;

    @NotNull(message = "Category is required")
    private String category;

    @NotBlank(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    private String notes;
} 