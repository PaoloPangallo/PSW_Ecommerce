package demo.demo_ecommerce.services;

import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.PaymentRepository;
import demo.demo_ecommerce.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

}
