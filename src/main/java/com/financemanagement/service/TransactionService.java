package com.financemanagement.service;

import com.financemanagement.domain.Transaction;
import com.financemanagement.domain.TransactionCategory;
import com.financemanagement.domain.TransactionStatus;
import com.financemanagement.domain.TransactionType;
import com.financemanagement.domain.commands.CreateTransactionCommand;
import com.financemanagement.domain.commands.UpdateTransactionCommand;
import com.financemanagement.dto.TransactionDTO;
import com.financemanagement.mapper.TransactionMapper;
import com.financemanagement.repository.TransactionRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.Data;
import lombok.Builder;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CommandGateway commandGateway;
    private final AccountService accountService;

    /**
     * Create a new transaction
     */
    public CompletableFuture<String> createTransaction(TransactionDTO transactionDTO) {
        log.info("Creating transaction: {}", transactionDTO.getDescription());
        
        String transactionId = UUID.randomUUID().toString();
        
        CreateTransactionCommand command = CreateTransactionCommand.builder()
                .transactionId(transactionId)
                .description(transactionDTO.getDescription())
                .amount(transactionDTO.getAmount())
                .transactionType(transactionDTO.getTransactionType())
                .category(transactionDTO.getCategory())
                .accountId(transactionDTO.getAccountId())
                .transactionDate(transactionDTO.getTransactionDate())
                .scheduledDate(transactionDTO.getScheduledDate())
                .notes(transactionDTO.getNotes())
                .build();

        return commandGateway.send(command)
                .thenApply(result -> {
                    log.info("Transaction created successfully with ID: {}", transactionId);
                    return transactionId;
                });
    }

    /**
     * Update an existing transaction
     */
    public CompletableFuture<Void> updateTransaction(String transactionId, TransactionDTO transactionDTO) {
        log.info("Updating transaction: {}", transactionId);
        
        UpdateTransactionCommand command = UpdateTransactionCommand.builder()
                .transactionId(transactionId)
                .description(transactionDTO.getDescription())
                .amount(transactionDTO.getAmount())
                .category(transactionDTO.getCategory())
                .transactionDate(transactionDTO.getTransactionDate())
                .scheduledDate(transactionDTO.getScheduledDate())
                .notes(transactionDTO.getNotes())
                .build();

        return commandGateway.send(command)
                .thenAccept(result -> log.info("Transaction updated successfully: {}", transactionId));
    }

    /**
     * Get transaction by ID
     */
    @Cacheable(value = "transactions", key = "#transactionId")
    public Optional<TransactionDTO> getTransactionById(String transactionId) {
        log.debug("Fetching transaction by ID: {}", transactionId);
        return transactionRepository.findById(transactionId)
                .map(transactionMapper::toDTO);
    }

    /**
     * Get transaction by ID and account ID
     */
    @Cacheable(value = "transactions", key = "#transactionId + '_' + #accountId")
    public Optional<TransactionDTO> getTransactionByIdAndAccountId(String transactionId, String accountId) {
        log.debug("Fetching transaction by ID: {} and account ID: {}", transactionId, accountId);
        return transactionRepository.findByIdAndAccountId(transactionId, accountId)
                .map(transactionMapper::toDTO);
    }

    /**
     * Get all transactions for an account with pagination
     */
    @Cacheable(value = "transactions", key = "#accountId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<TransactionDTO> getTransactionsByAccountId(String accountId, Pageable pageable) {
        log.debug("Fetching transactions for account: {} with pagination", accountId);
        return transactionRepository.findByAccountId(accountId, pageable)
                .map(transactionMapper::toDTO);
    }

    /**
     * Get all transactions for an account
     */
    @Cacheable(value = "transactions", key = "#accountId + '_all'")
    public List<TransactionDTO> getAllTransactionsByAccountId(String accountId) {
        log.debug("Fetching all transactions for account: {}", accountId);
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    /**
     * Get transactions by date range
     */
    public List<TransactionDTO> getTransactionsByDateRange(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching transactions for account: {} between {} and {}", accountId, startDate, endDate);
        return transactionRepository.findByAccountIdAndTransactionDateBetweenOrderByTransactionDateDesc(accountId, startDate, endDate)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    /**
     * Get transactions by category
     */
    public List<TransactionDTO> getTransactionsByCategory(String accountId, TransactionCategory category) {
        log.debug("Fetching transactions for account: {} with category: {}", accountId, category);
        return transactionRepository.findByAccountIdAndCategory(accountId, category)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    /**
     * Get transactions by type (income/expense)
     */
    public List<TransactionDTO> getTransactionsByType(String accountId, TransactionType type) {
        log.debug("Fetching transactions for account: {} with type: {}", accountId, type);
        return transactionRepository.findByAccountIdAndType(accountId, type)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    /**
     * Get transactions by status
     */
    public List<TransactionDTO> getTransactionsByStatus(String accountId, TransactionStatus status) {
        log.debug("Fetching transactions for account: {} with status: {}", accountId, status);
        return transactionRepository.findByAccountIdAndStatus(accountId, status)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    /**
     * Search transactions
     */
    public List<TransactionDTO> searchTransactions(String accountId, String searchTerm) {
        log.debug("Searching transactions for account: {} with term: {}", accountId, searchTerm);
        return transactionRepository.searchTransactions(accountId, searchTerm)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    /**
     * Get monthly summary
     */
    public List<Object[]> getMonthlySummary(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting monthly summary for account: {} between {} and {}", accountId, startDate, endDate);
        return transactionRepository.getMonthlySummary(accountId, startDate, endDate);
    }

    /**
     * Get total income for a period
     */
    public BigDecimal getTotalIncome(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting total income for account: {} between {} and {}", accountId, startDate, endDate);
        BigDecimal total = transactionRepository.sumAmountByAccountIdAndTypeAndDateRange(
                accountId, TransactionType.INCOME, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get total expenses for a period
     */
    public BigDecimal getTotalExpenses(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting total expenses for account: {} between {} and {}", accountId, startDate, endDate);
        BigDecimal total = transactionRepository.sumAmountByAccountIdAndTypeAndDateRange(
                accountId, TransactionType.EXPENSE, startDate, endDate);
        return total != null ? total.abs() : BigDecimal.ZERO;
    }

    /**
     * Get expenses by category for a period
     */
    public List<Object[]> getExpensesByCategory(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting expenses by category for account: {} between {} and {}", accountId, startDate, endDate);
        return transactionRepository.sumAmountByCategoryAndDateRange(accountId, startDate, endDate);
    }

    /**
     * Get recurring transactions
     */
    public List<TransactionDTO> getRecurringTransactions(String accountId) {
        log.debug("Getting recurring transactions for account: {}", accountId);
        return transactionRepository.findRecurringTransactionsByAccountId(accountId)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    /**
     * Get outstanding credit card transactions
     */
    public List<TransactionDTO> getOutstandingCreditCardTransactions(String accountId) {
        log.debug("Getting outstanding credit card transactions for account: {}", accountId);
        return transactionRepository.findOutstandingCreditCardTransactions(accountId, TransactionStatus.PENDING)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    /**
     * Get bi-weekly payments
     */
    public List<TransactionDTO> getBiWeeklyPayments(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting bi-weekly payments for account: {} between {} and {}", accountId, startDate, endDate);
        return transactionRepository.findBiWeeklyPayments(accountId, startDate, endDate)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    /**
     * Mark transaction as completed
     */
    @CacheEvict(value = "transactions", allEntries = true)
    public void markTransactionAsCompleted(String transactionId) {
        log.info("Marking transaction as completed: {}", transactionId);
        transactionRepository.findById(transactionId)
                .ifPresent(transaction -> {
                    transaction.markAsCompleted();
                    transactionRepository.save(transaction);
                });
    }

    /**
     * Mark transaction as cancelled
     */
    @CacheEvict(value = "transactions", allEntries = true)
    public void markTransactionAsCancelled(String transactionId) {
        log.info("Marking transaction as cancelled: {}", transactionId);
        transactionRepository.findById(transactionId)
                .ifPresent(transaction -> {
                    transaction.markAsCancelled();
                    transactionRepository.save(transaction);
                });
    }

    /**
     * Delete transaction
     */
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteTransaction(String transactionId) {
        log.info("Deleting transaction: {}", transactionId);
        transactionRepository.deleteById(transactionId);
    }

    /**
     * Get transaction statistics
     */
    public TransactionStatistics getTransactionStatistics(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting transaction statistics for account: {} between {} and {}", accountId, startDate, endDate);
        
        BigDecimal totalIncome = getTotalIncome(accountId, startDate, endDate);
        BigDecimal totalExpenses = getTotalExpenses(accountId, startDate, endDate);
        long totalTransactions = transactionRepository.countByAccountIdAndStatus(accountId, TransactionStatus.COMPLETED);
        
        return TransactionStatistics.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netIncome(totalIncome.subtract(totalExpenses))
                .totalTransactions(totalTransactions)
                .build();
    }

    @Data
    @Builder
    public static class TransactionStatistics {
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal netIncome;
        private long totalTransactions;
    }
} 