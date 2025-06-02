package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.CartDTO;
import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.SavedForLaterItem;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import demo.demo_ecommerce.services.CartService;
import demo.demo_ecommerce.services.SavedForLaterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved")
public class SavedForLaterController {

    private final SavedForLaterService savedService;
    private final CartService cartService;

    public SavedForLaterController(SavedForLaterService savedService, CartService cartService) {
        this.savedService = savedService;
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> saveForLater(@RequestParam Long userId,
                                                            @RequestParam Long productId,
                                                            @RequestParam int quantity) {
        savedService.saveForLater(userId, productId, quantity);
        return ResponseEntity.ok(Map.of("message", "Elemento salvato correttamente"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ShoppingCartItem>> getSaved(@PathVariable Long userId) {
        return ResponseEntity.ok(savedService.getSavedItems(userId));
    }


    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeSaved(@RequestParam Long userId,
                                                           @RequestParam Long productId) {
        savedService.removeSavedItem(userId, productId);
        return ResponseEntity.ok(Map.of("message", "Elemento rimosso dai salvati"));
    }


    @PostMapping("/restore")
    public ResponseEntity<CartDTO> restoreToCart(@RequestParam Long userId,
                                                 @RequestParam Long productId) {
        // 🔥 MANCAVA QUESTA RIGA:
        savedService.moveSavedItemToCart(userId, productId);

        Cart updatedCart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(CartDTO.fromEntity(updatedCart));
    }




}
