-- Criar Wallet
INSERT INTO wallets (id, user_id) VALUES
  ('51111111-1111-1111-1111-111111111111', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee');

-- Criar Balance com valor inicial zero
INSERT INTO balances (id, amount, wallet_id, created_at, updated_at, version) VALUES
  ('fbfbfbfb-fbfb-fbfb-fbfb-fbfbfbfbfbfb', 0.00, '51111111-1111-1111-1111-111111111111', NOW(), NOW(), 0);