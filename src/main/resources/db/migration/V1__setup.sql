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
    source_account_id UUID NOT NULL,
    destination_account_id UUID NOT NULL,
    type VARCHAR(255) NOT NULL,
    balance_id UUID NOT NULL,
    FOREIGN KEY (source_account_id) REFERENCES wallets (id) ON DELETE CASCADE,
    FOREIGN KEY (destination_account_id) REFERENCES wallets (id) ON DELETE CASCADE,
    FOREIGN KEY (balance_id) REFERENCES balances (id) ON DELETE CASCADE
);