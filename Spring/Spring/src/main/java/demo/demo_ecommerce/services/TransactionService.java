package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.InvalidTransactionException;
import demo.demo_ecommerce.entities.Transaction;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.PaymentRepository;
import demo.demo_ecommerce.repositories.TransactionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              PaymentRepository paymentRepository,
                              OrderRepository orderRepository) {
        this.transactionRepository = transactionRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    // Creazione di una nuova transazione
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getPayment() == null || transaction.getOrder() == null) {
            throw new InvalidTransactionException("Both Payment and Order must be provided.");
        }

        // Verifica esistenza delle entità correlate
        paymentRepository.findById(transaction.getPayment().getId())
                .orElseThrow(() -> new InvalidTransactionException("Payment not found"));
        orderRepository.findById(transaction.getOrder().getId())
                .orElseThrow(() -> new InvalidTransactionException("Order not found"));

        if (transaction.getStatus() == null) {
            transaction.setStatus(Transaction.TransactionStatus.PENDING);
        }

        logger.info("Creating transaction for Order ID: {} and Payment ID: {}",
                transaction.getOrder().getId(), transaction.getPayment().getId());
        return transactionRepository.save(transaction);
    }

    // Recupera tutte le transazioni per un ordine
    public List<Transaction> getTransactionsByOrderId(Long orderId) {
        return transactionRepository.findByOrderId(orderId);
    }

    // Recupera tutte le transazioni per un pagamento
    public List<Transaction> getTransactionsByPaymentId(Long paymentId) {
        return transactionRepository.findByPaymentId(paymentId);
    }

    // Recupera tutte le transazioni con uno stato specifico
    public List<Transaction> getTransactionsByStatus(Transaction.TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }

    // Recupera transazioni con paginazione
    public Page<Transaction> getTransactionsByOrderId(Long orderId, Pageable pageable) {
        return transactionRepository.findByOrderId(orderId, pageable);
    }
}
