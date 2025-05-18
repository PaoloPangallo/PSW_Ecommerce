package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.WishlistDTO;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.entities.Wishlist;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import demo.demo_ecommerce.repositories.WishlistRepository;
import demo.demo_ecommerce.Utility.WishlistNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    @Autowired
    private UsersRepository userRepository;

    public WishlistDTO createWishlist(Long userId, @Valid WishlistDTO wishlistDTO) {
        // Controlla se l'utente esiste
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Controlla se l'utente ha già una wishlist
        if (wishlistRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("A wishlist already exists for this user.");
        }

        // Crea e salva la nuova wishlist associata all'utente
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .createdDate(LocalDateTime.now())
                .build();

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return WishlistDTO.fromEntity(savedWishlist);
    }


    // Recupera la wishlist tramite ID
    public WishlistDTO getWishlistById(Long id) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found with ID: " + id));
        return WishlistDTO.fromEntity(wishlist);
    }
    @Transactional
    public WishlistDTO getUserWishlist(Long userId) {
        // Prova a recuperare la wishlist dell'utente
        Optional<Wishlist> optionalWishlist = wishlistRepository.findByUserIdWithProducts(userId);
        if (optionalWishlist.isPresent()) {
            return WishlistDTO.fromEntity(optionalWishlist.get());
        } else {
            // Recupera l'utente (assicurati di avere il repository degli utenti)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            // Crea una nuova wishlist vuota per l'utente
            Wishlist newWishlist = Wishlist.builder()
                    .user(user)
                    .createdDate(LocalDateTime.now())
                    .build();
            Wishlist savedWishlist = wishlistRepository.save(newWishlist);
            return WishlistDTO.fromEntity(savedWishlist);
        }
    }


    @Transactional
public void deleteWishlist(Long id) {
        if (!wishlistRepository.existsById(id)) {
            throw new WishlistNotFoundException("Wishlist not found with ID: " + id);
        }
        wishlistRepository.deleteById(id);
    }

    @Transactional
    public WishlistDTO addProductToWishlist(Long wishlistId, Long productId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found with ID: " + wishlistId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        wishlist.getProducts().add(product);
        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        return WishlistDTO.fromEntity(savedWishlist);
    }



    @Transactional
    public WishlistDTO removeProductFromWishlist(Long wishlistId, Long productId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new WishlistNotFoundException("Wishlist not found with ID: " + wishlistId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        wishlist.getProducts().remove(product);
        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        return WishlistDTO.fromEntity(savedWishlist);
    }

}
