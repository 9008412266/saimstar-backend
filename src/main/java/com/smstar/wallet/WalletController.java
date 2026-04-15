package com.smstar.wallet;

import com.smstar.gift.GiftService;
import com.smstar.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final UserRepository userRepository;
    private final GiftService giftService;

    // GET /api/wallet/balance
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(Map.of(
                "coinBalance", user.getCoinBalance(),
                "diamondBalance", user.getDiamondBalance()
        ));
    }

    // GET /api/wallet/transactions
    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(giftService.getTransactions(user.getId()));
    }
}
