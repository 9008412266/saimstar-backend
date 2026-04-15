package com.smstar.gift;

import com.smstar.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService giftService;
    private final UserRepository userRepository;

    // GET /api/gifts/types — all gift types (public)
    @GetMapping("/types")
    public ResponseEntity<List<GiftType>> getGiftTypes() {
        return ResponseEntity.ok(giftService.getAllGiftTypes());
    }

    // POST /api/gifts/send
    @PostMapping("/send")
    public ResponseEntity<?> sendGift(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SendGiftRequest req) {
        try {
            var sender = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            var result = giftService.sendGift(
                    sender.getId(),
                    req.getReceiverId(),
                    req.getGiftTypeId(),
                    req.getQuantity(),
                    req.getRoomId()
            );
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/gifts/transactions — current user's history
    @GetMapping("/transactions")
    public ResponseEntity<List<Map<String, Object>>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(giftService.getTransactions(user.getId()));
    }
}
