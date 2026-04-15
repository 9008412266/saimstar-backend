-- ─── Item Shop ───────────────────────────────────────────────────────────────
CREATE TABLE items (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(100) NOT NULL,
    type         ENUM('effect','frame','room_bg','music_bg') NOT NULL,
    emoji        VARCHAR(10)  DEFAULT '✨',
    description  VARCHAR(200),
    coin_price   INT          NOT NULL DEFAULT 0,
    is_free      BOOLEAN      NOT NULL DEFAULT FALSE,
    preview_url  VARCHAR(500),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    DEFAULT NOW()
);

-- Entry Effects
INSERT INTO items (name, type, emoji, description, coin_price, is_free) VALUES
  ('Default Entry',    'effect', '🚶', 'Simple walk-in', 0, TRUE),
  ('Star Burst',       'effect', '⭐', 'Stars explode on entry', 50,  FALSE),
  ('Rose Shower',      'effect', '🌹', 'Roses fall from sky',   100, FALSE),
  ('Fireworks',        'effect', '🎆', 'Colorful fireworks',    150, FALSE),
  ('Dragon Entry',     'effect', '🐉', 'Dragon flies in',       300, FALSE),
  ('Golden Entry',     'effect', '👑', 'Golden glow entrance',  500, FALSE),
  ('Galaxy Entry',     'effect', '🌌', 'Galaxy swirl entrance', 800, FALSE);

-- Profile Frames
INSERT INTO items (name, type, emoji, description, coin_price, is_free) VALUES
  ('No Frame',         'frame',  '⭕', 'Default circle avatar',  0,   TRUE),
  ('Gold Frame',       'frame',  '🟡', 'Shining gold border',    100, FALSE),
  ('Diamond Frame',    'frame',  '💎', 'Diamond crystal border', 500, FALSE),
  ('Crown Frame',      'frame',  '👑', 'Royal crown border',     200, FALSE),
  ('Heart Frame',      'frame',  '❤️', 'Heart-shaped border',    80,  FALSE),
  ('Star Frame',       'frame',  '⭐', 'Star-shaped border',     120, FALSE),
  ('Neon Frame',       'frame',  '🔆', 'Glowing neon border',    180, FALSE);

-- Room Backgrounds
INSERT INTO items (name, type, emoji, description, coin_price, is_free) VALUES
  ('Default Dark',     'room_bg','🌑', 'Classic dark room',      0,   TRUE),
  ('Night Sky',        'room_bg','🌌', 'Starry night sky',       50,  FALSE),
  ('Sunset Beach',     'room_bg','🌅', 'Warm sunset vibes',      80,  FALSE),
  ('Neon City',        'room_bg','🏙', 'Cyberpunk neon city',    100, FALSE),
  ('Cherry Blossom',   'room_bg','🌸', 'Japanese spring bloom',  120, FALSE),
  ('Space Station',    'room_bg','🚀', 'Outer space views',      150, FALSE),
  ('Underwater',       'room_bg','🐠', 'Deep ocean scenery',     200, FALSE);

-- Music / Singing Backgrounds
INSERT INTO items (name, type, emoji, description, coin_price, is_free) VALUES
  ('Default Stage',    'music_bg','🎤', 'Classic concert stage', 0,   TRUE),
  ('Galaxy Stage',     'music_bg','🌌', 'Galaxy nebula stage',   60,  FALSE),
  ('Neon Stage',       'music_bg','🎸', 'Electric neon stage',   90,  FALSE),
  ('Forest Stage',     'music_bg','🌲', 'Enchanted forest',      100, FALSE),
  ('Sky Stage',        'music_bg','☁️', 'Floating cloud stage',  120, FALSE),
  ('Fire Stage',       'music_bg','🔥', 'Blazing fire backdrop', 150, FALSE);

-- User purchased items
CREATE TABLE user_items (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT   NOT NULL,
    item_id      BIGINT   NOT NULL,
    is_equipped  BOOLEAN  NOT NULL DEFAULT FALSE,
    purchased_at TIMESTAMP DEFAULT NOW(),
    UNIQUE KEY uq_user_item (user_id, item_id),
    CONSTRAINT fk_ui_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ui_item FOREIGN KEY (item_id) REFERENCES items(id)
);
