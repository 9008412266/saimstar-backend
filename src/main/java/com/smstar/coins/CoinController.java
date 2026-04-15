package com.smstar.coins;

import com.smstar.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;
    private final UserRepository userRepository;

    @GetMapping("/packages")
    public ResponseEntity<List<CoinPackage>> getPackages() {
        return ResponseEntity.ok(coinService.getPackages());
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> initiatePurchase(@AuthenticationPrincipal UserDetails ud,
                                              @RequestBody Map<String, Object> body) {
        try {
            var user = userRepository.findByEmail(ud.getUsername()).orElseThrow();
            Long pkgId = Long.valueOf(body.get("packageId").toString());
            String method = body.getOrDefault("method", "upi").toString();
            return ResponseEntity.ok(coinService.initiatePurchase(user.getId(), pkgId, method));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@AuthenticationPrincipal UserDetails ud,
                                            @RequestBody Map<String, Object> body) {
        try {
            Long txId = Long.valueOf(body.get("transactionId").toString());
            String upiRef = body.getOrDefault("upiRef", "").toString();
            return ResponseEntity.ok(coinService.confirmPayment(txId, upiRef));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory(@AuthenticationPrincipal UserDetails ud) {
        var user = userRepository.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(coinService.getPurchaseHistory(user.getId()));
    }
}
