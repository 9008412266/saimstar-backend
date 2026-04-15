-- SM Star – V4: Emoji tables

CREATE TABLE emoji_packs (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    description   TEXT,
    thumbnail_url VARCHAR(500),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_emoji_packs_name (name)
);

CREATE TABLE emojis (
    id        BIGINT      NOT NULL AUTO_INCREMENT,
    pack_id   BIGINT      NOT NULL,
    name      VARCHAR(100) NOT NULL,
    shortcode VARCHAR(50)  NOT NULL COMMENT 'e.g. :heart_eyes:',
    image_url VARCHAR(500) NOT NULL COMMENT 'Oracle Bucket path',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_emoji_shortcode (shortcode),
    CONSTRAINT fk_emojis_pack FOREIGN KEY (pack_id) REFERENCES emoji_packs (id) ON DELETE CASCADE
);

CREATE INDEX idx_emojis_pack      ON emojis (pack_id);
CREATE INDEX idx_emojis_shortcode ON emojis (shortcode);
