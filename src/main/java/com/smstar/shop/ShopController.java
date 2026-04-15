package com.smstar.shop;

import com.smstar.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final UserRepository userRepository;

    // GET /api/shop/items?type=effect|frame|room_bg|music_bg
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getItems(@RequestParam(required = false) String type) {
        if (type != null) return ResponseEntity.ok(shopService.getItemsByType(type));
        return ResponseEntity.ok(shopService.getAllItems());
    }

    // POST /api/shop/buy  { itemId }
    @PostMapping("/buy")
    public ResponseEntity<?> buyItem(@AuthenticationPrincipal UserDetails ud,
                                     @RequestBody Map<String, Long> body) {
        try {
            var user = userRepository.findByEmail(ud.getUsername()).orElseThrow();
            return ResponseEntity.ok(shopService.buyItem(user.getId(), body.get("itemId")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/shop/equip  { itemId }
    @PostMapping("/equip")
    public ResponseEntity<?> equipItem(@AuthenticationPrincipal UserDetails ud,
                                       @RequestBody Map<String, Long> body) {
        try {
            var user = userRepository.findByEmail(ud.getUsername()).orElseThrow();
            return ResponseEntity.ok(shopService.equipItem(user.getId(), body.get("itemId")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/shop/my-items
    @GetMapping("/my-items")
    public ResponseEntity<List<Map<String, Object>>> getMyItems(@AuthenticationPrincipal UserDetails ud) {
        var user = userRepository.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(shopService.getMyItems(user.getId()));
    }

    // GET /api/shop/equipped
    @GetMapping("/equipped")
    public ResponseEntity<Map<String, Object>> getEquipped(@AuthenticationPrincipal UserDetails ud) {
        var user = userRepository.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(shopService.getEquipped(user.getId()));
    }
}
