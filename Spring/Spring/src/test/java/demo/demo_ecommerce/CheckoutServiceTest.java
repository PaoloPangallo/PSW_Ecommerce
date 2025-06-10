package demo.demo_ecommerce;

import demo.demo_ecommerce.dtos.OrderDTO;
import demo.demo_ecommerce.dtos.ShippingDTO;
import demo.demo_ecommerce.dtos.TransactionDTO;
import demo.demo_ecommerce.entities.*;
import demo.demo_ecommerce.repositories.*;
import demo.demo_ecommerce.services.CheckoutService;
import demo.demo_ecommerce.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutServiceTest {

    @InjectMocks
    private CheckoutService checkoutService;

    @Mock private OrderRepository orderRepository;
    @Mock private UsersRepository userRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private ShippingRepository shippingRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderService orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


}
