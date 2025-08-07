package com.financemanagement.service;

import com.financemanagement.domain.Account;
import com.financemanagement.domain.AccountStatus;
import com.financemanagement.domain.AccountType;
import com.financemanagement.domain.Currency;
import com.financemanagement.domain.commands.CreateAccountCommand;
import com.financemanagement.dto.AccountDTO;
import com.financemanagement.mapper.AccountMapper;
import com.financemanagement.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CommandGateway commandGateway;

    /**
     * Create a new account
     */
    public CompletableFuture<String> createAccount(AccountDTO accountDTO) {
        log.info("Creating account: {}", accountDTO.getName());
        
        String accountId = UUID.randomUUID().toString();
        
        CreateAccountCommand command = CreateAccountCommand.builder()
                .accountId(accountId)
                .name(accountDTO.getName())
                .accountType(accountDTO.getAccountType())
                .initialBalance(accountDTO.getCurrentBalance())
                .currency(accountDTO.getCurrency())
                .accountNumber(accountDTO.getAccountNumber())
                .institution(accountDTO.getInstitution())
                .notes(accountDTO.getNotes())
                .build();

        return commandGateway.send(command)
                .thenApply(result -> {
                    log.info("Account created successfully with ID: {}", accountId);
                    return accountId;
                });
    }

    /**
     * Get account by ID
     */
    @Cacheable(value = "accounts", key = "#accountId")
    public Optional<AccountDTO> getAccountById(String accountId) {
        log.debug("Fetching account by ID: {}", accountId);
        return accountRepository.findById(accountId)
                .map(accountMapper::toDTO);
    }

    /**
     * Get account by ID and status
     */
    @Cacheable(value = "accounts", key = "#accountId + '_' + #status")
    public Optional<AccountDTO> getAccountByIdAndStatus(String accountId, AccountStatus status) {
        log.debug("Fetching account by ID: {} and status: {}", accountId, status);
        return accountRepository.findByIdAndStatus(accountId, status)
                .map(accountMapper::toDTO);
    }

    /**
     * Get all active accounts with pagination
     */
    @Cacheable(value = "accounts", key = "'active_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<AccountDTO> getActiveAccounts(Pageable pageable) {
        log.debug("Fetching active accounts with pagination");
        return accountRepository.findByStatus(AccountStatus.ACTIVE, pageable)
                .map(accountMapper::toDTO);
    }

    /**
     * Get all active accounts
     */
    @Cacheable(value = "accounts", key = "'active_all'")
    public List<AccountDTO> getAllActiveAccounts() {
        log.debug("Fetching all active accounts");
        return accountRepository.findByStatusOrderByNameAsc(AccountStatus.ACTIVE)
                .stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    /**
     * Get accounts by type
     */
    public List<AccountDTO> getAccountsByType(AccountType type) {
        log.debug("Fetching accounts by type: {}", type);
        return accountRepository.findByType(type)
                .stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    /**
     * Get accounts by type and status
     */
    public List<AccountDTO> getAccountsByTypeAndStatus(AccountType type, AccountStatus status) {
        log.debug("Fetching accounts by type: {} and status: {}", type, status);
        return accountRepository.findByTypeAndStatus(type, status)
                .stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    /**
     * Get accounts by currency
     */
    public List<AccountDTO> getAccountsByCurrency(Currency currency) {
        log.debug("Fetching accounts by currency: {}", currency);
        return accountRepository.findByCurrency(currency.name())
                .stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    /**
     * Get accounts by institution
     */
    public List<AccountDTO> getAccountsByInstitution(String institution) {
        log.debug("Fetching accounts by institution: {}", institution);
        return accountRepository.findByInstitution(institution)
                .stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    /**
     * Search accounts
     */
    public List<AccountDTO> searchAccounts(String searchTerm) {
        log.debug("Searching accounts with term: {}", searchTerm);
        return accountRepository.searchAccounts(searchTerm)
                .stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    /**
     * Get main account
     */
    @Cacheable(value = "accounts", key = "'main'")
    public Optional<AccountDTO> getMainAccount() {
        log.debug("Fetching main account");
        return accountRepository.findMainAccount()
                .map(accountMapper::toDTO);
    }

    /**
     * Get special check account
     */
    @Cacheable(value = "accounts", key = "'special_check'")
    public Optional<AccountDTO> getSpecialCheckAccount() {
        log.debug("Fetching special check account");
        return accountRepository.findSpecialCheckAccount()
                .map(accountMapper::toDTO);
    }

    /**
     * Get active credit cards
     */
    @Cacheable(value = "accounts", key = "'credit_cards'")
    public List<AccountDTO> getActiveCreditCards() {
        log.debug("Fetching active credit cards");
        return accountRepository.findActiveCreditCards()
                .stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    /**
     * Get accounts with low balance
     */
    public List<AccountDTO> getAccountsWithLowBalance(BigDecimal threshold) {
        log.debug("Fetching accounts with balance below: {}", threshold);
        return accountRepository.findAccountsWithLowBalance(threshold)
                .stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    /**
     * Get accounts with high balance
     */
    public List<AccountDTO> getAccountsWithHighBalance(BigDecimal threshold) {
        log.debug("Fetching accounts with balance above: {}", threshold);
        return accountRepository.findAccountsWithHighBalance(threshold)
                .stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    /**
     * Update account balance
     */
    @CacheEvict(value = "accounts", allEntries = true)
    public void updateAccountBalance(String accountId, BigDecimal newBalance) {
        log.info("Updating account balance: {} to {}", accountId, newBalance);
        accountRepository.findById(accountId)
                .ifPresent(account -> {
                    account.updateBalance(newBalance);
                    accountRepository.save(account);
                });
    }

    /**
     * Add to account balance
     */
    @CacheEvict(value = "accounts", allEntries = true)
    public void addToAccountBalance(String accountId, BigDecimal amount) {
        log.info("Adding {} to account balance: {}", amount, accountId);
        accountRepository.findById(accountId)
                .ifPresent(account -> {
                    account.addToBalance(amount);
                    accountRepository.save(account);
                });
    }

    /**
     * Subtract from account balance
     */
    @CacheEvict(value = "accounts", allEntries = true)
    public void subtractFromAccountBalance(String accountId, BigDecimal amount) {
        log.info("Subtracting {} from account balance: {}", amount, accountId);
        accountRepository.findById(accountId)
                .ifPresent(account -> {
                    account.subtractFromBalance(amount);
                    accountRepository.save(account);
                });
    }

    /**
     * Update account
     */
    @CacheEvict(value = "accounts", allEntries = true)
    public void updateAccount(String accountId, AccountDTO accountDTO) {
        log.info("Updating account: {}", accountId);
        accountRepository.findById(accountId)
                .ifPresent(account -> {
                    accountMapper.updateEntityFromDTO(accountDTO, account);
                    accountRepository.save(account);
                });
    }

    /**
     * Delete account
     */
    @CacheEvict(value = "accounts", allEntries = true)
    public void deleteAccount(String accountId) {
        log.info("Deleting account: {}", accountId);
        accountRepository.deleteById(accountId);
    }

    /**
     * Get account statistics
     */
    public AccountStatistics getAccountStatistics() {
        log.debug("Getting account statistics");
        
        BigDecimal totalBalance = accountRepository.sumBalanceByStatus(AccountStatus.ACTIVE);
        long totalAccounts = accountRepository.countByStatus(AccountStatus.ACTIVE);
        List<Object[]> balanceByType = accountRepository.sumBalanceByTypeAndStatus(AccountStatus.ACTIVE);
        List<Object[]> balanceByCurrency = accountRepository.sumBalanceByCurrencyAndStatus(AccountStatus.ACTIVE);
        
        return AccountStatistics.builder()
                .totalBalance(totalBalance != null ? totalBalance : BigDecimal.ZERO)
                .totalAccounts(totalAccounts)
                .balanceByType(balanceByType)
                .balanceByCurrency(balanceByCurrency)
                .build();
    }

    /**
     * Get total balance across all active accounts
     */
    public BigDecimal getTotalBalance() {
        log.debug("Getting total balance across all active accounts");
        BigDecimal total = accountRepository.sumBalanceByStatus(AccountStatus.ACTIVE);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get balance by account type
     */
    public List<Object[]> getBalanceByType() {
        log.debug("Getting balance by account type");
        return accountRepository.sumBalanceByTypeAndStatus(AccountStatus.ACTIVE);
    }

    /**
     * Get balance by currency
     */
    public List<Object[]> getBalanceByCurrency() {
        log.debug("Getting balance by currency");
        return accountRepository.sumBalanceByCurrencyAndStatus(AccountStatus.ACTIVE);
    }

    @lombok.Data
    @lombok.Builder
    public static class AccountStatistics {
        private BigDecimal totalBalance;
        private long totalAccounts;
        private List<Object[]> balanceByType;
        private List<Object[]> balanceByCurrency;
    }
} 