CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE cards (
    id                    BIGSERIAL PRIMARY KEY,
    card_number_encrypted VARCHAR(255)   NOT NULL,
    card_number_masked    VARCHAR(19)    NOT NULL,
    owner_id              BIGINT         NOT NULL,
    expiry_date           DATE           NOT NULL,
    status                VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    balance               DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    created_at            TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_cards_user FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);


CREATE INDEX idx_cards_owner_id ON cards (owner_id);
CREATE INDEX idx_cards_status   ON cards (status);