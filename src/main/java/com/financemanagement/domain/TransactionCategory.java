package com.financemanagement.domain;

public enum TransactionCategory {
    // Income Categories
    SALARY("Salary", false),
    CHILD_BENEFIT("Child Benefit", false),
    DEPOSIT_SPECIAL_CHECK("Deposito Cheque Especial", false),
    OTHER_INCOME("Other Income", false),

    // Housing Expenses
    MORTGAGE("Mortgage", true),
    HOME_INSURANCE("Seguro Casa", true),
    UTILITIES("Utilities", true),
    TIPP("TIPP", true),

    // Transportation
    CAR_PAYMENT("Carro", true),
    GASOLINE("Gasolina", true),

    // Credit Cards
    VISA_AEROPLAN("Cartao Visa Aeroplan", false),
    VISA("Cartao Visa", false),
    CANADIAN_TIRE("Cartao Canadian Tire", false),
    COSTCO("Cartao Costco", false),
    HOME_DEPOT("Cartao Home Depot", false),
    NAIARA("Cartao Naiara", false),

    // Family & Personal
    CHILD_CARE("Child Care", true),
    GYM("Academia", true),
    ENTERTAINMENT("Diversao", false),

    // Technology & Services
    CELLULAR_INTERNET("Celular + Internet", true),
    AMAZON_NETFLIX("Amazon + Netflix", true),

    // Groceries & Shopping
    GROCERIES("Feira", false),
    FURNITURE("Sofa", false),

    // Financial
    SPECIAL_CHECK_INTEREST("Juros Cheque especial", true),

    // Other
    OTHER("Other", false);

    private final String displayName;
    private final boolean recurring;

    TransactionCategory(String displayName, boolean recurring) {
        this.displayName = displayName;
        this.recurring = recurring;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public boolean isIncome() {
        return this == SALARY || this == CHILD_BENEFIT || this == DEPOSIT_SPECIAL_CHECK || this == OTHER_INCOME;
    }

    public boolean isExpense() {
        return !isIncome();
    }

    public boolean isCreditCard() {
        return this == VISA_AEROPLAN || this == VISA || this == CANADIAN_TIRE || 
               this == COSTCO || this == HOME_DEPOT || this == NAIARA;
    }

    public boolean isHousing() {
        return this == MORTGAGE || this == HOME_INSURANCE || this == UTILITIES || this == TIPP;
    }

    public boolean isTransportation() {
        return this == CAR_PAYMENT || this == GASOLINE;
    }
} 