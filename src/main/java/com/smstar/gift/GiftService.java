package com.smstar.gift;

import com.smstar.user.User;
import com.smstar.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GiftService {

    private final GiftTypeRepository giftTypeRepository;
    private final GiftTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public List<GiftType> getAllGiftTypes() {
        return giftTypeRepository.findByIsActiveTrueOrderByCoinPriceAsc();
    }

    @Transactional
    public Map<String, Object> sendGift(Long senderId, Long receiverId, Long giftTypeId, int quantity, String roomId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        GiftType giftType = giftTypeRepository.findById(giftTypeId)
                .orElseThrow(() -> new RuntimeException("Gift type not found"));

        int totalCost = giftType.getCoinPrice() * quantity;
        if (sender.getCoinBalance() < totalCost) {
            throw new RuntimeException("Insufficient coins. Need " + totalCost + ", have " + sender.getCoinBalance());
        }

        // Deduct coins from sender
        sender.setCoinBalance(sender.getCoinBalance() - totalCost);
        // Add diamonds to receiver (1 diamond per 10 coins)
        receiver.setDiamondBalance(receiver.getDiamondBalance() + (totalCost / 10));

        userRepository.save(sender);
        userRepository.save(receiver);

        GiftTransaction tx = GiftTransaction.builder()
                .sender(sender)
                .receiver(receiver)
                .giftType(giftType)
                .quantity(quantity)
                .coinsSpent(totalCost)
                .roomId(roomId)
                .sentAt(LocalDateTime.now())
                .build();
        transactionRepository.save(tx);

        return Map.of(
                "success", true,
                "giftName", giftType.getName(),
                "giftEmoji", giftType.getEmoji(),
                "coinsSpent", totalCost,
                "senderBalance", sender.getCoinBalance(),
                "receiverUsername", receiver.getUsername(),
                "transactionId", tx.getId()
        );
    }

    public List<Map<String, Object>> getTransactions(Long userId) {
        return transactionRepository
                .findByUserId(userId, PageRequest.of(0, 20))
                .stream()
                .map(tx -> {
                    boolean isSender = tx.getSender().getId().equals(userId);
                    String detail = isSender
                            ? tx.getGiftType().getEmoji() + " " + tx.getGiftType().getName() + " × " + tx.getQuantity() + " to " + tx.getReceiver().getUsername()
                            : tx.getGiftType().getEmoji() + " " + tx.getGiftType().getName() + " from " + tx.getSender().getUsername();
                    return Map.<String, Object>of(
                            "id", tx.getId(),
                            "type", isSender ? "Sent Gift" : "Received Gift",
                            "detail", detail,
                            "amount", isSender ? -tx.getCoinsSpent() : +(tx.getCoinsSpent() / 10),
                            "sentAt", tx.getSentAt().toString()
                    );
                })
                .collect(Collectors.toList());
    }
}
