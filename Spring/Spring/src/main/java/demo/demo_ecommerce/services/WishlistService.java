package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.WishlistDTO;
import demo.demo_ecommerce.entities.Wishlist;
import demo.demo_ecommerce.repositories.WishlistRepository;
import demo.demo_ecommerce.Utility.WishlistNotFoundException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    // Crea una nuova wishlist solo se non esiste già per l'utente
    public WishlistDTO createWishlist(Long userId, @Valid WishlistDTO wishlistDTO) {
        if (wishlistRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("A wishlist already exists for this user.");
        }

        // Converti DTO -> Entità
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(null); // Qui dovresti associare lo User corretto
        wishlist.setCreatedDate(LocalDateTime.now());

        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        return WishlistDTO.fromEntity(savedWishlist);
    }

    // Recupera la wishlist tramite ID
    public WishlistDTO getWishlistById(Long id) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found with ID: " + id));
        return WishlistDTO.fromEntity(wishlist);
    }

    // Recupera la wishlist dell'utente
    public WishlistDTO getUserWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new WishlistNotFoundException("No wishlist found for user ID: " + userId));
        return WishlistDTO.fromEntity(wishlist);
    }

    // Elimina una wishlist tramite ID
    public void deleteWishlist(Long id) {
        if (!wishlistRepository.existsById(id)) {
            throw new WishlistNotFoundException("Wishlist not found with ID: " + id);
        }
        wishlistRepository.deleteById(id);
    }

    // Elimina la wishlist per un determinato utente
    public void deleteUserWishlist(Long userId) {
        if (!wishlistRepository.existsByUserId(userId)) {
            throw new WishlistNotFoundException("No wishlist found for user ID: " + userId);
        }
        wishlistRepository.deleteByUserId(userId);
    }

    // Recupera tutte le wishlist come lista di DTO
    public List<WishlistDTO> getAllWishlists() {
        return wishlistRepository.findAll()
                .stream()
                .map(WishlistDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
