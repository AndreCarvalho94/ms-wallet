CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL
);

CREATE TABLE balances (
    id UUID PRIMARY KEY,
    amount DECIMAL(19, 2) NOT NULL,
    wallet_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT NOT NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallets (id) ON DELETE CASCADE
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    amount DECIMAL(19, 2) NOT NULL,
    source_wallet_id UUID,
    destination_wallet_id UUID,
    type VARCHAR(255) NOT NULL,
    balance_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (source_wallet_id) REFERENCES wallets (id) ON DELETE CASCADE,
    FOREIGN KEY (destination_wallet_id) REFERENCES wallets (id) ON DELETE CASCADE,
    FOREIGN KEY (balance_id) REFERENCES balances (id) ON DELETE CASCADE
);

ALTER TABLE transactions
ADD CONSTRAINT chk_transaction_amount_positive CHECK (amount >= 0);

ALTER TABLE balances
ADD CONSTRAINT chk_balance_amount_positive CHECK (amount >= 0);