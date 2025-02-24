package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.CartDTO;
import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.services.CartService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }


    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addItemToCart(@PathVariable Long userId,
                                              @RequestParam @NotNull Long productId,
                                              @RequestParam @Min(1) int quantity) {
        Cart cart = cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }


    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable Long userId,
                                                   @PathVariable Long productId) {
        Cart cart = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }


    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
