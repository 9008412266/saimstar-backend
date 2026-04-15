package com.smstar.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // ===== REST =====

    @GetMapping("/api/chat/rooms")
    public ResponseEntity<List<ChatDto.RoomDto>> getMyRooms() {
        return ResponseEntity.ok(chatService.getMyRooms());
    }

    @PostMapping("/api/chat/rooms")
    public ResponseEntity<ChatDto.RoomDto> createRoom(@RequestBody ChatDto.CreateRoomRequest request) {
        return ResponseEntity.ok(chatService.createRoom(request));
    }

    @GetMapping("/api/chat/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatDto.MessageDto>> getMessages(
            @PathVariable Long roomId,
            @PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(chatService.getMessages(roomId, pageable));
    }

    // ===== WebSocket STOMP =====

    /**
     * Client sends to: /app/chat.send
     * Message is saved and broadcast to: /topic/room/{roomId}
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatDto.SendMessageRequest request) {
        chatService.sendMessage(request);
    }
}
