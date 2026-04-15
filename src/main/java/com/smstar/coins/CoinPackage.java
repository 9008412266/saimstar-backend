package com.smstar.coins;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "coin_packages")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CoinPackage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int coins;

    @Column(name = "bonus_coins", nullable = false)
    private int bonusCoins;

    @Column(name = "price_inr", nullable = false)
    private BigDecimal priceInr;

    @Column(length = 50)
    private String badge;

    @Column(name = "is_popular", nullable = false)
    private boolean isPopular;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public int getTotalCoins() { return coins + bonusCoins; }
}
