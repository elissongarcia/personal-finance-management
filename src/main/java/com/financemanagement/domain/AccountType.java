package com.financemanagement.domain;

public enum AccountType {
    MAIN("Main Account"),
    SPECIAL_CHECK("Special Check Account"),
    CREDIT_CARD("Credit Card"),
    SAVINGS("Savings Account"),
    INVESTMENT("Investment Account"),
    LOAN("Loan Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 