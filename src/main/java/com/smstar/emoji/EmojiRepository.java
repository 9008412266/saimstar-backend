package com.smstar.emoji;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmojiRepository extends JpaRepository<Emoji, Long> {
    List<Emoji> findByPackId(Long packId);
    List<Emoji> findByNameContainingIgnoreCaseOrShortcodeContainingIgnoreCase(String name, String shortcode);
}
