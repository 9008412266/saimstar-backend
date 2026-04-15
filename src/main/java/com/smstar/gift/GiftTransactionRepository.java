package com.smstar.gift;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GiftTransactionRepository extends JpaRepository<GiftTransaction, Long> {

    @Query("SELECT t FROM GiftTransaction t WHERE t.sender.id = :userId OR t.receiver.id = :userId ORDER BY t.sentAt DESC")
    List<GiftTransaction> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
