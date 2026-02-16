CREATE TABLE IF NOT EXISTS claims (
  id               UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
  policy_number    VARCHAR(64)  NOT NULL,
  claimant_id      VARCHAR(64)  NOT NULL,
  type             VARCHAR(32)  NOT NULL,
  description      VARCHAR(500) NOT NULL,
  amount_requested DECIMAL(19, 2) NOT NULL,
  status           VARCHAR(32)  NOT NULL,
  created_at       TIMESTAMP    NOT NULL,
  updated_at       TIMESTAMP    NOT NULL
);


CREATE INDEX IF NOT EXISTS idx_claims_status        ON claims(status);
CREATE INDEX IF NOT EXISTS idx_claims_policy_number ON claims(policy_number);
CREATE INDEX IF NOT EXISTS idx_claims_claimant_id   ON claims(claimant_id);
CREATE INDEX IF NOT EXISTS idx_claims_type          ON claims(type);
