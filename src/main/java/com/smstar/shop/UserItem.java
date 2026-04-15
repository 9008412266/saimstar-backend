package com.smstar.shop;

import com.smstar.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_items")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "is_equipped", nullable = false)
    private boolean isEquipped = false;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;
}
