package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.OrderDTO;
import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.CartRepository;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UsersRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UsersRepository userRepository,
                        CartRepository cartRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderDTO createOrder(Long userId) {
        // 1. Trova l'utente
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + userId));

        // 2. Trova il carrello
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato per l'utente con ID: " + userId));

        // 3. Controlla se il carrello è vuoto
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Il carrello è vuoto, impossibile creare un ordine");
        }

        // 4. Calcola il totale
        BigDecimal totalBD = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalBD.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Il totale dell'ordine deve essere maggiore di zero");
        }

        // 5. Crea l'ordine
        Order order = Order.builder()
                .user(user)
                .total(totalBD)
                .build();

        order = orderRepository.save(order);

        // 6. Svuota il carrello
        cart.getItems().clear();
        cartRepository.save(cart);

        // Ritorna l'ordine convertito in DTO (per il create, non serve caricare gli item)
        return OrderDTO.fromEntity(order, false);
    }

    // Restituisce una pagina di ordini associati a un utente (non carichiamo gli items per la lista)
    public Page<Order> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    public OrderDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdFetchItems(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato: " + orderId));
        return OrderDTO.fromEntity(order);
    }


    @Transactional
    public Order updateOrder(Order order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("L'ordine non può essere nullo e deve avere un ID valido");
        }
        return orderRepository.save(order);
    }
}
