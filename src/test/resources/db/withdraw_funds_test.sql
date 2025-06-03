INSERT INTO wallets (id, user_id) VALUES
  ('61111111-1111-1111-1111-111111111111', 'ffffffff-ffff-ffff-ffff-ffffffffffff');

INSERT INTO balances (id, amount, wallet_id, created_at, updated_at, version) VALUES
  ('abababab-abab-abab-abab-abababababab', 50.00, '61111111-1111-1111-1111-111111111111', NOW(), NOW(), 0);