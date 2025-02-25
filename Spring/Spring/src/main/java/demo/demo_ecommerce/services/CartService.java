package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.CartRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.ShoppingCartItemRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository; // Aggiunto repository

    public CartService(CartRepository cartRepository,
                       UsersRepository usersRepository,
                       ProductRepository productRepository,
                       ShoppingCartItemRepository shoppingCartItemRepository) { // Aggiunto repository nel costruttore
        this.cartRepository = cartRepository;
        this.usersRepository = usersRepository;
        this.productRepository = productRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
    }

    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> {
                    User user = usersRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });

        // Forza il caricamento della lista `items`
        cart.getItems().size(); // Questo inizializza la lista prima che la sessione si chiuda

        return cart;
    }



    @Transactional
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // Controlla se il prodotto è già nel carrello
        Optional<ShoppingCartItem> existingItemOpt = shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItemOpt.isPresent()) {
            // Se il prodotto è già nel carrello, aggiorniamo la quantità
            ShoppingCartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            shoppingCartItemRepository.save(existingItem);
        } else {
            // Se il prodotto non è presente, creiamo un nuovo elemento
            ShoppingCartItem newItem = new ShoppingCartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            shoppingCartItemRepository.save(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItemFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        Optional<ShoppingCartItem> existingItemOpt = shoppingCartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItemOpt.isPresent()) {
            shoppingCartItemRepository.delete(existingItemOpt.get());
        } else {
            throw new IllegalArgumentException("Item not found in the cart.");
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        shoppingCartItemRepository.deleteAllByCartId(cart.getId());
        cartRepository.save(cart);
    }
}
