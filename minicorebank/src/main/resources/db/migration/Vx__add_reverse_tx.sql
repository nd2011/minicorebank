ALTER TABLE transactions
  ADD COLUMN reversed_of_tx_id BIGINT NULL,
  ADD INDEX idx_tx_reversed_of (reversed_of_tx_id);

-- (khuyến nghị) đảm bảo idempotencyKey unique nếu chưa có
ALTER TABLE transactions
  ADD UNIQUE KEY uk_tx_idempotency (idempotency_key);
