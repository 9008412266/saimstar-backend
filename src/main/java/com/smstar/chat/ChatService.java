package com.smstar.chat;

import com.smstar.user.User;
import com.smstar.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository roomRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<ChatDto.RoomDto> getMyRooms() {
        User me = getCurrentUser();
        return roomRepository.findByMemberId(me.getId())
                .stream().map(this::toRoomDto).toList();
    }

    @Transactional
    public ChatDto.RoomDto createRoom(ChatDto.CreateRoomRequest request) {
        User me = getCurrentUser();

        Set<User> members = new HashSet<>();
        members.add(me);
        if (request.getMemberIds() != null) {
            members.addAll(userRepository.findAllById(request.getMemberIds()));
        }

        ChatRoom.RoomType type = ChatRoom.RoomType.valueOf(
                request.getType() != null ? request.getType().toUpperCase() : "GROUP"
        );

        ChatRoom room = ChatRoom.builder()
                .name(request.getName())
                .type(type)
                .createdBy(me)
                .members(members)
                .build();

        return toRoomDto(roomRepository.save(room));
    }

    public Page<ChatDto.MessageDto> getMessages(Long roomId, Pageable pageable) {
        return messageRepository.findByRoomIdOrderBySentAtDesc(roomId, pageable)
                .map(this::toMessageDto);
    }

    @Transactional
    public ChatDto.MessageDto sendMessage(ChatDto.SendMessageRequest request) {
        User sender = getCurrentUser();
        ChatRoom room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        ChatMessage.MessageType type = ChatMessage.MessageType.valueOf(
                request.getMessageType() != null ? request.getMessageType().toUpperCase() : "TEXT"
        );

        ChatMessage message = ChatMessage.builder()
                .room(room)
                .sender(sender)
                .content(request.getContent())
                .messageType(type)
                .build();

        ChatMessage saved = messageRepository.save(message);
        ChatDto.MessageDto dto = toMessageDto(saved);

        // Broadcast via WebSocket to all room subscribers
        messagingTemplate.convertAndSend("/topic/room/" + room.getId(), dto);

        return dto;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private ChatDto.RoomDto toRoomDto(ChatRoom room) {
        List<ChatDto.MemberDto> members = room.getMembers().stream()
                .map(u -> ChatDto.MemberDto.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .displayName(u.getDisplayName())
                        .profilePicUrl(u.getProfilePicUrl())
                        .build())
                .toList();

        return ChatDto.RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .type(room.getType().name())
                .members(members)
                .createdAt(room.getCreatedAt())
                .build();
    }

    private ChatDto.MessageDto toMessageDto(ChatMessage msg) {
        return ChatDto.MessageDto.builder()
                .id(msg.getId())
                .roomId(msg.getRoom().getId())
                .senderId(msg.getSender().getId())
                .senderUsername(msg.getSender().getUsername())
                .senderDisplayName(msg.getSender().getDisplayName())
                .senderProfilePicUrl(msg.getSender().getProfilePicUrl())
                .content(msg.getContent())
                .messageType(msg.getMessageType().name())
                .sentAt(msg.getSentAt())
                .build();
    }
}
