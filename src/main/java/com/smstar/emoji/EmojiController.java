package com.smstar.emoji;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/emojis")
@RequiredArgsConstructor
public class EmojiController {

    private final EmojiService emojiService;

    @GetMapping("/packs")
    public ResponseEntity<List<EmojiDto.PackDto>> getAllPacks() {
        return ResponseEntity.ok(emojiService.getAllPacks());
    }

    @GetMapping("/packs/{id}")
    public ResponseEntity<EmojiDto.PackDto> getPackById(@PathVariable Long id) {
        return ResponseEntity.ok(emojiService.getPackById(id));
    }

    @PostMapping("/packs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmojiDto.PackDto> createPack(@RequestBody EmojiDto.CreatePackRequest request) {
        return ResponseEntity.ok(emojiService.createPack(request));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmojiDto.EmojiItem> addEmoji(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute EmojiDto.AddEmojiRequest request) throws IOException {
        return ResponseEntity.ok(emojiService.addEmoji(file, request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmojiDto.EmojiItem>> searchEmojis(@RequestParam String q) {
        return ResponseEntity.ok(emojiService.searchEmojis(q));
    }
}
