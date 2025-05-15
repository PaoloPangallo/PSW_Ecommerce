package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.OrderDTO;
import demo.demo_ecommerce.entities.*;
import demo.demo_ecommerce.entities.Order.ShippingMethod;
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

    @Transactional
    public OrderDTO createOrder(Long userId, ShippingMethod shippingMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato per l'utente con ID: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Il carrello è vuoto, impossibile creare un ordine");
        }

        // Calcolo totale carrello con eventuali sconti
        BigDecimal totalBD = cart.getItems().stream()
                .map(cartItem -> {
                    BigDecimal price = cartItem.getProduct().getPrice();
                    if (cartItem.getAppliedCoupon() != null) {
                        BigDecimal discount = cartItem.getAppliedCoupon().getDiscountPercentage();
                        BigDecimal discountRate = BigDecimal.valueOf(100)
                                .subtract(discount)
                                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                        price = price.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
                    }
                    return price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalBD.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Il totale dell'ordine deve essere maggiore di zero");
        }

        // Calcolo del costo di spedizione
        BigDecimal shippingCost = calculateShippingCost(totalBD, shippingMethod);

        // Crea ordine
        Order unsavedOrder = Order.builder()
                .user(user)
                .total(totalBD)
                .shippingMethod(shippingMethod)
                .shippingCost(shippingCost)
                .build();

        // Crea OrderItem
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

                    Product product = cartItem.getProduct();
                    int quantity = cartItem.getQuantity();
                    if (product.getStock() < quantity) {
                        throw new IllegalArgumentException("Stock insufficiente per il prodotto: " + product.getName());
                    }
                    product.setStock(product.getStock() - quantity);

                    return OrderItem.builder()
                            .order(unsavedOrder)
                            .product(product)
                            .quantity(quantity)
                            .price(productPrice)
                            .build();
                })
                .collect(Collectors.toList());

        unsavedOrder.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(unsavedOrder);

        // Svuota carrello
        cart.getItems().clear();
        cartRepository.save(cart);

        return OrderDTO.fromEntity(savedOrder, true); // o false se vuoi evitare il caricamento degli items
    }

    private BigDecimal calculateShippingCost(BigDecimal total, ShippingMethod method) {
        if (total.compareTo(new BigDecimal("100.00")) >= 0) {
            return BigDecimal.ZERO;
        }

        return switch (method) {
            case STANDARD -> new BigDecimal("4.99");
            case EXPRESS -> new BigDecimal("9.99");
            case PREMIUM -> new BigDecimal("14.99");
        };
    }

    @Transactional
    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUserIdFetchItems(userId, pageable);
        ordersPage.getContent().forEach(order -> {
            if (!Hibernate.isInitialized(order.getOrderItems())) {
                Hibernate.initialize(order.getOrderItems());
            }
        });
        return ordersPage.map(order -> OrderDTO.fromEntity(order, true));
    }

    @Transactional
    public OrderDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdFetchItems(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato: " + orderId));
        return OrderDTO.fromEntity(order, true);
    }
}
