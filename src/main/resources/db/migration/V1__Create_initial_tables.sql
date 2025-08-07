-- Initial database schema for Personal Finance Management
-- Optimized for PostgreSQL with proper indexing

-- Create enum types
CREATE TYPE transaction_type AS ENUM ('INCOME', 'EXPENSE', 'TRANSFER');
CREATE TYPE transaction_category AS ENUM (
    'SALARY', 'BONUS', 'INVESTMENT_INCOME', 'OTHER_INCOME',
    'MORTGAGE', 'RENT', 'UTILITIES', 'INTERNET', 'PHONE', 'INSURANCE',
    'CREDIT_CARD_PAYMENT', 'CREDIT_CARD_INTEREST', 'CREDIT_CARD_FEES',
    'GROCERIES', 'DINING', 'TRANSPORTATION', 'FUEL', 'MAINTENANCE',
    'HEALTHCARE', 'ENTERTAINMENT', 'SHOPPING', 'TRAVEL', 'EDUCATION',
    'SUBSCRIPTIONS', 'OTHER_EXPENSE'
);
CREATE TYPE transaction_status AS ENUM ('PENDING', 'COMPLETED', 'CANCELLED', 'FAILED', 'SCHEDULED');
CREATE TYPE account_type AS ENUM ('MAIN', 'SPECIAL_CHECK', 'CREDIT_CARD', 'SAVINGS', 'INVESTMENT', 'LOAN');
CREATE TYPE account_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'CLOSED');
CREATE TYPE currency AS ENUM ('CAD', 'USD', 'EUR', 'GBP');
CREATE TYPE recurrence_type AS ENUM ('DAILY', 'WEEKLY', 'BI_WEEKLY', 'MONTHLY', 'YEARLY');
CREATE TYPE recurring_transaction_status AS ENUM ('ACTIVE', 'INACTIVE', 'PAUSED');

-- Create accounts table with optimized structure
CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    type account_type NOT NULL,
    current_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    currency currency NOT NULL DEFAULT 'CAD',
    account_number VARCHAR(50),
    institution VARCHAR(100),
    status account_status NOT NULL DEFAULT 'ACTIVE',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create transactions table with optimized structure
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    type transaction_type NOT NULL,
    category transaction_category NOT NULL,
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    transaction_date DATE NOT NULL,
    scheduled_date DATE,
    status transaction_status NOT NULL DEFAULT 'COMPLETED',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create recurring transactions table
CREATE TABLE recurring_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    type transaction_type NOT NULL,
    category transaction_category NOT NULL,
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    recurrence_type recurrence_type NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    day_of_month INTEGER CHECK (day_of_month >= 1 AND day_of_month <= 31),
    day_of_week INTEGER CHECK (day_of_week >= 1 AND day_of_week <= 7),
    interval INTEGER DEFAULT 1,
    status recurring_transaction_status NOT NULL DEFAULT 'ACTIVE',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create balances table for historical tracking
CREATE TABLE balances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    balance_date DATE NOT NULL,
    opening_balance DECIMAL(15,2) NOT NULL,
    closing_balance DECIMAL(15,2) NOT NULL,
    total_income DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_expenses DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    currency currency NOT NULL DEFAULT 'CAD',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create indexes for performance optimization
-- Account indexes
CREATE INDEX idx_accounts_status ON accounts(status);
CREATE INDEX idx_accounts_type ON accounts(type);
CREATE INDEX idx_accounts_currency ON accounts(currency);
CREATE INDEX idx_accounts_institution ON accounts(institution);
CREATE INDEX idx_accounts_created_at ON accounts(created_at);

-- Transaction indexes
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_category ON transactions(category);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_scheduled_date ON transactions(scheduled_date);
CREATE INDEX idx_transactions_amount ON transactions(amount);
CREATE INDEX idx_transactions_account_date ON transactions(account_id, transaction_date DESC);
CREATE INDEX idx_transactions_account_type ON transactions(account_id, type);
CREATE INDEX idx_transactions_account_category ON transactions(account_id, category);
CREATE INDEX idx_transactions_date_range ON transactions(transaction_date) WHERE transaction_date >= CURRENT_DATE - INTERVAL '1 year';

-- Recurring transaction indexes
CREATE INDEX idx_recurring_transactions_account_id ON recurring_transactions(account_id);
CREATE INDEX idx_recurring_transactions_status ON recurring_transactions(status);
CREATE INDEX idx_recurring_transactions_type ON recurring_transactions(type);
CREATE INDEX idx_recurring_transactions_start_date ON recurring_transactions(start_date);

-- Balance indexes
CREATE INDEX idx_balances_account_id ON balances(account_id);
CREATE INDEX idx_balances_date ON balances(balance_date);
CREATE INDEX idx_balances_account_date ON balances(account_id, balance_date DESC);

-- Create composite indexes for common query patterns
CREATE INDEX idx_transactions_account_type_date ON transactions(account_id, type, transaction_date DESC);
CREATE INDEX idx_transactions_account_category_date ON transactions(account_id, category, transaction_date DESC);
CREATE INDEX idx_transactions_amount_range ON transactions(amount) WHERE amount > 0;

-- Create partial indexes for better performance
CREATE INDEX idx_transactions_income ON transactions(account_id, amount, transaction_date) WHERE type = 'INCOME';
CREATE INDEX idx_transactions_expense ON transactions(account_id, amount, transaction_date) WHERE type = 'EXPENSE';

-- Create indexes for text search
CREATE INDEX idx_transactions_description_gin ON transactions USING gin(to_tsvector('english', description));
CREATE INDEX idx_accounts_name_gin ON accounts USING gin(to_tsvector('english', name));

-- Create unique constraints
CREATE UNIQUE INDEX idx_accounts_account_number ON accounts(account_number) WHERE account_number IS NOT NULL;
CREATE UNIQUE INDEX idx_balances_account_date ON balances(account_id, balance_date);

-- Create check constraints
ALTER TABLE transactions ADD CONSTRAINT chk_transaction_amount CHECK (amount != 0);
ALTER TABLE accounts ADD CONSTRAINT chk_account_balance CHECK (current_balance >= -999999.99 AND current_balance <= 999999.99);

-- Create updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    NEW.version = OLD.version + 1;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_accounts_updated_at BEFORE UPDATE ON accounts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_transactions_updated_at BEFORE UPDATE ON transactions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_recurring_transactions_updated_at BEFORE UPDATE ON recurring_transactions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_balances_updated_at BEFORE UPDATE ON balances
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data for testing
INSERT INTO accounts (name, type, current_balance, currency, institution, status) VALUES
('Main Checking', 'MAIN', 5000.00, 'CAD', 'Royal Bank of Canada', 'ACTIVE'),
('Credit Card', 'CREDIT_CARD', -1500.00, 'CAD', 'Visa', 'ACTIVE'),
('Savings Account', 'SAVINGS', 10000.00, 'CAD', 'Royal Bank of Canada', 'ACTIVE');

-- Insert sample transactions
INSERT INTO transactions (description, amount, type, category, account_id, transaction_date, status) VALUES
('Salary Payment', 5000.00, 'INCOME', 'SALARY', (SELECT id FROM accounts WHERE name = 'Main Checking'), CURRENT_DATE, 'COMPLETED'),
('Grocery Shopping', -150.00, 'EXPENSE', 'GROCERIES', (SELECT id FROM accounts WHERE name = 'Main Checking'), CURRENT_DATE, 'COMPLETED'),
('Credit Card Payment', -500.00, 'EXPENSE', 'CREDIT_CARD_PAYMENT', (SELECT id FROM accounts WHERE name = 'Credit Card'), CURRENT_DATE, 'COMPLETED'); 