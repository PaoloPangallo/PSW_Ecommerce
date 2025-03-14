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

    @Transactional
    public OrderDTO createOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato per l'utente con ID: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Il carrello è vuoto, impossibile creare un ordine");
        }

        // Calcoliamo il totale sommando il prezzo di ogni prodotto, applicando il coupon se presente
        BigDecimal totalBD = cart.getItems().stream()
                .map(cartItem -> {
                    BigDecimal price = cartItem.getProduct().getPrice();
                    if (cartItem.getAppliedCoupon() != null) {
                        double discount = cartItem.getAppliedCoupon().getDiscountPercentage();
                        // Calcolo: prezzo scontato = prezzo * (1 - discount/100)
                        BigDecimal discountMultiplier = BigDecimal.valueOf(100 - discount)
                                .divide(BigDecimal.valueOf(100));
                        price = price.multiply(discountMultiplier);
                    }
                    return price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalBD.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Il totale dell'ordine deve essere maggiore di zero");
        }

        Order order = Order.builder()
                .user(user)
                .total(totalBD)
                .build();

        Order finalOrder = order;
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    BigDecimal productPrice = cartItem.getProduct().getPrice();
                    if (cartItem.getAppliedCoupon() != null) {
                        double discount = cartItem.getAppliedCoupon().getDiscountPercentage();
                        BigDecimal discountMultiplier = BigDecimal.valueOf(100 - discount)
                                .divide(BigDecimal.valueOf(100));
                        productPrice = productPrice.multiply(discountMultiplier);
                    }
                    return OrderItem.builder()
                            .order(finalOrder)
                            .product(cartItem.getProduct())
                            .quantity(cartItem.getQuantity())
                            .price(productPrice)
                            .build();
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order = orderRepository.save(order);

        // Svuota il carrello dopo la creazione dell'ordine
        cart.getItems().clear();
        cartRepository.save(cart);

        return OrderDTO.fromEntity(order);
    }


    // Metodo aggiornato per recuperare gli ordini con join fetch degli orderItems
    @Transactional
    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUserIdFetchItems(userId, pageable);
        // Forza l'inizializzazione degli orderItems per ogni ordine
        ordersPage.getContent().forEach(order -> {
            if (!Hibernate.isInitialized(order.getOrderItems())) {
                Hibernate.initialize(order.getOrderItems());
            }
        });
        return ordersPage.map(OrderDTO::fromEntity);
    }

    @Transactional
    public OrderDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdFetchItems(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato: " + orderId));
        return OrderDTO.fromEntity(order);
    }




}
