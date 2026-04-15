package com.smstar.coins;

import com.smstar.user.User;
import com.smstar.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoinService {

    private final CoinPackageRepository packageRepository;
    private final CoinTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // Fixed UPI ID for receiving payments
    private static final String UPI_ID = "smstar@upi";
    private static final String BANK_ACCOUNT = "1234567890";
    private static final String BANK_IFSC    = "SBIN0001234";
    private static final String BANK_NAME    = "State Bank of India";

    public List<CoinPackage> getPackages() {
        return packageRepository.findByIsActiveTrueOrderByPriceInrAsc();
    }

    /** Initiate purchase — returns payment details to show user */
    @Transactional
    public Map<String, Object> initiatePurchase(Long userId, Long packageId, String method) {
        User user = userRepository.findById(userId).orElseThrow();
        CoinPackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        CoinPurchaseTransaction tx = CoinPurchaseTransaction.builder()
                .user(user)
                .coinPackage(pkg)
                .totalCoins(pkg.getTotalCoins())
                .amountInr(pkg.getPriceInr())
                .paymentMethod(CoinPurchaseTransaction.PaymentMethod.valueOf(method))
                .status(CoinPurchaseTransaction.Status.pending)
                .createdAt(LocalDateTime.now())
                .build();
        tx = transactionRepository.save(tx);

        Map<String, Object> payment = new java.util.HashMap<>();
        payment.put("transactionId", tx.getId());
        payment.put("packageName", pkg.getName());
        payment.put("coins", pkg.getCoins());
        payment.put("bonusCoins", pkg.getBonusCoins());
        payment.put("totalCoins", pkg.getTotalCoins());
        payment.put("amountInr", pkg.getPriceInr());
        payment.put("method", method);

        if ("upi".equals(method)) {
            payment.put("upiId", UPI_ID);
            payment.put("upiUrl", "upi://pay?pa=" + UPI_ID + "&pn=SMStar&am=" + pkg.getPriceInr() + "&tn=Coins+" + pkg.getTotalCoins());
            payment.put("instruction", "Open any UPI app and pay ₹" + pkg.getPriceInr() + " to " + UPI_ID);
        } else {
            payment.put("bankName", BANK_NAME);
            payment.put("accountNumber", BANK_ACCOUNT);
            payment.put("ifsc", BANK_IFSC);
            payment.put("amount", pkg.getPriceInr());
            payment.put("instruction", "Transfer ₹" + pkg.getPriceInr() + " to account " + BANK_ACCOUNT + " (IFSC: " + BANK_IFSC + ")");
        }
        return payment;
    }

    /** Verify/confirm payment (in production this would check with payment gateway) */
    @Transactional
    public Map<String, Object> confirmPayment(Long txId, String upiRef) {
        CoinPurchaseTransaction tx = transactionRepository.findById(txId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (tx.getStatus() == CoinPurchaseTransaction.Status.completed) {
            throw new RuntimeException("Transaction already completed");
        }
        // Add coins to user
        User user = tx.getUser();
        user.setCoinBalance(user.getCoinBalance() + tx.getTotalCoins());
        userRepository.save(user);

        tx.setStatus(CoinPurchaseTransaction.Status.completed);
        tx.setUpiRef(upiRef != null ? upiRef : "MANUAL-" + System.currentTimeMillis());
        transactionRepository.save(tx);

        return Map.of(
                "success", true,
                "coinsAdded", tx.getTotalCoins(),
                "newBalance", user.getCoinBalance(),
                "message", "✅ " + tx.getTotalCoins() + " coins added to your wallet!"
        );
    }

    public List<Map<String, Object>> getPurchaseHistory(Long userId) {
        return transactionRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 20))
                .stream()
                .map(tx -> Map.<String, Object>of(
                        "id", tx.getId(),
                        "packageName", tx.getCoinPackage().getName(),
                        "totalCoins", tx.getTotalCoins(),
                        "amountInr", tx.getAmountInr(),
                        "method", tx.getPaymentMethod().name(),
                        "status", tx.getStatus().name(),
                        "createdAt", tx.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
    }
}
