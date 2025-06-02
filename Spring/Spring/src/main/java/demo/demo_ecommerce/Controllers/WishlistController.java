package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.Utility.ResourceNotFoundException;
import demo.demo_ecommerce.dtos.WishlistDTO;
import demo.demo_ecommerce.services.WishlistService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {

    private static final Logger logger = LoggerFactory.getLogger(WishlistController.class);

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<WishlistDTO> createWishlist(
            @PathVariable Long userId,
            @Valid @RequestBody WishlistDTO wishlistDTO) {

        logger.info("Creating wishlist for user with ID {}", userId);
        WishlistDTO createdWishlist = wishlistService.createWishlist(userId, wishlistDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWishlist);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishlistDTO> getWishlistById(@PathVariable Long id) {
        WishlistDTO wishlistDTO = wishlistService.getWishlistById(id);
        if (wishlistDTO == null) {
            throw new ResourceNotFoundException("Wishlist not found with ID: " + id);
        }
        return ResponseEntity.ok(wishlistDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Long id) {
        logger.info("Deleting wishlist with ID {}", id);
        wishlistService.deleteWishlist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<WishlistDTO> getUserWishlist(@PathVariable Long userId) {
        WishlistDTO wishlistDTO = wishlistService.getUserWishlist(userId);
        if (wishlistDTO == null) {
            throw new ResourceNotFoundException("Wishlist not found for user with ID: " + userId);
        }
        return ResponseEntity.ok(wishlistDTO);
    }

    @PostMapping("/{wishlistId}/products/{productId}")
    public ResponseEntity<WishlistDTO> addProductToWishlist(
            @PathVariable Long wishlistId,
            @PathVariable Long productId) {

        logger.info("Adding product {} to wishlist {}", productId, wishlistId);
        WishlistDTO updatedWishlist = wishlistService.addProductToWishlist(wishlistId, productId);
        if (updatedWishlist == null) {
            throw new ResourceNotFoundException("Wishlist or product not found");
        }
        return ResponseEntity.ok(updatedWishlist);
    }

    @DeleteMapping("/{wishlistId}/products/{productId}")
    public ResponseEntity<WishlistDTO> removeProductFromWishlist(
            @PathVariable Long wishlistId,
            @PathVariable Long productId) {

        logger.info("Removing product {} from wishlist {}", productId, wishlistId);
        WishlistDTO updatedWishlist = wishlistService.removeProductFromWishlist(wishlistId, productId);
        if (updatedWishlist == null) {
            throw new ResourceNotFoundException("Wishlist or product not found");
        }
        return ResponseEntity.ok(updatedWishlist);
    }
}
