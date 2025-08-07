package com.financemanagement.domain.events;

import lombok.*;
import org.axonframework.serialization.Revision;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Revision("1.0")
public class TransactionUpdatedEvent {

    private String transactionId;
    private String description;
    private BigDecimal amount;
    private String category;
    private LocalDate transactionDate;
    private LocalDate scheduledDate;
    private String notes;
    private LocalDateTime updatedAt;
} 