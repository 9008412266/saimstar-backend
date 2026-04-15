package com.smstar.music;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    // ===== Songs =====

    @GetMapping("/songs")
    public ResponseEntity<Page<MusicDto.SongDto>> getSongs(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(musicService.getSongs(genre, artistId, search, pageable));
    }

    @GetMapping("/songs/{id}")
    public ResponseEntity<MusicDto.SongDto> getSong(@PathVariable Long id) {
        return ResponseEntity.ok(musicService.getSongById(id));
    }

    @GetMapping("/songs/{id}/stream")
    public ResponseEntity<Map<String, String>> streamSong(@PathVariable Long id) {
        String url = musicService.getStreamUrl(id);
        return ResponseEntity.ok(Map.of("streamUrl", url));
    }

    @PostMapping("/songs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MusicDto.SongDto> uploadSong(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute MusicDto.UploadSongRequest request) throws IOException {
        return ResponseEntity.ok(musicService.uploadSong(file, request));
    }

    @DeleteMapping("/songs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        musicService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Albums =====

    @GetMapping("/albums")
    public ResponseEntity<Page<MusicDto.AlbumDto>> getAlbums(
            @RequestParam(required = false) Long artistId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(musicService.getAlbums(artistId, pageable));
    }

    @GetMapping("/albums/{id}")
    public ResponseEntity<MusicDto.AlbumDto> getAlbum(@PathVariable Long id) {
        return ResponseEntity.ok(musicService.getAlbumById(id));
    }

    // ===== Artists =====

    @GetMapping("/artists")
    public ResponseEntity<Page<MusicDto.ArtistDto>> getArtists(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(musicService.getArtists(search, pageable));
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity<MusicDto.ArtistDto> getArtist(@PathVariable Long id) {
        return ResponseEntity.ok(musicService.getArtistById(id));
    }
}
