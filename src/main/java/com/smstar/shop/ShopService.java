package com.smstar.shop;

import com.smstar.user.User;
import com.smstar.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;

    public List<Item> getAllItems() {
        return itemRepository.findByIsActiveTrueOrderByTypeAscCoinPriceAsc();
    }

    public List<Item> getItemsByType(String type) {
        return itemRepository.findByTypeAndIsActiveTrueOrderByCoinPriceAsc(Item.ItemType.valueOf(type));
    }

    @Transactional
    public Map<String, Object> buyItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));

        if (userItemRepository.existsByUserIdAndItemId(userId, itemId)) {
            throw new RuntimeException("Item already owned");
        }
        if (!item.isFree() && user.getCoinBalance() < item.getCoinPrice()) {
            throw new RuntimeException("Insufficient coins. Need " + item.getCoinPrice() + ", have " + user.getCoinBalance());
        }
        if (!item.isFree()) {
            user.setCoinBalance(user.getCoinBalance() - item.getCoinPrice());
            userRepository.save(user);
        }
        UserItem ui = UserItem.builder()
                .user(user).item(item)
                .isEquipped(false)
                .purchasedAt(LocalDateTime.now())
                .build();
        userItemRepository.save(ui);

        return Map.of(
                "success", true,
                "itemName", item.getName(),
                "itemEmoji", item.getEmoji(),
                "coinsSpent", item.isFree() ? 0 : item.getCoinPrice(),
                "remainingBalance", user.getCoinBalance()
        );
    }

    @Transactional
    public Map<String, Object> equipItem(Long userId, Long itemId) {
        UserItem ui = userItemRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new RuntimeException("Item not owned"));
        // Unequip all items of same type
        userItemRepository.unequipAllByType(userId, ui.getItem().getType());
        ui.setEquipped(true);
        userItemRepository.save(ui);
        return Map.of("success", true, "equipped", ui.getItem().getName());
    }

    public List<Map<String, Object>> getMyItems(Long userId) {
        return userItemRepository.findByUserIdOrderByPurchasedAtDesc(userId).stream()
                .map(ui -> Map.<String, Object>of(
                        "id", ui.getId(),
                        "itemId", ui.getItem().getId(),
                        "name", ui.getItem().getName(),
                        "type", ui.getItem().getType().name(),
                        "emoji", ui.getItem().getEmoji(),
                        "isEquipped", ui.isEquipped(),
                        "purchasedAt", ui.getPurchasedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getEquipped(Long userId) {
        var myItems = userItemRepository.findByUserIdOrderByPurchasedAtDesc(userId);
        var equipped = myItems.stream()
                .filter(UserItem::isEquipped)
                .collect(Collectors.toMap(
                        ui -> ui.getItem().getType().name(),
                        ui -> Map.of(
                                "id", ui.getItem().getId(),
                                "name", ui.getItem().getName(),
                                "emoji", ui.getItem().getEmoji()
                        )
                ));
        return Map.copyOf(equipped);
    }
}
