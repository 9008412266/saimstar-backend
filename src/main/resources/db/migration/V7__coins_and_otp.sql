-- ─── Coin Packages ───────────────────────────────────────────────────────────
CREATE TABLE coin_packages (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(100) NOT NULL,
    coins        INT          NOT NULL,
    bonus_coins  INT          NOT NULL DEFAULT 0,
    price_inr    DECIMAL(10,2) NOT NULL,
    badge        VARCHAR(50),
    is_popular   BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE
);

INSERT INTO coin_packages (name, coins, bonus_coins, price_inr, badge, is_popular) VALUES
  ('Starter',    100,   0,    10.00,  NULL,        FALSE),
  ('Basic',      500,   50,   50.00,  '+10% Bonus', FALSE),
  ('Popular',    1200,  200,  100.00, '+20% Bonus', TRUE),
  ('Value',      2600,  600,  200.00, '+30% Bonus', FALSE),
  ('Gold Pack',  7000,  2000, 500.00, '+40% Bonus', FALSE),
  ('Diamond',    15000, 5000, 1000.00,'+50% Bonus', FALSE);

-- ─── Coin Purchase Transactions ───────────────────────────────────────────────
CREATE TABLE coin_purchase_transactions (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id        BIGINT        NOT NULL,
    package_id     BIGINT        NOT NULL,
    total_coins    INT           NOT NULL,
    amount_inr     DECIMAL(10,2) NOT NULL,
    payment_method ENUM('upi','bank','card') NOT NULL,
    upi_ref        VARCHAR(100),
    status         ENUM('pending','completed','failed') DEFAULT 'pending',
    created_at     TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_cpt_user    FOREIGN KEY (user_id)    REFERENCES users(id),
    CONSTRAINT fk_cpt_package FOREIGN KEY (package_id) REFERENCES coin_packages(id)
);

-- ─── Phone OTP ───────────────────────────────────────────────────────────────
ALTER TABLE users
    ADD COLUMN phone_number  VARCHAR(15)  UNIQUE,
    ADD COLUMN auth_provider ENUM('email','phone','facebook','instagram') DEFAULT 'email';

CREATE TABLE phone_otps (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone       VARCHAR(15) NOT NULL,
    otp_code    VARCHAR(6)  NOT NULL,
    expires_at  DATETIME    NOT NULL,
    is_used     BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   DEFAULT NOW(),
    INDEX idx_otp_phone (phone)
);
