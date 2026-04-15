package com.smstar.emoji;

import com.smstar.storage.OracleStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private final EmojiPackRepository packRepository;
    private final EmojiRepository emojiRepository;
    private final OracleStorageService storageService;

    public List<EmojiDto.PackDto> getAllPacks() {
        return packRepository.findByIsActiveTrue()
                .stream().map(this::toPackDtoWithEmojis).toList();
    }

    public EmojiDto.PackDto getPackById(Long id) {
        EmojiPack pack = packRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Emoji pack not found"));
        return toPackDtoWithEmojis(pack);
    }

    @Transactional
    public EmojiDto.PackDto createPack(EmojiDto.CreatePackRequest request) {
        EmojiPack pack = EmojiPack.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .build();
        return toPackDto(packRepository.save(pack));
    }

    @Transactional
    public EmojiDto.EmojiItem addEmoji(MultipartFile file, EmojiDto.AddEmojiRequest request) throws IOException {
        EmojiPack pack = packRepository.findById(request.getPackId())
                .orElseThrow(() -> new IllegalArgumentException("Emoji pack not found"));

        String objectName = "emojis/" + pack.getId() + "/" + request.getShortcode() + "_" + file.getOriginalFilename();
        String url = storageService.uploadFile(objectName, file.getInputStream(), file.getContentType());

        Emoji emoji = Emoji.builder()
                .pack(pack)
                .name(request.getName())
                .shortcode(request.getShortcode())
                .imageUrl(url)
                .isActive(true)
                .build();

        Emoji saved = emojiRepository.save(emoji);
        return toEmojiItem(saved);
    }

    public List<EmojiDto.EmojiItem> searchEmojis(String query) {
        return emojiRepository
                .findByNameContainingIgnoreCaseOrShortcodeContainingIgnoreCase(query, query)
                .stream().map(this::toEmojiItem).toList();
    }

    private EmojiDto.PackDto toPackDto(EmojiPack pack) {
        return EmojiDto.PackDto.builder()
                .id(pack.getId())
                .name(pack.getName())
                .description(pack.getDescription())
                .thumbnailUrl(pack.getThumbnailUrl())
                .createdAt(pack.getCreatedAt())
                .build();
    }

    private EmojiDto.PackDto toPackDtoWithEmojis(EmojiPack pack) {
        EmojiDto.PackDto dto = toPackDto(pack);
        if (pack.getEmojis() != null) {
            dto.setEmojis(pack.getEmojis().stream().map(this::toEmojiItem).toList());
        }
        return dto;
    }

    private EmojiDto.EmojiItem toEmojiItem(Emoji emoji) {
        return EmojiDto.EmojiItem.builder()
                .id(emoji.getId())
                .name(emoji.getName())
                .shortcode(emoji.getShortcode())
                .imageUrl(emoji.getImageUrl())
                .build();
    }
}
