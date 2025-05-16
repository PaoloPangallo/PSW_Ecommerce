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

    @Test
    void testProcessCheckout_SuccessfulFlow() {
        Long userId = 1L;
        User user = new User(); user.setId(userId); user.setUsername("paolo");

        // Transaction input
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(BigDecimal.valueOf(100));
        transactionDTO.setStatus("COMPLETED");
        transactionDTO.setPaymentMethod("CARD");

        // Shipping input
        ShippingDTO shippingDTO = new ShippingDTO();
        shippingDTO.setAddress("Via Roma");
        shippingDTO.setCity("Milano");
        shippingDTO.setCountry("Italia");
        shippingDTO.setZipCode("20100");
        shippingDTO.setShippingMethod(Order.ShippingMethod.EXPRESS);
        shippingDTO.setStatus("DELIVERING");

        // Ordine simulato
        OrderDTO orderDTO = new OrderDTO(); orderDTO.setId(42L);
        Order order = new Order(); order.setId(42L); order.setTotal(BigDecimal.valueOf(100));
        order.setStatus(Order.OrderStatus.SHIPPED);

        Payment payment = new Payment(); payment.setPaymentMethod("CARD");

        // Mocking
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderService.createOrder(userId, Order.ShippingMethod.EXPRESS)).thenReturn(orderDTO);
        when(orderRepository.findById(orderDTO.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.findByPaymentMethod("CARD")).thenReturn(payment);
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Esecuzione
        Order result = checkoutService.processCheckout(userId, transactionDTO, shippingDTO);

        // Verifica
        assertNotNull(result);
        assertEquals(Order.OrderStatus.PAID, result.getStatus());
        verify(transactionRepository).save(any(Transaction.class));
        verify(shippingRepository).save(any(Shipping.class));
        verify(orderRepository, times(2)).save(any(Order.class)); // ordine salvato all’inizio e alla fine
    }

    @Test
    void testProcessCheckout_InvalidPayment_ThrowsException() {
        Long userId = 1L;
        User user = new User(); user.setId(userId);

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(BigDecimal.valueOf(50));
        transactionDTO.setStatus("COMPLETED");
        transactionDTO.setPaymentMethod("INVALID");

        ShippingDTO shippingDTO = new ShippingDTO();

        OrderDTO orderDTO = new OrderDTO(); orderDTO.setId(99L);
        Order order = new Order(); order.setId(99L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderService.createOrder(eq(userId), any())).thenReturn(orderDTO);
        when(orderRepository.findById(orderDTO.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.findByPaymentMethod("INVALID")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class,
                () -> checkoutService.processCheckout(userId, transactionDTO, shippingDTO));

        assertTrue(exception.getMessage().contains("Metodo di pagamento non valido"));
    }
}
