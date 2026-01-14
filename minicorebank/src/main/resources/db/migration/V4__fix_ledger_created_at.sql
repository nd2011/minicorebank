ALTER TABLE ledger_entries
MODIFY created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);
