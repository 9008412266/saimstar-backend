package com.smstar.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByTypeAndIsActiveTrueOrderByCoinPriceAsc(Item.ItemType type);
    List<Item> findByIsActiveTrueOrderByTypeAscCoinPriceAsc();
}
