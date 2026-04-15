package com.smstar.gift;

import com.smstar.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_transactions")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class GiftTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_type_id", nullable = false)
    private GiftType giftType;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "coins_spent", nullable = false)
    private int coinsSpent;

    @Column(name = "room_id", length = 100)
    private String roomId;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
