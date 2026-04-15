-- SM Star – V2: Music tables (artists, albums, songs)

CREATE TABLE artists (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(150) NOT NULL,
    bio         TEXT,
    image_url   VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);

CREATE TABLE albums (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    title        VARCHAR(200) NOT NULL,
    artist_id    BIGINT       NOT NULL,
    cover_url    VARCHAR(500),
    release_date DATE,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_albums_artist FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE
);

CREATE TABLE songs (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    title            VARCHAR(200) NOT NULL,
    artist_id        BIGINT       NOT NULL,
    album_id         BIGINT,
    file_url         VARCHAR(500) NOT NULL COMMENT 'Oracle Bucket object path',
    duration_seconds INT          NOT NULL DEFAULT 0,
    genre            VARCHAR(50),
    play_count       BIGINT NOT NULL DEFAULT 0,
    uploaded_by      BIGINT COMMENT 'FK to users.id',
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_songs_artist FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE,
    CONSTRAINT fk_songs_album  FOREIGN KEY (album_id)  REFERENCES albums  (id) ON DELETE SET NULL,
    CONSTRAINT fk_songs_uploader FOREIGN KEY (uploaded_by) REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX idx_songs_genre    ON songs (genre);
CREATE INDEX idx_songs_artist   ON songs (artist_id);
CREATE INDEX idx_albums_artist  ON albums (artist_id);
