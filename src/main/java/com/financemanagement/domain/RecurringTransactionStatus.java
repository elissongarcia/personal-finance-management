package com.financemanagement.domain;

public enum RecurringTransactionStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    PAUSED("Paused");

    private final String displayName;

    RecurringTransactionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 