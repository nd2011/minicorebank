CREATE TABLE holds (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  hold_ref VARCHAR(64) NOT NULL UNIQUE,
  account_id BIGINT NOT NULL,
  amount DECIMAL(19,4) NOT NULL,
  currency VARCHAR(16) NOT NULL,
  status VARCHAR(16) NOT NULL,
  idempotency_key VARCHAR(128) NOT NULL UNIQUE,
  note VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  released_at TIMESTAMP NULL,
  CONSTRAINT fk_holds_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE INDEX idx_holds_account_id ON holds(account_id);
CREATE INDEX idx_holds_status ON holds(status);
