package com.smstar.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    List<UserItem> findByUserIdOrderByPurchasedAtDesc(Long userId);
    Optional<UserItem> findByUserIdAndItemId(Long userId, Long itemId);
    boolean existsByUserIdAndItemId(Long userId, Long itemId);

    @Modifying
    @Query("UPDATE UserItem u SET u.isEquipped = false WHERE u.user.id = :userId AND u.item.type = :type")
    void unequipAllByType(@Param("userId") Long userId, @Param("type") Item.ItemType type);
}
