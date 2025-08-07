package com.financemanagement.mapper;

import com.financemanagement.domain.Transaction;
import com.financemanagement.domain.TransactionCategory;
import com.financemanagement.domain.TransactionStatus;
import com.financemanagement.domain.TransactionType;
import com.financemanagement.dto.TransactionDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "transactionType", source = "type")
    @Mapping(target = "transactionTypeDisplay", expression = "java(transaction.getType().getDisplayName())")
    @Mapping(target = "categoryDisplay", expression = "java(transaction.getCategory().getDisplayName())")
    @Mapping(target = "statusDisplay", expression = "java(transaction.getStatus().getDisplayName())")
    @Mapping(target = "absoluteAmount", expression = "java(transaction.getAbsoluteAmount())")
    @Mapping(target = "isIncome", expression = "java(transaction.isIncome())")
    @Mapping(target = "isExpense", expression = "java(transaction.isExpense())")
    @Mapping(target = "isRecurring", expression = "java(transaction.isRecurring())")
    TransactionDTO toDTO(Transaction transaction);

    @Mapping(target = "type", expression = "java(TransactionType.valueOf(dto.getTransactionType()))")
    @Mapping(target = "category", expression = "java(TransactionCategory.valueOf(dto.getCategory()))")
    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? TransactionStatus.valueOf(dto.getStatus()) : TransactionStatus.PENDING)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Transaction toEntity(TransactionDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "type", expression = "java(dto.getTransactionType() != null ? TransactionType.valueOf(dto.getTransactionType()) : transaction.getType())")
    @Mapping(target = "category", expression = "java(dto.getCategory() != null ? TransactionCategory.valueOf(dto.getCategory()) : transaction.getCategory())")
    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? TransactionStatus.valueOf(dto.getStatus()) : transaction.getStatus())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDTO(TransactionDTO dto, @MappingTarget Transaction transaction);
} 