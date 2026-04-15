package com.smstar.coins;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoinTransactionRepository extends JpaRepository<CoinPurchaseTransaction, Long> {
    List<CoinPurchaseTransaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
