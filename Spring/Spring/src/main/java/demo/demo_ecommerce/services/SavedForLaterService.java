package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.ShoppingCartItemRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SavedForLaterService {

    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final ProductRepository productRepository;
    private final UsersRepository userRepository;
    private final CartService cartService;

    public SavedForLaterService(ShoppingCartItemRepository shoppingCartItemRepository,
                                ProductRepository productRepository,
                                UsersRepository userRepository,
                                CartService cartService) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    /**
     * Salva un prodotto per dopo e rimuove dal carrello attivo se presente.
     */
    @Transactional
    public void saveForLater(Long userId, Long productId, int quantity) {
        userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        Cart cart = cartService.getCartByUserId(userId);

        // Rimuovi eventuale versione attiva (nel carrello) per evitare doppioni
        shoppingCartItemRepository.findByCartIdAndProductIdAndSavedForLaterFalse(cart.getId(), productId)
                .ifPresent(shoppingCartItemRepository::delete);

        // Crea o aggiorna item nei salvati
        ShoppingCartItem item = shoppingCartItemRepository
                .findByCartIdAndProductIdAndSavedForLaterTrue(cart.getId(), productId)
                .orElse(ShoppingCartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(quantity)
                        .savedForLater(true)
                        .build());

        item.setQuantity(quantity);
        item.setSavedForLater(true);
        shoppingCartItemRepository.save(item);
    }

    /**
     * Ritorna tutti gli item salvati per dopo.
     */
    public List<ShoppingCartItem> getSavedItems(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return shoppingCartItemRepository.findByCartIdAndSavedForLaterTrue(cart.getId());
    }

    /**
     * Rimuove un item dalla lista dei salvati.
     */
    @Transactional
    public void removeSavedItem(Long userId, Long productId) {
        Cart cart = cartService.getCartByUserId(userId);
        shoppingCartItemRepository.findByCartIdAndProductIdAndSavedForLaterTrue(cart.getId(), productId)
                .ifPresent(shoppingCartItemRepository::delete);
    }

    /**
     * Rimette nel carrello un item salvato, eventualmente sommando la quantità.
     */
    @Transactional
    public void moveSavedItemToCart(Long userId, Long productId) {
        Cart cart = cartService.getCartByUserId(userId);

        ShoppingCartItem savedItem = shoppingCartItemRepository
                .findByCartIdAndProductIdAndSavedForLaterTrue(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Elemento non trovato tra i salvati."));

        shoppingCartItemRepository.findByCartIdAndProductIdAndSavedForLaterFalse(cart.getId(), productId)
                .ifPresentOrElse(existingItem -> {
                    // Somma quantità e rimuove il salvato
                    existingItem.setQuantity(existingItem.getQuantity() + savedItem.getQuantity());
                    shoppingCartItemRepository.delete(savedItem);
                    shoppingCartItemRepository.save(existingItem);
                }, () -> {
                    savedItem.setSavedForLater(false);
                    shoppingCartItemRepository.save(savedItem);
                });
    }
}
