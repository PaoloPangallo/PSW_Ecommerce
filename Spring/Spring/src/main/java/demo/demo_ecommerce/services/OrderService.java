package demo.demo_ecommerce.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.CartRepository;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.UsersRepository;

@Service
public class OrderService {

    private ProductRepository productRepository;
    Product product;
    Pageable pageable;


    private final OrderRepository orderRepository;
    private final UsersRepository userRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository, UsersRepository userRepository,
                        CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Order createOrder(Long userId) {
        // 1. Recupera l'utente
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // 2. Recupera il carrello
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato per l'utente"));

        // 3. Controlla che non sia vuoto
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Il carrello è vuoto, non è possibile creare un ordine");
        }

        // 4. Calcola il totale usando la lista di ShoppingCartItem
        BigDecimal totalBD = cart.getItems().stream()
                .map(item -> {
                    // Se la Product ha un campo BigDecimal price
                    BigDecimal price = item.getProduct().getPrice();
                    int quantity = item.getQuantity();
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double total = totalBD.doubleValue();

        if (total <= 0) {
            throw new IllegalArgumentException("Il totale dell'ordine deve essere maggiore di zero");
        }

        // 5. Crea l'ordine
        Order order = new Order();
        order.setUser(user);
        order.setTotal(BigDecimal.valueOf(total));
        order.setCreatedAt(LocalDateTime.now());

        // 6. Salva l'ordine
        order = orderRepository.save(order);

        // 7. Svuota il carrello
        cart.getItems().clear();
        cartRepository.save(cart);

        return order;
    }

    // Metodo per ottenere tutti gli ordini di un utente
    public Page<Order> getOrdersByUserId(Long userId) {

        return orderRepository.findByUserId(userId, pageable);
    }

    // Metodo per ottenere i dettagli di un singolo ordine
    public Order getOrderById(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato"));
    }

    // Metodo helper per ottenere il prezzo di un prodotto (esempio di mock, puoi sostituirlo con una chiamata al repository dei prodotti)
    private BigDecimal getProductPriceById(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getPrice)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato con ID: " + productId));
    }

}
