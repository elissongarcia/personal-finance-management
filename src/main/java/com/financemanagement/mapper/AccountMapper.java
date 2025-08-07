package com.financemanagement.mapper;

import com.financemanagement.domain.Account;
import com.financemanagement.domain.AccountStatus;
import com.financemanagement.domain.AccountType;
import com.financemanagement.domain.Currency;
import com.financemanagement.dto.AccountDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "accountType", source = "type")
    @Mapping(target = "accountTypeDisplay", expression = "java(account.getType().getDisplayName())")
    @Mapping(target = "currencyDisplay", expression = "java(account.getCurrency().getDisplayName())")
    @Mapping(target = "statusDisplay", expression = "java(account.getStatus().getDisplayName())")
    @Mapping(target = "absoluteBalance", expression = "java(account.getAbsoluteBalance())")
    @Mapping(target = "isActive", expression = "java(account.isActive())")
    @Mapping(target = "isMainAccount", expression = "java(account.isMainAccount())")
    @Mapping(target = "isSpecialCheckAccount", expression = "java(account.isSpecialCheckAccount())")
    @Mapping(target = "isCreditCard", expression = "java(account.isCreditCard())")
    AccountDTO toDTO(Account account);

    @Mapping(target = "type", expression = "java(AccountType.valueOf(dto.getAccountType()))")
    @Mapping(target = "currency", expression = "java(Currency.valueOf(dto.getCurrency()))")
    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? AccountStatus.valueOf(dto.getStatus()) : AccountStatus.ACTIVE)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Account toEntity(AccountDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "type", expression = "java(dto.getAccountType() != null ? AccountType.valueOf(dto.getAccountType()) : account.getType())")
    @Mapping(target = "currency", expression = "java(dto.getCurrency() != null ? Currency.valueOf(dto.getCurrency()) : account.getCurrency())")
    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? AccountStatus.valueOf(dto.getStatus()) : account.getStatus())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDTO(AccountDTO dto, @MappingTarget Account account);
} 