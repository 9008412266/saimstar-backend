package com.smstar.shop;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    @Column(length = 10)
    private String emoji;

    @Column(length = 200)
    private String description;

    @Column(name = "coin_price", nullable = false)
    private int coinPrice;

    @Column(name = "is_free", nullable = false)
    private boolean isFree;

    @Column(name = "preview_url", length = 500)
    private String previewUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum ItemType { effect, frame, room_bg, music_bg }
}
