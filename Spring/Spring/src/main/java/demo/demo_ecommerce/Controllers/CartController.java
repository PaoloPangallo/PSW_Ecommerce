package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.CartDTO;
import demo.demo_ecommerce.dtos.QuantityUpdateRequest;
import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.ShoppingCartItemRepository;
import demo.demo_ecommerce.services.CartService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // Consenti l’accesso se:
    // - l'utente è ADMIN, oppure
    // - l'utente nel token JWT (principal) corrisponde a userId (il proprietario del carrello)
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @PatchMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartDTO> updateItemQuantity(@PathVariable Long userId,
                                                      @PathVariable Long productId,
                                                      @RequestBody QuantityUpdateRequest request) {
        Cart cart = cartService.updateItemQuantity(userId, productId, request.getQuantity());
        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }

    // Idem come sopra
    @PostMapping("/apply-coupon-to-item/{cartItemId}/{couponCode}")
    public ResponseEntity<CartDTO> applyCouponToItem(
            @PathVariable Long cartItemId,
            @PathVariable String couponCode,
            @AuthenticationPrincipal User user) {

        ShoppingCartItem item = shoppingCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item non trovato"));

        Long ownerId = item.getCart().getUser().getId();

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        if (!isAdmin && !ownerId.equals(user.getId())) {
            return ResponseEntity.status(403).body(null); // ❌ Non autorizzato
        }

        boolean success = cartService.applyCouponToCartItem(cartItemId, couponCode);
        if (!success) {
            return ResponseEntity.badRequest().build();
        }

        Cart updatedCart = cartService.getCartByUserId(ownerId);
        return ResponseEntity.ok(CartDTO.fromEntity(updatedCart));
    }


    // Get cart: ADMIN può vedere qualsiasi carrello, l’utente può vedere solo il proprio
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }

    // Aggiunge item al carrello (stessa logica di sicurezza)
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @PostMapping("/{userId}/add")
    public ResponseEntity<CartDTO> addItemToCart(@PathVariable Long userId,
                                                 @RequestParam @NotNull Long productId,
                                                 @RequestParam int quantity) {
        Cart cart = cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }

    // Rimuove item dal carrello
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable Long userId,
                                                   @PathVariable Long productId) {
        Cart cart = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }

    // Svuota il carrello
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<CartDTO> clearCart(@PathVariable Long userId) {
        Cart cart = cartService.clearCart(userId);
        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }

}
