package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.CartRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       UsersRepository usersRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.usersRepository = usersRepository;
        this.productRepository = productRepository;
    }

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = usersRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            Cart cart = new Cart(user);
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        Cart cart = getCartByUserId(userId);

        // Recupera il prodotto dal repository
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // Usa il metodo addItem che ora richiede un oggetto Product
        cart.addItem(product, quantity);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItemFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        cart.removeItem(productId);
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cart.clear();
        cartRepository.save(cart);
    }
}
