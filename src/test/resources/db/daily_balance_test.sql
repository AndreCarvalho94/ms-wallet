-- Script SQL para testes de saldo consolidado até 02/06/2025

-- 1. Criar Wallets
INSERT INTO wallets (id, user_id) VALUES
  ('21111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
  ('22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

-- 2. Criar Balance da Wallet 1
INSERT INTO balances (id, amount, wallet_id, created_at, updated_at, version) VALUES
  ('b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1', 0.00, '21111111-1111-1111-1111-111111111111', NOW(), NOW(), 0);

-- 3. Transações ATÉ 02/06/2025
INSERT INTO transactions (id, amount, source_wallet_id, destination_wallet_id, type, created_at) VALUES
  ('11111111-1111-1111-1111-111111111111', 100.00, NULL, '21111111-1111-1111-1111-111111111111', 'DEPOSIT', '2025-05-30 10:00:00'),
  ('22222222-2222-2222-2222-222222222222', 20.00, '21111111-1111-1111-1111-111111111111', NULL, 'WITHDRAW', '2025-05-31 15:00:00'),
  ('33333333-3333-3333-3333-333333333333', 50.00, '22222222-2222-2222-2222-222222222222', '21111111-1111-1111-1111-111111111111', 'TRANSFER', '2025-06-02 09:00:00');

-- 4. Transações APÓS 02/06/2025
INSERT INTO transactions (id, amount, source_wallet_id, destination_wallet_id, type, created_at) VALUES
  ('44444444-4444-4444-4444-444444444444', 40.00, '21111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'TRANSFER', '2025-06-03 11:00:00'),
  ('55555555-5555-5555-5555-555555555555', 30.00, '21111111-1111-1111-1111-111111111111', NULL, 'WITHDRAW', '2025-06-04 14:00:00'),
  ('66666666-6666-6666-6666-666666666666', 80.00, NULL, '21111111-1111-1111-1111-111111111111', 'DEPOSIT', '2025-06-05 09:30:00');

-- 5. Atualizar saldo
UPDATE balances
SET amount = 140.00, updated_at = NOW(), version = 1
WHERE id = 'b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1';