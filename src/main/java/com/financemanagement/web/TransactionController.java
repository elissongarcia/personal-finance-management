package com.financemanagement.web;

import com.financemanagement.domain.TransactionCategory;
import com.financemanagement.domain.TransactionStatus;
import com.financemanagement.domain.TransactionType;
import com.financemanagement.dto.TransactionDTO;
import com.financemanagement.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Management", description = "APIs for managing financial transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Create a new transaction", description = "Creates a new financial transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public CompletableFuture<ResponseEntity<String>> createTransaction(
            @Valid @RequestBody TransactionDTO transactionDTO) {
        log.info("Creating transaction: {}", transactionDTO.getDescription());
        return transactionService.createTransaction(transactionDTO)
                .thenApply(transactionId -> ResponseEntity.status(HttpStatus.CREATED).body(transactionId));
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction by ID", description = "Retrieves a specific transaction by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction found",
                    content = @Content(schema = @Schema(implementation = TransactionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TransactionDTO> getTransactionById(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        log.debug("Fetching transaction by ID: {}", transactionId);
        Optional<TransactionDTO> transaction = transactionService.getTransactionById(transactionId);
        return transaction.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{transactionId}/account/{accountId}")
    @Operation(summary = "Get transaction by ID and account ID", description = "Retrieves a specific transaction by its ID and account ID")
    public ResponseEntity<TransactionDTO> getTransactionByIdAndAccountId(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId,
            @Parameter(description = "Account ID") @PathVariable String accountId) {
        log.debug("Fetching transaction by ID: {} and account ID: {}", transactionId, accountId);
        Optional<TransactionDTO> transaction = transactionService.getTransactionByIdAndAccountId(transactionId, accountId);
        return transaction.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get transactions by account ID", description = "Retrieves all transactions for a specific account with pagination")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsByAccountId(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.debug("Fetching transactions for account: {} with pagination", accountId);
        Page<TransactionDTO> transactions = transactionService.getTransactionsByAccountId(accountId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/all")
    @Operation(summary = "Get all transactions by account ID", description = "Retrieves all transactions for a specific account")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsByAccountId(
            @Parameter(description = "Account ID") @PathVariable String accountId) {
        log.debug("Fetching all transactions for account: {}", accountId);
        List<TransactionDTO> transactions = transactionService.getAllTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/date-range")
    @Operation(summary = "Get transactions by date range", description = "Retrieves transactions for a specific account within a date range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Fetching transactions for account: {} between {} and {}", accountId, startDate, endDate);
        List<TransactionDTO> transactions = transactionService.getTransactionsByDateRange(accountId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/category/{category}")
    @Operation(summary = "Get transactions by category", description = "Retrieves transactions for a specific account and category")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCategory(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Transaction category") @PathVariable TransactionCategory category) {
        log.debug("Fetching transactions for account: {} with category: {}", accountId, category);
        List<TransactionDTO> transactions = transactionService.getTransactionsByCategory(accountId, category);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/type/{type}")
    @Operation(summary = "Get transactions by type", description = "Retrieves transactions for a specific account and type")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Transaction type") @PathVariable TransactionType type) {
        log.debug("Fetching transactions for account: {} with type: {}", accountId, type);
        List<TransactionDTO> transactions = transactionService.getTransactionsByType(accountId, type);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/status/{status}")
    @Operation(summary = "Get transactions by status", description = "Retrieves transactions for a specific account and status")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByStatus(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Transaction status") @PathVariable TransactionStatus status) {
        log.debug("Fetching transactions for account: {} with status: {}", accountId, status);
        List<TransactionDTO> transactions = transactionService.getTransactionsByStatus(accountId, status);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/search")
    @Operation(summary = "Search transactions", description = "Searches transactions for a specific account")
    public ResponseEntity<List<TransactionDTO>> searchTransactions(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Search term") @RequestParam String searchTerm) {
        log.debug("Searching transactions for account: {} with term: {}", accountId, searchTerm);
        List<TransactionDTO> transactions = transactionService.searchTransactions(accountId, searchTerm);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/monthly-summary")
    @Operation(summary = "Get monthly summary", description = "Retrieves monthly transaction summary for a specific account")
    public ResponseEntity<List<Object[]>> getMonthlySummary(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Getting monthly summary for account: {} between {} and {}", accountId, startDate, endDate);
        List<Object[]> summary = transactionService.getMonthlySummary(accountId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/account/{accountId}/income")
    @Operation(summary = "Get total income", description = "Retrieves total income for a specific account and period")
    public ResponseEntity<BigDecimal> getTotalIncome(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Getting total income for account: {} between {} and {}", accountId, startDate, endDate);
        BigDecimal totalIncome = transactionService.getTotalIncome(accountId, startDate, endDate);
        return ResponseEntity.ok(totalIncome);
    }

    @GetMapping("/account/{accountId}/expenses")
    @Operation(summary = "Get total expenses", description = "Retrieves total expenses for a specific account and period")
    public ResponseEntity<BigDecimal> getTotalExpenses(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Getting total expenses for account: {} between {} and {}", accountId, startDate, endDate);
        BigDecimal totalExpenses = transactionService.getTotalExpenses(accountId, startDate, endDate);
        return ResponseEntity.ok(totalExpenses);
    }

    @GetMapping("/account/{accountId}/expenses-by-category")
    @Operation(summary = "Get expenses by category", description = "Retrieves expenses grouped by category for a specific account and period")
    public ResponseEntity<List<Object[]>> getExpensesByCategory(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Getting expenses by category for account: {} between {} and {}", accountId, startDate, endDate);
        List<Object[]> expenses = transactionService.getExpensesByCategory(accountId, startDate, endDate);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/account/{accountId}/recurring")
    @Operation(summary = "Get recurring transactions", description = "Retrieves recurring transactions for a specific account")
    public ResponseEntity<List<TransactionDTO>> getRecurringTransactions(
            @Parameter(description = "Account ID") @PathVariable String accountId) {
        log.debug("Getting recurring transactions for account: {}", accountId);
        List<TransactionDTO> transactions = transactionService.getRecurringTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/outstanding-credit-cards")
    @Operation(summary = "Get outstanding credit card transactions", description = "Retrieves outstanding credit card transactions for a specific account")
    public ResponseEntity<List<TransactionDTO>> getOutstandingCreditCardTransactions(
            @Parameter(description = "Account ID") @PathVariable String accountId) {
        log.debug("Getting outstanding credit card transactions for account: {}", accountId);
        List<TransactionDTO> transactions = transactionService.getOutstandingCreditCardTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/bi-weekly-payments")
    @Operation(summary = "Get bi-weekly payments", description = "Retrieves bi-weekly payments for a specific account and period")
    public ResponseEntity<List<TransactionDTO>> getBiWeeklyPayments(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Getting bi-weekly payments for account: {} between {} and {}", accountId, startDate, endDate);
        List<TransactionDTO> transactions = transactionService.getBiWeeklyPayments(accountId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{transactionId}")
    @Operation(summary = "Update transaction", description = "Updates an existing transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public CompletableFuture<ResponseEntity<Void>> updateTransaction(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        log.info("Updating transaction: {}", transactionId);
        return transactionService.updateTransaction(transactionId, transactionDTO)
                .thenApply(result -> ResponseEntity.ok().build());
    }

    @PatchMapping("/{transactionId}/complete")
    @Operation(summary = "Mark transaction as completed", description = "Marks a transaction as completed")
    public ResponseEntity<Void> markTransactionAsCompleted(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        log.info("Marking transaction as completed: {}", transactionId);
        transactionService.markTransactionAsCompleted(transactionId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{transactionId}/cancel")
    @Operation(summary = "Mark transaction as cancelled", description = "Marks a transaction as cancelled")
    public ResponseEntity<Void> markTransactionAsCancelled(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        log.info("Marking transaction as cancelled: {}", transactionId);
        transactionService.markTransactionAsCancelled(transactionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{transactionId}")
    @Operation(summary = "Delete transaction", description = "Deletes a transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        log.info("Deleting transaction: {}", transactionId);
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/account/{accountId}/statistics")
    @Operation(summary = "Get transaction statistics", description = "Retrieves transaction statistics for a specific account and period")
    public ResponseEntity<TransactionService.TransactionStatistics> getTransactionStatistics(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Getting transaction statistics for account: {} between {} and {}", accountId, startDate, endDate);
        TransactionService.TransactionStatistics statistics = transactionService.getTransactionStatistics(accountId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
} 