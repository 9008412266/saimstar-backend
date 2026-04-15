package com.smstar.emoji;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class EmojiDto {

    @Data
    @Builder
    public static class PackDto {
        private Long id;
        private String name;
        private String description;
        private String thumbnailUrl;
        private List<EmojiItem> emojis;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    public static class EmojiItem {
        private Long id;
        private String name;
        private String shortcode;
        private String imageUrl;
    }

    @Data
    public static class CreatePackRequest {
        private String name;
        private String description;
    }

    @Data
    public static class AddEmojiRequest {
        private Long packId;
        private String name;
        private String shortcode;
    }
}
