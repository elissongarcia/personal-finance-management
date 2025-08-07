package com.financemanagement.repository;

import com.financemanagement.domain.Transaction;
import com.financemanagement.domain.TransactionCategory;
import com.financemanagement.domain.TransactionStatus;
import com.financemanagement.domain.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Basic CRUD operations
    Optional<Transaction> findByIdAndAccountId(String id, String accountId);
    
    Page<Transaction> findByAccountId(String accountId, Pageable pageable);
    
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(String accountId);
    
    // Date range queries
    List<Transaction> findByAccountIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            String accountId, LocalDate startDate, LocalDate endDate);
    
    List<Transaction> findByAccountIdAndScheduledDateBetweenOrderByScheduledDateDesc(
            String accountId, LocalDate startDate, LocalDate endDate);
    
    // Category and type queries
    List<Transaction> findByAccountIdAndCategory(String accountId, TransactionCategory category);
    
    List<Transaction> findByAccountIdAndType(String accountId, TransactionType type);
    
    List<Transaction> findByAccountIdAndCategoryAndTransactionDateBetween(
            String accountId, TransactionCategory category, LocalDate startDate, LocalDate endDate);
    
    // Status queries
    List<Transaction> findByAccountIdAndStatus(String accountId, TransactionStatus status);
    
    List<Transaction> findByAccountIdAndStatusAndTransactionDateBetween(
            String accountId, TransactionStatus status, LocalDate startDate, LocalDate endDate);
    
    // Amount queries
    List<Transaction> findByAccountIdAndAmountGreaterThan(String accountId, BigDecimal amount);
    
    List<Transaction> findByAccountIdAndAmountLessThan(String accountId, BigDecimal amount);
    
    // Aggregation queries
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.accountId = :accountId AND t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByAccountIdAndTypeAndDateRange(
            @Param("accountId") String accountId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.accountId = :accountId AND t.transactionDate BETWEEN :startDate AND :endDate GROUP BY t.category")
    List<Object[]> sumAmountByCategoryAndDateRange(
            @Param("accountId") String accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.accountId = :accountId AND t.status = :status")
    long countByAccountIdAndStatus(@Param("accountId") String accountId, @Param("status") TransactionStatus status);
    
    // Recurring transaction queries
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.category.isRecurring = true ORDER BY t.scheduledDate DESC")
    List<Transaction> findRecurringTransactionsByAccountId(@Param("accountId") String accountId);
    
    // Outstanding balance queries (for credit cards)
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.category.isCreditCard = true AND t.status = :status ORDER BY t.transactionDate DESC")
    List<Transaction> findOutstandingCreditCardTransactions(
            @Param("accountId") String accountId, 
            @Param("status") TransactionStatus status);
    
    // Monthly summary queries
    @Query("SELECT YEAR(t.transactionDate) as year, MONTH(t.transactionDate) as month, " +
           "SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as totalIncome, " +
           "SUM(CASE WHEN t.type = 'EXPENSE' THEN ABS(t.amount) ELSE 0 END) as totalExpenses " +
           "FROM Transaction t WHERE t.accountId = :accountId AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate) ORDER BY year, month")
    List<Object[]> getMonthlySummary(
            @Param("accountId") String accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // Search functionality
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND " +
           "(LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> searchTransactions(
            @Param("accountId") String accountId, 
            @Param("searchTerm") String searchTerm);
    
    // Bi-weekly payment queries
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.scheduledDate >= :startDate " +
           "AND t.scheduledDate <= :endDate AND t.category.isRecurring = true " +
           "ORDER BY t.scheduledDate")
    List<Transaction> findBiWeeklyPayments(
            @Param("accountId") String accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
} 