-- 1. Criar novas Wallets
INSERT INTO wallets (id, user_id) VALUES
  ('31111111-1111-1111-1111-111111111111', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),
  ('42222222-2222-2222-2222-222222222222', 'dddddddd-dddd-dddd-dddd-dddddddddddd');

-- 2. Criar Balances para cada Wallet com valor inicial zero
INSERT INTO balances (id, amount, wallet_id, created_at, updated_at, version) VALUES
  ('cbcbcbcb-cbcb-cbcb-cbcb-cbcbcbcbcbcb', 0.00, '31111111-1111-1111-1111-111111111111', NOW(), NOW(), 0),
  ('dbdbdbdb-dbdb-dbdb-dbdb-dbdbdbdbdbdb', 0.00, '42222222-2222-2222-2222-222222222222', NOW(), NOW(), 0);

-- 3. Transferências iniciais para ajustar os valores dos balances
INSERT INTO transactions (id, amount, source_wallet_id, destination_wallet_id, type, balance_id, created_at) VALUES
  ('31111111-1111-1111-1111-111111111112', 500.00, NULL, '31111111-1111-1111-1111-111111111111', 'DEPOSIT', 'cbcbcbcb-cbcb-cbcb-cbcb-cbcbcbcbcbcb', '2025-06-01 08:00:00'),
  ('31111111-1111-1111-1111-111111111113', 300.00, NULL, '42222222-2222-2222-2222-222222222222', 'DEPOSIT', 'dbdbdbdb-dbdb-dbdb-dbdb-dbdbdbdbdbdb', '2025-06-01 08:30:00');

-- 4. Transferência da Wallet 3 → Wallet 4
INSERT INTO transactions (id, amount, source_wallet_id, destination_wallet_id, type, balance_id, created_at) VALUES
  ('31111111-1111-1111-1111-111111111114', 150.00, '31111111-1111-1111-1111-111111111111', '42222222-2222-2222-2222-222222222222', 'TRANSFER', 'cbcbcbcb-cbcb-cbcb-cbcb-cbcbcbcbcbcb', '2025-06-01 09:00:00');

-- 5. Transferência da Wallet 4 → Wallet 3
INSERT INTO transactions (id, amount, source_wallet_id, destination_wallet_id, type, balance_id, created_at) VALUES
  ('31111111-1111-1111-1111-111111111115', 100.00, '42222222-2222-2222-2222-222222222222', '31111111-1111-1111-1111-111111111111', 'TRANSFER', 'dbdbdbdb-dbdb-dbdb-dbdb-dbdbdbdbdbdb', '2025-06-02 11:30:00');

-- 6. Atualizar balances após transferências
UPDATE balances
SET amount = 450.00, updated_at = NOW(), version = 1
WHERE id = 'cbcbcbcb-cbcb-cbcb-cbcb-cbcbcbcbcbcb';

UPDATE balances
SET amount = 350.00, updated_at = NOW(), version = 1
WHERE id = 'dbdbdbdb-dbdb-dbdb-dbdb-dbdbdbdbdbdb';