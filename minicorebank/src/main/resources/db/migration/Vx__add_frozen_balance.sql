ALTER TABLE accounts
  ADD COLUMN frozen_snapshot DECIMAL(19,4) NOT NULL DEFAULT 0;

CREATE INDEX idx_accounts_frozen_snapshot
  ON accounts (frozen_snapshot);
