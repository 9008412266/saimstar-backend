package com.smstar.coins;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoinPackageRepository extends JpaRepository<CoinPackage, Long> {
    List<CoinPackage> findByIsActiveTrueOrderByPriceInrAsc();
}
