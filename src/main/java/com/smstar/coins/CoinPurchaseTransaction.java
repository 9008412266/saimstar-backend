package com.smstar.coins;

import com.smstar.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coin_purchase_transactions")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CoinPurchaseTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private CoinPackage coinPackage;

    @Column(name = "total_coins", nullable = false)
    private int totalCoins;

    @Column(name = "amount_inr", nullable = false)
    private BigDecimal amountInr;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "upi_ref", length = 100)
    private String upiRef;

    @Enumerated(EnumType.STRING)
    private Status status = Status.pending;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum PaymentMethod { upi, bank, card }
    public enum Status { pending, completed, failed }
}
