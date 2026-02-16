CREATE TABLE IF NOT EXISTS flows (
  id             UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
  claim_id       UUID NULL,
  status         VARCHAR(32) NOT NULL,
  last_step      VARCHAR(32) NULL,
  executed_steps VARCHAR(500) NOT NULL,
  error_message  VARCHAR(1000) NULL,
  created_at     TIMESTAMP NOT NULL,
  updated_at     TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_flows_status ON flows(status);
CREATE INDEX IF NOT EXISTS idx_flows_claim_id ON flows(claim_id);