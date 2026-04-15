-- SM Star – V3: Chat tables

CREATE TABLE chat_rooms (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100),
    type       ENUM('DIRECT', 'GROUP') NOT NULL DEFAULT 'DIRECT',
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_chat_rooms_creator FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE chat_room_members (
    room_id   BIGINT NOT NULL,
    user_id   BIGINT NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (room_id, user_id),
    CONSTRAINT fk_members_room FOREIGN KEY (room_id) REFERENCES chat_rooms (id) ON DELETE CASCADE,
    CONSTRAINT fk_members_user FOREIGN KEY (user_id) REFERENCES users      (id) ON DELETE CASCADE
);

CREATE TABLE chat_messages (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    room_id      BIGINT NOT NULL,
    sender_id    BIGINT NOT NULL,
    content      TEXT   NOT NULL,
    message_type ENUM('TEXT', 'EMOJI', 'SYSTEM') NOT NULL DEFAULT 'TEXT',
    sent_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_messages_room   FOREIGN KEY (room_id)   REFERENCES chat_rooms (id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users       (id) ON DELETE CASCADE
);

CREATE INDEX idx_messages_room   ON chat_messages (room_id, sent_at);
CREATE INDEX idx_messages_sender ON chat_messages (sender_id);
