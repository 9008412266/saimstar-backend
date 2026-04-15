package com.smstar.gift;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GiftTypeRepository extends JpaRepository<GiftType, Long> {
    List<GiftType> findByIsActiveTrueOrderByCoinPriceAsc();
}
