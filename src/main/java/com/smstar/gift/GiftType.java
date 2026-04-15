package com.smstar.gift;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_types")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GiftType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 10)
    private String emoji;

    @Column(name = "coin_price", nullable = false)
    private int coinPrice;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
