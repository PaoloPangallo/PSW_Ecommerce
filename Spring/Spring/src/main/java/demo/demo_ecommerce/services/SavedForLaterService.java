package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.ShoppingCartItemRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
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
        List<ShoppingCartItem> items = shoppingCartItemRepository.findByCartIdAndSavedForLaterTrue(cart.getId());
        log.info("🎯 getSavedItems: restituiti {} item per cart {}", items.size(), cart.getId());
        return items;
    }



    @Transactional
    public void removeSavedItem(Long userId, Long productId) {
        Cart cart = cartService.getCartByUserId(userId);
        Long cartId = cart.getId();

        log.info("🧹 Rimozione via query diretta: cartId={}, productId={}", cartId, productId);
        shoppingCartItemRepository.deleteSavedItem(cartId, productId);
        log.info("🗑 Eliminazione completata per utente {} e prodotto {}", userId, productId);
    }





    /**
     * Rimette nel carrello un item salvato, eventualmente sommando la quantità.
     */
    /**
     * Rimette nel carrello un item salvato, eventualmente sommando la quantità.
     */
    @Transactional
    public void moveSavedItemToCart(Long userId, Long productId) {
        Cart cart = cartService.getCartByUserId(userId);

        ShoppingCartItem savedItem = shoppingCartItemRepository
                .findByCartIdAndProductIdAndSavedForLaterTrue(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Elemento non trovato tra i salvati."));

        Product product = savedItem.getProduct();
        if (product == null) {
            throw new IllegalStateException("Il prodotto dell'item salvato è nullo.");
        }

        // Calcola prezzo aggiornato con eventuale sconto
        var basePrice = product.getPrice();
        var discountPercentage = product.getDiscountPercentage();

        if (discountPercentage != null && discountPercentage > 0) {
            var discountAmount = basePrice
                    .multiply(BigDecimal.valueOf(discountPercentage))
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            var discountedPrice = basePrice.subtract(discountAmount);
            savedItem.setPrice(discountedPrice);
            savedItem.setOldPrice(basePrice);
        } else {
            savedItem.setPrice(basePrice);
            savedItem.setOldPrice(null);
        }

        savedItem.setSavedForLater(false);

        shoppingCartItemRepository.findByCartIdAndProductIdAndSavedForLaterFalse(cart.getId(), productId)
                .ifPresentOrElse(existingItem -> {
                    // Prima aggiorniamo e salviamo l'item esistente
                    existingItem.setQuantity(existingItem.getQuantity() + savedItem.getQuantity());
                    shoppingCartItemRepository.save(existingItem);
                    // Poi eliminiamo l'item salvato, che non va più usato
                    shoppingCartItemRepository.deleteById(savedItem.getId());
                }, () -> {
                    // Nessun item attivo: basta marcare il salvato come attivo
                    shoppingCartItemRepository.save(savedItem);
                });
    }
}