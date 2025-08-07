package com.financemanagement.web;

import com.financemanagement.domain.AccountStatus;
import com.financemanagement.domain.AccountType;
import com.financemanagement.domain.Currency;
import com.financemanagement.dto.AccountDTO;
import com.financemanagement.service.AccountService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Management", description = "APIs for managing financial accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new account", description = "Creates a new financial account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public CompletableFuture<ResponseEntity<String>> createAccount(
            @Valid @RequestBody AccountDTO accountDTO) {
        log.info("Creating account: {}", accountDTO.getName());
        return accountService.createAccount(accountDTO)
                .thenApply(accountId -> ResponseEntity.status(HttpStatus.CREATED).body(accountId));
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account by ID", description = "Retrieves a specific account by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountDTO> getAccountById(
            @Parameter(description = "Account ID") @PathVariable String accountId) {
        log.debug("Fetching account by ID: {}", accountId);
        Optional<AccountDTO> account = accountService.getAccountById(accountId);
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{accountId}/status/{status}")
    @Operation(summary = "Get account by ID and status", description = "Retrieves a specific account by its ID and status")
    public ResponseEntity<AccountDTO> getAccountByIdAndStatus(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Account status") @PathVariable AccountStatus status) {
        log.debug("Fetching account by ID: {} and status: {}", accountId, status);
        Optional<AccountDTO> account = accountService.getAccountByIdAndStatus(accountId, status);
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get active accounts", description = "Retrieves all active accounts with pagination")
    public ResponseEntity<Page<AccountDTO>> getActiveAccounts(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.debug("Fetching active accounts with pagination");
        Page<AccountDTO> accounts = accountService.getActiveAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all active accounts", description = "Retrieves all active accounts")
    public ResponseEntity<List<AccountDTO>> getAllActiveAccounts() {
        log.debug("Fetching all active accounts");
        List<AccountDTO> accounts = accountService.getAllActiveAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get accounts by type", description = "Retrieves accounts by account type")
    public ResponseEntity<List<AccountDTO>> getAccountsByType(
            @Parameter(description = "Account type") @PathVariable AccountType type) {
        log.debug("Fetching accounts by type: {}", type);
        List<AccountDTO> accounts = accountService.getAccountsByType(type);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/type/{type}/status/{status}")
    @Operation(summary = "Get accounts by type and status", description = "Retrieves accounts by account type and status")
    public ResponseEntity<List<AccountDTO>> getAccountsByTypeAndStatus(
            @Parameter(description = "Account type") @PathVariable AccountType type,
            @Parameter(description = "Account status") @PathVariable AccountStatus status) {
        log.debug("Fetching accounts by type: {} and status: {}", type, status);
        List<AccountDTO> accounts = accountService.getAccountsByTypeAndStatus(type, status);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/currency/{currency}")
    @Operation(summary = "Get accounts by currency", description = "Retrieves accounts by currency")
    public ResponseEntity<List<AccountDTO>> getAccountsByCurrency(
            @Parameter(description = "Currency") @PathVariable Currency currency) {
        log.debug("Fetching accounts by currency: {}", currency);
        List<AccountDTO> accounts = accountService.getAccountsByCurrency(currency);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/institution/{institution}")
    @Operation(summary = "Get accounts by institution", description = "Retrieves accounts by institution")
    public ResponseEntity<List<AccountDTO>> getAccountsByInstitution(
            @Parameter(description = "Institution") @PathVariable String institution) {
        log.debug("Fetching accounts by institution: {}", institution);
        List<AccountDTO> accounts = accountService.getAccountsByInstitution(institution);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/search")
    @Operation(summary = "Search accounts", description = "Searches accounts by various criteria")
    public ResponseEntity<List<AccountDTO>> searchAccounts(
            @Parameter(description = "Search term") @RequestParam String searchTerm) {
        log.debug("Searching accounts with term: {}", searchTerm);
        List<AccountDTO> accounts = accountService.searchAccounts(searchTerm);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/main")
    @Operation(summary = "Get main account", description = "Retrieves the main account")
    public ResponseEntity<AccountDTO> getMainAccount() {
        log.debug("Fetching main account");
        Optional<AccountDTO> account = accountService.getMainAccount();
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/special-check")
    @Operation(summary = "Get special check account", description = "Retrieves the special check account")
    public ResponseEntity<AccountDTO> getSpecialCheckAccount() {
        log.debug("Fetching special check account");
        Optional<AccountDTO> account = accountService.getSpecialCheckAccount();
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/credit-cards")
    @Operation(summary = "Get active credit cards", description = "Retrieves all active credit card accounts")
    public ResponseEntity<List<AccountDTO>> getActiveCreditCards() {
        log.debug("Fetching active credit cards");
        List<AccountDTO> accounts = accountService.getActiveCreditCards();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/low-balance")
    @Operation(summary = "Get accounts with low balance", description = "Retrieves accounts with balance below threshold")
    public ResponseEntity<List<AccountDTO>> getAccountsWithLowBalance(
            @Parameter(description = "Balance threshold") @RequestParam BigDecimal threshold) {
        log.debug("Fetching accounts with balance below: {}", threshold);
        List<AccountDTO> accounts = accountService.getAccountsWithLowBalance(threshold);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/high-balance")
    @Operation(summary = "Get accounts with high balance", description = "Retrieves accounts with balance above threshold")
    public ResponseEntity<List<AccountDTO>> getAccountsWithHighBalance(
            @Parameter(description = "Balance threshold") @RequestParam BigDecimal threshold) {
        log.debug("Fetching accounts with balance above: {}", threshold);
        List<AccountDTO> accounts = accountService.getAccountsWithHighBalance(threshold);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{accountId}/balance")
    @Operation(summary = "Update account balance", description = "Updates the balance of a specific account")
    public ResponseEntity<Void> updateAccountBalance(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "New balance") @RequestParam BigDecimal newBalance) {
        log.info("Updating account balance: {} to {}", accountId, newBalance);
        accountService.updateAccountBalance(accountId, newBalance);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{accountId}/add-balance")
    @Operation(summary = "Add to account balance", description = "Adds an amount to the account balance")
    public ResponseEntity<Void> addToAccountBalance(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Amount to add") @RequestParam BigDecimal amount) {
        log.info("Adding {} to account balance: {}", amount, accountId);
        accountService.addToAccountBalance(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{accountId}/subtract-balance")
    @Operation(summary = "Subtract from account balance", description = "Subtracts an amount from the account balance")
    public ResponseEntity<Void> subtractFromAccountBalance(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Parameter(description = "Amount to subtract") @RequestParam BigDecimal amount) {
        log.info("Subtracting {} from account balance: {}", amount, accountId);
        accountService.subtractFromAccountBalance(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}")
    @Operation(summary = "Update account", description = "Updates an existing account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> updateAccount(
            @Parameter(description = "Account ID") @PathVariable String accountId,
            @Valid @RequestBody AccountDTO accountDTO) {
        log.info("Updating account: {}", accountId);
        accountService.updateAccount(accountId, accountDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{accountId}")
    @Operation(summary = "Delete account", description = "Deletes an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "Account ID") @PathVariable String accountId) {
        log.info("Deleting account: {}", accountId);
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get account statistics", description = "Retrieves account statistics")
    public ResponseEntity<AccountService.AccountStatistics> getAccountStatistics() {
        log.debug("Getting account statistics");
        AccountService.AccountStatistics statistics = accountService.getAccountStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/total-balance")
    @Operation(summary = "Get total balance", description = "Retrieves total balance across all active accounts")
    public ResponseEntity<BigDecimal> getTotalBalance() {
        log.debug("Getting total balance across all active accounts");
        BigDecimal totalBalance = accountService.getTotalBalance();
        return ResponseEntity.ok(totalBalance);
    }

    @GetMapping("/balance-by-type")
    @Operation(summary = "Get balance by account type", description = "Retrieves balance grouped by account type")
    public ResponseEntity<List<Object[]>> getBalanceByType() {
        log.debug("Getting balance by account type");
        List<Object[]> balanceByType = accountService.getBalanceByType();
        return ResponseEntity.ok(balanceByType);
    }

    @GetMapping("/balance-by-currency")
    @Operation(summary = "Get balance by currency", description = "Retrieves balance grouped by currency")
    public ResponseEntity<List<Object[]>> getBalanceByCurrency() {
        log.debug("Getting balance by currency");
        List<Object[]> balanceByCurrency = accountService.getBalanceByCurrency();
        return ResponseEntity.ok(balanceByCurrency);
    }
} 