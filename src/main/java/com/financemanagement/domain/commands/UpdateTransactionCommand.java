package com.financemanagement.domain.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTransactionCommand {

    @TargetAggregateIdentifier
    private String transactionId;

    private String description;

    private BigDecimal amount;

    private String category;

    private LocalDate transactionDate;

    private LocalDate scheduledDate;

    private String notes;
} 