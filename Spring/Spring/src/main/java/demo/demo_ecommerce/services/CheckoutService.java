package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.OrderDTO;
import demo.demo_ecommerce.dtos.ShippingDTO;
import demo.demo_ecommerce.dtos.TransactionDTO;
import demo.demo_ecommerce.entities.*;
import demo.demo_ecommerce.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutService {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutService.class);

    private final OrderRepository orderRepository;
    private final UsersRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ShippingRepository shippingRepository;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @PersistenceContext
    private EntityManager entityManager;

    public CheckoutService(OrderRepository orderRepository,
                           UsersRepository userRepository,
                           TransactionRepository transactionRepository,
                           ShippingRepository shippingRepository,
                           PaymentRepository paymentRepository,
                           OrderService orderService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.shippingRepository = shippingRepository;
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    @Transactional
    public Order processCheckout(Long userId, TransactionDTO transactionDTO, ShippingDTO shippingDTO) {
        logger.info("📥 Avvio del checkout per userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + userId));
        logger.info("👤 Utente recuperato: {}", user.getUsername());

        Order.ShippingMethod method = shippingDTO.getShippingMethod() != null
                ? shippingDTO.getShippingMethod()
                : Order.ShippingMethod.STANDARD;

        OrderDTO orderDto = orderService.createOrder(userId, method);
        entityManager.flush();
        logger.info("🧾 Ordine creato con ID: {}, totale: {}", orderDto.getId(), orderDto.getTotal());

        Order order = orderRepository.findById(orderDto.getId())
                .orElseThrow(() -> new RuntimeException("Ordine non trovato con ID: " + orderDto.getId()));

        Payment payment = paymentRepository.findByPaymentMethod(transactionDTO.getPaymentMethod());
        if (payment == null) {
            logger.error("❌ Metodo di pagamento non valido: {}", transactionDTO.getPaymentMethod());
            throw new RuntimeException("Metodo di pagamento non valido: " + transactionDTO.getPaymentMethod());
        }
        logger.info("✅ Metodo di pagamento valido: {}", payment.getPaymentMethod());

        Transaction transaction = Transaction.builder()
                .order(order)
                .payment(payment)
                .amount(transactionDTO.getAmount())
                .status(Transaction.TransactionStatus.valueOf(transactionDTO.getStatus().toUpperCase()))
                .transactionId("TEMP")
                .build();

        transactionRepository.save(transaction);
        logger.info("💰 Transazione salvata (ID={}): {} €", transaction.getId(), transaction.getAmount());

        Shipping.ShippingStatus shippingStatus = shippingDTO.getStatus() != null
                ? Shipping.ShippingStatus.valueOf(shippingDTO.getStatus().toUpperCase())
                : Shipping.ShippingStatus.PENDING;

        Shipping shipping = Shipping.builder()
                .order(order)
                .address(shippingDTO.getAddress())
                .city(shippingDTO.getCity())
                .zipCode(shippingDTO.getZipCode())
                .country(shippingDTO.getCountry())
                .status(shippingStatus)
                .build();

        shippingRepository.save(shipping);
        logger.info("📦 Spedizione salvata per ordine ID: {}", order.getId());

        // Evita sovrascritture precedenti e imposta chiaramente lo stato
        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.saveAndFlush(order); // forza il flush per evitare inconsistenze
        logger.info("📝 Stato ordine aggiornato a: {}", order.getStatus());

        return order;
    }
}
