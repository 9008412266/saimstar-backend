package com.smstar.chat;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ChatDto {

    @Data
    @Builder
    public static class RoomDto {
        private Long id;
        private String name;
        private String type;
        private List<MemberDto> members;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    public static class MemberDto {
        private Long id;
        private String username;
        private String displayName;
        private String profilePicUrl;
    }

    @Data
    @Builder
    public static class MessageDto {
        private Long id;
        private Long roomId;
        private Long senderId;
        private String senderUsername;
        private String senderDisplayName;
        private String senderProfilePicUrl;
        private String content;
        private String messageType;
        private LocalDateTime sentAt;
    }

    @Data
    public static class CreateRoomRequest {
        private String name;
        private String type;  // DIRECT or GROUP
        private List<Long> memberIds;
    }

    @Data
    public static class SendMessageRequest {
        private Long roomId;
        private String content;
        private String messageType;  // TEXT, EMOJI
    }
}
