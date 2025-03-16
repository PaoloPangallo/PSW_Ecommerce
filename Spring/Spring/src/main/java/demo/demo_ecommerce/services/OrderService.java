package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.OrderDTO;
import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.OrderItem;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.CartRepository;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UsersRepository userRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository,
                        UsersRepository userRepository,
                        CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    /**
     * Crea un ordine a partire dal carrello dell'utente.
     * Se un coupon è applicato all'item, ricalcola il prezzo scontato
     * usando la percentuale di sconto (BigDecimal) presente in Coupon.
     */
    @Transactional
    public OrderDTO createOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato per l'utente con ID: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Il carrello è vuoto, impossibile creare un ordine");
        }

        // Calcolo del totale (riga per riga, applicando lo sconto se presente)
        BigDecimal totalBD = cart.getItems().stream()
                .map(cartItem -> {
                    // Prezzo di partenza = prezzo del prodotto
                    BigDecimal price = cartItem.getProduct().getPrice();

                    // Se c'è un coupon, calcoliamo lo sconto
                    if (cartItem.getAppliedCoupon() != null) {
                        BigDecimal discount = cartItem.getAppliedCoupon().getDiscountPercentage(); // es. 10, 20
                        // discountRate = (100 - discount) / 100
                        BigDecimal discountRate = BigDecimal.valueOf(100)
                                .subtract(discount)
                                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

                        // Prezzo scontato
                        price = price.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
                    }

                    // Moltiplichiamo per la quantità
                    return price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalBD.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Il totale dell'ordine deve essere maggiore di zero");
        }

        // Creiamo l'ordine (non lo salviamo ancora, così la variabile è effectively final)
        Order unsavedOrder = Order.builder()
                .user(user)
                .total(totalBD)
                .build();

        // Costruiamo la lista di OrderItem nella lambda, usando unsavedOrder
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    BigDecimal productPrice = cartItem.getProduct().getPrice();
                    if (cartItem.getAppliedCoupon() != null) {
                        BigDecimal discount = cartItem.getAppliedCoupon().getDiscountPercentage();
                        BigDecimal discountRate = BigDecimal.valueOf(100)
                                .subtract(discount)
                                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                        productPrice = productPrice.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
                    }
                    return OrderItem.builder()
                            .order(unsavedOrder)
                            .product(cartItem.getProduct())
                            .quantity(cartItem.getQuantity())
                            .price(productPrice)
                            .build();
                })
                .collect(Collectors.toList());

        // Colleghiamo gli OrderItem all'ordine
        unsavedOrder.setOrderItems(orderItems);

        // Ora salviamo l'ordine "completo"
        Order savedOrder = orderRepository.save(unsavedOrder);

        // Svuotiamo il carrello
        cart.getItems().clear();
        cartRepository.save(cart);

        // Restituiamo il DTO
        return OrderDTO.fromEntity(savedOrder);
    }

    /**
     * Recupera una pagina di ordini per l'utente, forzando l'inizializzazione degli orderItems.
     */
    @Transactional
    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUserIdFetchItems(userId, pageable);
        ordersPage.getContent().forEach(order -> {
            if (!Hibernate.isInitialized(order.getOrderItems())) {
                Hibernate.initialize(order.getOrderItems());
            }
        });
        return ordersPage.map(OrderDTO::fromEntity);
    }

    /**
     * Recupera un ordine specifico per l'utente, inizializzando gli orderItems.
     */
    @Transactional
    public OrderDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdFetchItems(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato: " + orderId));
        return OrderDTO.fromEntity(order);
    }
}
