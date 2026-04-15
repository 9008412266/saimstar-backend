package com.smstar.music;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MusicDto {

    @Data
    @Builder
    public static class SongDto {
        private Long id;
        private String title;
        private Long artistId;
        private String artistName;
        private Long albumId;
        private String albumTitle;
        private String albumCoverUrl;
        private String streamUrl;
        private Integer durationSeconds;
        private String genre;
        private Long playCount;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    public static class AlbumDto {
        private Long id;
        private String title;
        private Long artistId;
        private String artistName;
        private String coverUrl;
        private LocalDate releaseDate;
        private List<SongDto> songs;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    public static class ArtistDto {
        private Long id;
        private String name;
        private String bio;
        private String imageUrl;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UploadSongRequest {
        private String title;
        private Long artistId;
        private Long albumId;
        private Integer durationSeconds;
        private String genre;
    }
}
