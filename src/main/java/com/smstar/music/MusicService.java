package com.smstar.music;

import com.smstar.storage.OracleStorageService;
import com.smstar.user.User;
import com.smstar.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final OracleStorageService storageService;

    // ===== Songs =====

    public Page<MusicDto.SongDto> getSongs(String genre, Long artistId, String search, Pageable pageable) {
        Page<Song> page;
        if (genre != null)     page = songRepository.findByGenreIgnoreCase(genre, pageable);
        else if (artistId != null) page = songRepository.findByArtistId(artistId, pageable);
        else if (search != null)   page = songRepository.findByTitleContainingIgnoreCase(search, pageable);
        else                       page = songRepository.findAll(pageable);
        return page.map(this::toSongDto);
    }

    public MusicDto.SongDto getSongById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));
        return toSongDto(song);
    }

    public String getStreamUrl(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));
        // Increment play count
        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);
        return storageService.getPreAuthUrl(song.getFileUrl());
    }

    @Transactional
    public MusicDto.SongDto uploadSong(MultipartFile file, MusicDto.UploadSongRequest request) throws IOException {
        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));

        Album album = null;
        if (request.getAlbumId() != null) {
            album = albumRepository.findById(request.getAlbumId())
                    .orElseThrow(() -> new IllegalArgumentException("Album not found"));
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User uploader = userRepository.findByEmail(email).orElse(null);

        String objectName = "songs/" + artist.getId() + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        storageService.uploadFile(objectName, file.getInputStream(), file.getContentType());

        Song song = Song.builder()
                .title(request.getTitle())
                .artist(artist)
                .album(album)
                .fileUrl(objectName)
                .durationSeconds(request.getDurationSeconds())
                .genre(request.getGenre())
                .playCount(0L)
                .uploadedBy(uploader)
                .build();

        return toSongDto(songRepository.save(song));
    }

    @Transactional
    public void deleteSong(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));
        storageService.deleteFile(song.getFileUrl());
        songRepository.delete(song);
    }

    // ===== Albums =====

    public Page<MusicDto.AlbumDto> getAlbums(Long artistId, Pageable pageable) {
        Page<Album> page = artistId != null
                ? albumRepository.findByArtistId(artistId, pageable)
                : albumRepository.findAll(pageable);
        return page.map(this::toAlbumDto);
    }

    public MusicDto.AlbumDto getAlbumById(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));
        return toAlbumDtoWithSongs(album);
    }

    // ===== Artists =====

    public Page<MusicDto.ArtistDto> getArtists(String search, Pageable pageable) {
        Page<Artist> page = search != null
                ? artistRepository.findByNameContainingIgnoreCase(search, pageable)
                : artistRepository.findAll(pageable);
        return page.map(this::toArtistDto);
    }

    public MusicDto.ArtistDto getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));
        return toArtistDto(artist);
    }

    // ===== Mappers =====

    private MusicDto.SongDto toSongDto(Song song) {
        return MusicDto.SongDto.builder()
                .id(song.getId())
                .title(song.getTitle())
                .artistId(song.getArtist().getId())
                .artistName(song.getArtist().getName())
                .albumId(song.getAlbum() != null ? song.getAlbum().getId() : null)
                .albumTitle(song.getAlbum() != null ? song.getAlbum().getTitle() : null)
                .albumCoverUrl(song.getAlbum() != null ? song.getAlbum().getCoverUrl() : null)
                .durationSeconds(song.getDurationSeconds())
                .genre(song.getGenre())
                .playCount(song.getPlayCount())
                .createdAt(song.getCreatedAt())
                .build();
    }

    private MusicDto.AlbumDto toAlbumDto(Album album) {
        return MusicDto.AlbumDto.builder()
                .id(album.getId())
                .title(album.getTitle())
                .artistId(album.getArtist().getId())
                .artistName(album.getArtist().getName())
                .coverUrl(album.getCoverUrl())
                .releaseDate(album.getReleaseDate())
                .createdAt(album.getCreatedAt())
                .build();
    }

    private MusicDto.AlbumDto toAlbumDtoWithSongs(Album album) {
        MusicDto.AlbumDto dto = toAlbumDto(album);
        if (album.getSongs() != null) {
            dto.setSongs(album.getSongs().stream().map(this::toSongDto).toList());
        }
        return dto;
    }

    private MusicDto.ArtistDto toArtistDto(Artist artist) {
        return MusicDto.ArtistDto.builder()
                .id(artist.getId())
                .name(artist.getName())
                .bio(artist.getBio())
                .imageUrl(artist.getImageUrl())
                .createdAt(artist.getCreatedAt())
                .build();
    }
}
