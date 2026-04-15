package com.smstar.emoji;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmojiPackRepository extends JpaRepository<EmojiPack, Long> {
    List<EmojiPack> findByIsActiveTrue();
}
