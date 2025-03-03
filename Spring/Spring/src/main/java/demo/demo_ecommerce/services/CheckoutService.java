package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.ShippingDTO;
import demo.demo_ecommerce.dtos.TransactionDTO;
import demo.demo_ecommerce.entities.*;
import demo.demo_ecommerce.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutService.class);

    private final OrderRepository orderRepository;
    private final UsersRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ShippingRepository shippingRepository;
    private final PaymentRepository paymentRepository;

    public CheckoutService(OrderRepository orderRepository,
                           UsersRepository userRepository,
                           TransactionRepository transactionRepository,
                           ShippingRepository shippingRepository,
                           PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.shippingRepository = shippingRepository;
        this.paymentRepository = paymentRepository;
    }

    public Order processCheckout(Long userId, TransactionDTO transactionDTO, ShippingDTO shippingDTO) {
        logger.info("📥 [CheckoutService] Avviando checkout per userId: {}", userId);

        // 1. Recupera l'utente
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato per l'ID: " + userId));
        logger.info("✅ [CheckoutService] Utente trovato: {}", user.getUsername());

        // 2. Crea un nuovo Order associato all'utente
        Order order = Order.builder()
                .user(user)
                .total(transactionDTO.getAmount())
                .status(Order.OrderStatus.CREATED)
                .build();
        orderRepository.save(order);
        logger.info("✅ [CheckoutService] Ordine creato con ID: {}, Totale: {}", order.getId(), order.getTotal());

        // 3. Verifica il metodo di pagamento
        logger.info("🔎 [CheckoutService] Metodo di pagamento ricevuto: {}", transactionDTO.getPaymentMethod());
        Payment payment = paymentRepository.findByPaymentMethod(transactionDTO.getPaymentMethod());
        if (payment == null) {
            logger.error("❌ [CheckoutService] Metodo di pagamento non valido: {}", transactionDTO.getPaymentMethod());
            throw new RuntimeException("Metodo di pagamento non valido: " + transactionDTO.getPaymentMethod());
        }
        logger.info("✅ [CheckoutService] Metodo di pagamento valido: {}", payment.getPaymentMethod());

        // 4. Crea e salva la Transaction
        Transaction transaction = Transaction.builder()
                .order(order)
                .payment(payment)
                .amount(transactionDTO.getAmount())
                .status(Transaction.TransactionStatus.valueOf(transactionDTO.getStatus().toUpperCase()))
                .transactionId("TEMP")
                .build();
        transactionRepository.save(transaction);
        logger.info("✅ [CheckoutService] Transazione salvata con ID: {}, Importo: {}", transaction.getId(), transaction.getAmount());

        // 5. Gestione dello status per la spedizione
        String shippingStatus = shippingDTO.getStatus();
        if (shippingStatus == null) {
            logger.warn("⚠️ [CheckoutService] Shipping status non fornito, impostazione di default su 'PENDING'");
            shippingStatus = "PENDING";
        }

        // 6. Crea e salva la Shipping
        Shipping shipping = Shipping.builder()
                .order(order)
                .address(shippingDTO.getAddress())
                .city(shippingDTO.getCity())
                .zipCode(shippingDTO.getZipCode())
                .country(shippingDTO.getCountry())
                .status(Shipping.ShippingStatus.valueOf(shippingStatus.toUpperCase()))
                .build();
        shippingRepository.save(shipping);
        logger.info("✅ [CheckoutService] Spedizione salvata per ordine ID: {}", order.getId());

        // 7. Aggiorna lo stato dell'ordine
        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.save(order);
        logger.info("✅ [CheckoutService] Ordine aggiornato a stato: {}", order.getStatus());

        return order;
    }
}
