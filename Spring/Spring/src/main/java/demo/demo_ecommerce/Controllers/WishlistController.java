package demo.demo_ecommerce.Controllers;


import demo.demo_ecommerce.dtos.WishlistDTO;
import demo.demo_ecommerce.services.WishlistService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // Creazione di una nuova wishlist per un utente
    @PostMapping("/user/{userId}")
    public ResponseEntity<WishlistDTO> createWishlist(
            @PathVariable Long userId,
            @Valid @RequestBody WishlistDTO wishlistDTO) {
        WishlistDTO createdWishlist = wishlistService.createWishlist(userId, wishlistDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWishlist);
    }

    // Recupera una wishlist tramite ID
    @GetMapping("/{id}")
    public ResponseEntity<WishlistDTO> getWishlistById(@PathVariable Long id) {
        WishlistDTO wishlistDTO = wishlistService.getWishlistById(id);
        return ResponseEntity.ok(wishlistDTO);
    }

    // Recupera la wishlist di un utente
    @GetMapping("/user/{userId}")
    public ResponseEntity<WishlistDTO> getUserWishlist(@PathVariable Long userId) {
        WishlistDTO wishlistDTO = wishlistService.getUserWishlist(userId);
        return ResponseEntity.ok(wishlistDTO);
    }

    // Elimina una wishlist tramite ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Long id) {
        wishlistService.deleteWishlist(id);
        return ResponseEntity.noContent().build();
    }
}
