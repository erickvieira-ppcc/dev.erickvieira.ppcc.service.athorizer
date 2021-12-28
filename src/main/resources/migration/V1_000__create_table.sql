CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS tb_wallet(
    id                      UUID NOT NULL,
    user_id                 UUID NOT NULL,
    surname                 VARCHAR(64) NOT NULL,
    is_active               BOOLEAN DEFAULT TRUE,
    is_default              BOOLEAN DEFAULT FALSE,
    min_balance             NUMERIC(15, 2) NOT NULL,
    accept_bank_transfer    BOOLEAN DEFAULT TRUE,
    accept_payments         BOOLEAN DEFAULT TRUE,
    accept_withdrawing      BOOLEAN DEFAULT TRUE,
    accept_deposit          BOOLEAN DEFAULT TRUE,
    created_at              TIMESTAMP WITH TIME ZONE,
    updated_at              TIMESTAMP WITH TIME ZONE,
    deleted_at              TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_transaction(
    id                      UUID default gen_random_uuid(),
    original_id             UUID,
    wallet_id               UUID,
    event                   SMALLINT NOT NULL,
    value                   NUMERIC(15, 2) NOT NULL,
    credit_delta            NUMERIC(15, 2) NOT NULL,
    created_at              TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id),
    CONSTRAINT fk__wallet__transaction
        FOREIGN KEY (wallet_id)
            REFERENCES tb_wallet (id),
    CONSTRAINT fk__transaction__original_id
        FOREIGN KEY (original_id)
            REFERENCES tb_transaction (id)
);