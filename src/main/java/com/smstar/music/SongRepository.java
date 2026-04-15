package com.smstar.music;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    Page<Song> findByGenreIgnoreCase(String genre, Pageable pageable);
    Page<Song> findByArtistId(Long artistId, Pageable pageable);
    Page<Song> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
