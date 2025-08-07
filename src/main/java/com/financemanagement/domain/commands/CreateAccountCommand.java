package com.financemanagement.domain.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountCommand {

    @TargetAggregateIdentifier
    private String accountId;

    @NotBlank(message = "Account name is required")
    private String name;

    @NotNull(message = "Account type is required")
    private String accountType;

    @NotNull(message = "Initial balance is required")
    private BigDecimal initialBalance;

    @NotNull(message = "Currency is required")
    private String currency;

    private String accountNumber;

    private String institution;

    private String notes;
} 