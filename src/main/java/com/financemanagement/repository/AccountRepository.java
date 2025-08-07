package com.financemanagement.repository;

import com.financemanagement.domain.Account;
import com.financemanagement.domain.AccountStatus;
import com.financemanagement.domain.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    // Basic CRUD operations
    Optional<Account> findByIdAndStatus(String id, AccountStatus status);
    
    Page<Account> findByStatus(AccountStatus status, Pageable pageable);
    
    List<Account> findByStatusOrderByNameAsc(AccountStatus status);
    
    // Account type queries
    List<Account> findByType(AccountType type);
    
    List<Account> findByTypeAndStatus(AccountType type, AccountStatus status);
    
    Optional<Account> findByTypeAndStatusAndName(AccountType type, AccountStatus status, String name);
    
    // Balance queries
    List<Account> findByCurrentBalanceGreaterThan(BigDecimal balance);
    
    List<Account> findByCurrentBalanceLessThan(BigDecimal balance);
    
    List<Account> findByCurrentBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);
    
    // Currency queries
    List<Account> findByCurrency(String currency);
    
    List<Account> findByCurrencyAndStatus(String currency, AccountStatus status);
    
    // Institution queries
    List<Account> findByInstitution(String institution);
    
    List<Account> findByInstitutionAndStatus(String institution, AccountStatus status);
    
    // Search functionality
    @Query("SELECT a FROM Account a WHERE " +
           "(LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "(a.accountNumber IS NOT NULL AND LOWER(a.accountNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
           "(a.institution IS NOT NULL AND LOWER(a.institution) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
           "(a.notes IS NOT NULL AND LOWER(a.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Account> searchAccounts(@Param("searchTerm") String searchTerm);
    
    // Balance summary queries
    @Query("SELECT SUM(a.currentBalance) FROM Account a WHERE a.status = :status")
    BigDecimal sumBalanceByStatus(@Param("status") AccountStatus status);
    
    @Query("SELECT a.type, SUM(a.currentBalance) FROM Account a WHERE a.status = :status GROUP BY a.type")
    List<Object[]> sumBalanceByTypeAndStatus(@Param("status") AccountStatus status);
    
    @Query("SELECT a.currency, SUM(a.currentBalance) FROM Account a WHERE a.status = :status GROUP BY a.currency")
    List<Object[]> sumBalanceByCurrencyAndStatus(@Param("status") AccountStatus status);
    
    // Account count queries
    @Query("SELECT COUNT(a) FROM Account a WHERE a.status = :status")
    long countByStatus(@Param("status") AccountStatus status);
    
    @Query("SELECT a.type, COUNT(a) FROM Account a WHERE a.status = :status GROUP BY a.type")
    List<Object[]> countByTypeAndStatus(@Param("status") AccountStatus status);
    
    // Main account queries
    @Query("SELECT a FROM Account a WHERE a.type = 'MAIN' AND a.status = 'ACTIVE'")
    Optional<Account> findMainAccount();
    
    // Special check account queries
    @Query("SELECT a FROM Account a WHERE a.type = 'SPECIAL_CHECK' AND a.status = 'ACTIVE'")
    Optional<Account> findSpecialCheckAccount();
    
    // Credit card queries
    @Query("SELECT a FROM Account a WHERE a.type = 'CREDIT_CARD' AND a.status = 'ACTIVE' ORDER BY a.name")
    List<Account> findActiveCreditCards();
    
    // Account balance alerts
    @Query("SELECT a FROM Account a WHERE a.currentBalance < :threshold AND a.status = 'ACTIVE'")
    List<Account> findAccountsWithLowBalance(@Param("threshold") BigDecimal threshold);
    
    @Query("SELECT a FROM Account a WHERE a.currentBalance > :threshold AND a.status = 'ACTIVE'")
    List<Account> findAccountsWithHighBalance(@Param("threshold") BigDecimal threshold);
} 