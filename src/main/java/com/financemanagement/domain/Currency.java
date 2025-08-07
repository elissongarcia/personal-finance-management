package com.financemanagement.domain;

public enum Currency {
    CAD("Canadian Dollar", "CAD", "$"),
    USD("US Dollar", "USD", "$"),
    EUR("Euro", "EUR", "€"),
    GBP("British Pound", "GBP", "£");

    private final String displayName;
    private final String code;
    private final String symbol;

    Currency(String displayName, String code, String symbol) {
        this.displayName = displayName;
        this.code = code;
        this.symbol = symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }
} 