package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.CartDTO;
import demo.demo_ecommerce.dtos.QuantityUpdateRequest;
import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import demo.demo_ecommerce.repositories.ShoppingCartItemRepository;
import demo.demo_ecommerce.services.CartService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final ShoppingCartItemRepository shoppingCartItemRepository;

    public CartController(CartService cartService, ShoppingCartItemRepository shoppingCartItemRepository) {
        this.cartService = cartService;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
    }

    @PatchMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartDTO> updateItemQuantity(@PathVariable Long userId,
                                                      @PathVariable Long productId,
                                                      @RequestBody QuantityUpdateRequest request) {
        Cart cart = cartService.updateItemQuantity(userId, productId, request.getQuantity());
        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }




        // Altri endpoint già presenti…

    @PostMapping("/apply-coupon-to-item/{cartItemId}/{couponCode}")
    public ResponseEntity<CartDTO> applyCouponToItem(@PathVariable Long cartItemId,
                                                     @PathVariable String couponCode) {
        boolean success = cartService.applyCouponToCartItem(cartItemId, couponCode);
        if (!success) {
            // Nel caso di fallimento, potresti restituire un 400 con un messaggio,
            // oppure un CartDTO invariato. Scegli tu in base alla logica di business.
            return ResponseEntity.badRequest().build();
        }
        // Se il coupon è stato applicato correttamente,
        // recupera il carrello aggiornato e ritorna il DTO
        ShoppingCartItem item = shoppingCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        Cart cart = item.getCart();

        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }




    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartDTO> addItemToCart(@PathVariable Long userId,
                                                 @RequestParam @NotNull Long productId,
                                                 @RequestParam int quantity) {
        Cart cart = cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok(CartDTO.fromEntity(cart)); // Converte Cart in CartDTO
    }




    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable Long userId,
                                                   @PathVariable Long productId) {
        Cart cart = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }


    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<CartDTO> clearCart(@PathVariable Long userId) {
        Cart cart = cartService.clearCart(userId);
        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }

}
