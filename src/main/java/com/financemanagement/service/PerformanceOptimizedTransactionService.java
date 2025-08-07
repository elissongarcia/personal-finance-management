package com.financemanagement.service;

import com.financemanagement.domain.Transaction;
import com.financemanagement.domain.TransactionCategory;
import com.financemanagement.domain.TransactionType;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PerformanceOptimizedTransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CommandGateway commandGateway;
    private final Executor taskExecutor;
    private final Executor reportingExecutor;

    /**
     * Async method to create transaction with caching
     */
    @Async("taskExecutor")
    public CompletableFuture<TransactionDTO> createTransactionAsync(TransactionDTO transactionDTO) {
        log.info("Creating transaction asynchronously: {}", transactionDTO.getDescription());
        
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        TransactionDTO created = createTransaction(transactionDTO);
        return CompletableFuture.completedFuture(created);
    }

    /**
     * Cached method for getting transaction by ID
     */
    @Cacheable(value = "transactions", key = "#transactionId")
    public TransactionDTO getTransactionById(String transactionId) {
        log.debug("Fetching transaction from database: {}", transactionId);
        return transactionRepository.findById(transactionId)
                .map(transactionMapper::toDTO)
                .orElse(null);
    }

    /**
     * Cached method for getting transactions by account
     */
    @Cacheable(value = "transactions", key = "'account:' + #accountId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<TransactionDTO> getTransactionsByAccountId(String accountId, Pageable pageable) {
        log.debug("Fetching transactions for account: {} with pagination", accountId);
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId, pageable)
                .map(transactionMapper::toDTO);
    }

    /**
     * Async method for generating financial reports
     */
    @Async("reportingExecutor")
    public CompletableFuture<Map<String, Object>> generateMonthlyReportAsync(String accountId, int year, int month) {
        log.info("Generating monthly report asynchronously for account: {} year: {} month: {}", accountId, year, month);
        
        try {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            
            Map<String, Object> report = Map.of(
                "accountId", accountId,
                "period", year + "-" + String.format("%02d", month),
                "totalIncome", getTotalIncomeByDateRange(accountId, startDate, endDate),
                "totalExpenses", getTotalExpensesByDateRange(accountId, startDate, endDate),
                "categoryBreakdown", getCategoryBreakdown(accountId, startDate, endDate),
                "generatedAt", LocalDate.now()
            );
            
            return CompletableFuture.completedFuture(report);
        } catch (Exception e) {
            log.error("Error generating monthly report", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Cached method for getting monthly summary
     */
    @Cacheable(value = "statistics", key = "'monthly:' + #accountId + ':' + #year + ':' + #month")
    public Map<String, Object> getMonthlySummary(String accountId, int year, int month) {
        log.debug("Fetching monthly summary for account: {} year: {} month: {}", accountId, year, month);
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        return Map.of(
            "totalIncome", getTotalIncomeByDateRange(accountId, startDate, endDate),
            "totalExpenses", getTotalExpensesByDateRange(accountId, startDate, endDate),
            "netAmount", getNetAmountByDateRange(accountId, startDate, endDate),
            "transactionCount", getTransactionCountByDateRange(accountId, startDate, endDate)
        );
    }

    /**
     * Async method for bulk transaction processing
     */
    @Async("taskExecutor")
    public CompletableFuture<List<TransactionDTO>> processBulkTransactionsAsync(List<TransactionDTO> transactions) {
        log.info("Processing {} transactions asynchronously", transactions.size());
        
        List<TransactionDTO> processed = transactions.stream()
                .map(this::createTransaction)
                .toList();
        
        return CompletableFuture.completedFuture(processed);
    }

    /**
     * Cached method for getting category breakdown
     */
    @Cacheable(value = "statistics", key = "'category-breakdown:' + #accountId + ':' + #startDate + ':' + #endDate")
    public Map<TransactionCategory, BigDecimal> getCategoryBreakdown(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching category breakdown for account: {} from {} to {}", accountId, startDate, endDate);
        
        return transactionRepository.findByAccountIdAndTransactionDateBetweenOrderByTransactionDateDesc(accountId, startDate, endDate)
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    Transaction::getCategory,
                    java.util.stream.Collectors.reducing(
                        BigDecimal.ZERO,
                        Transaction::getAmount,
                        BigDecimal::add
                    )
                ));
    }

    /**
     * Cache eviction for transaction updates
     */
    @CacheEvict(value = {"transactions", "statistics"}, allEntries = true)
    public TransactionDTO updateTransaction(String transactionId, TransactionDTO transactionDTO) {
        log.info("Updating transaction: {}", transactionId);
        // Implementation for updating transaction
        return transactionDTO;
    }

    /**
     * Optimized method for getting total income
     */
    @Cacheable(value = "statistics", key = "'income:' + #accountId + ':' + #startDate + ':' + #endDate")
    public BigDecimal getTotalIncomeByDateRange(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating total income for account: {} from {} to {}", accountId, startDate, endDate);
        return transactionRepository.sumAmountByAccountIdAndTypeAndDateRange(
            accountId, TransactionType.INCOME, startDate, endDate);
    }

    /**
     * Optimized method for getting total expenses
     */
    @Cacheable(value = "statistics", key = "'expenses:' + #accountId + ':' + #startDate + ':' + #endDate")
    public BigDecimal getTotalExpensesByDateRange(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating total expenses for account: {} from {} to {}", accountId, startDate, endDate);
        return transactionRepository.sumAmountByAccountIdAndTypeAndDateRange(
            accountId, TransactionType.EXPENSE, startDate, endDate);
    }

    /**
     * Optimized method for getting net amount
     */
    public BigDecimal getNetAmountByDateRange(String accountId, LocalDate startDate, LocalDate endDate) {
        BigDecimal income = getTotalIncomeByDateRange(accountId, startDate, endDate);
        BigDecimal expenses = getTotalExpensesByDateRange(accountId, startDate, endDate);
        return income.subtract(expenses);
    }

    /**
     * Optimized method for getting transaction count
     */
    @Cacheable(value = "statistics", key = "'count:' + #accountId + ':' + #startDate + ':' + #endDate")
    public long getTransactionCountByDateRange(String accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Counting transactions for account: {} from {} to {}", accountId, startDate, endDate);
        return transactionRepository.countByAccountIdAndTransactionDateBetween(accountId, startDate, endDate);
    }

    /**
     * Async method for data cleanup and maintenance
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> performDataMaintenanceAsync() {
        log.info("Starting async data maintenance");
        
        try {
            // Simulate maintenance tasks
            Thread.sleep(5000);
            log.info("Data maintenance completed successfully");
            return CompletableFuture.completedFuture(null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        } catch (Exception e) {
            log.error("Error during data maintenance", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // Helper method for creating transaction
    private TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        // Implementation for creating transaction
        // This would typically involve command gateway
        return transactionDTO;
    }
} 