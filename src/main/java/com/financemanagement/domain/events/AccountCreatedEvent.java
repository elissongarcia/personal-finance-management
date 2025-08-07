package com.financemanagement.domain.events;

import lombok.*;
import org.axonframework.serialization.Revision;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Revision("1.0")
public class AccountCreatedEvent {

    private String accountId;
    private String name;
    private String accountType;
    private BigDecimal initialBalance;
    private String currency;
    private String accountNumber;
    private String institution;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
} 