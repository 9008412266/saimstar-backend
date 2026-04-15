-- Add wallet fields to users
ALTER TABLE users
    ADD COLUMN coin_balance BIGINT NOT NULL DEFAULT 1000,
    ADD COLUMN diamond_balance BIGINT NOT NULL DEFAULT 0;

-- Gift types master table
CREATE TABLE gift_types (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(50)  NOT NULL,
    emoji      VARCHAR(10)  NOT NULL,
    coin_price INT          NOT NULL,
    category   VARCHAR(30)  NOT NULL DEFAULT 'Gift',
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    DEFAULT NOW()
);

-- Pre-populate gift types
INSERT INTO gift_types (name, emoji, coin_price, category) VALUES
  ('Rose',         '🌹',  1,    'Gift'),
  ('Magic Hat',    '🎩',  5,    'Gift'),
  ('Crown',        '👑',  10,   'Gift'),
  ('Lucky Candy',  '🍬',  5,    'Gift'),
  ('Lucky Number', '🔢',  10,   'Gift'),
  ('Dragon',       '🐉',  17,   'Gift'),
  ('Heart Balloon','🎈',  20,   'Gift'),
  ('Star',         '⭐',  50,   'Gift'),
  ('Diamond Ring', '💎',  500,  'Special'),
  ('Mystery Box',  '📦',  100,  'Mysterious Box'),
  ('Ferrari',      '🏎️', 3000, 'Special'),
  ('Clown',        '🤡',  15,   'Activity');

-- Gift transactions log
CREATE TABLE gift_transactions (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id    BIGINT NOT NULL,
    receiver_id  BIGINT NOT NULL,
    gift_type_id BIGINT NOT NULL,
    quantity     INT    NOT NULL DEFAULT 1,
    coins_spent  INT    NOT NULL,
    room_id      VARCHAR(100),
    sent_at      TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_gift_sender   FOREIGN KEY (sender_id)    REFERENCES users(id),
    CONSTRAINT fk_gift_receiver FOREIGN KEY (receiver_id)  REFERENCES users(id),
    CONSTRAINT fk_gift_type     FOREIGN KEY (gift_type_id) REFERENCES gift_types(id)
);
