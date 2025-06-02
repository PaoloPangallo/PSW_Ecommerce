package demo.demo_ecommerce;



import demo.demo_ecommerce.Controllers.PaymentController;
import demo.demo_ecommerce.dtos.PaymentRequestDTO;
import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.Payment;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.services.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private demo.demo_ecommerce.repositories.UsersRepository usersRepository;

    @MockBean
    private demo.demo_ecommerce.repositories.OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;
    private Order mockOrder;
    private Payment mockPayment;

    @BeforeEach
    public void setup() {
        mockUser = new User();
        mockUser.setId(1L);

        mockOrder = new Order();
        mockOrder.setId(10L);

        mockPayment = Payment.builder()
                .id(100L)
                .user(mockUser)
                .order(mockOrder)
                .paymentMethod("CREDIT_CARD")
                .amount(new Double("99.99"))
                .status(Payment.PaymentStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    public void testCreatePayment_Success() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setOrderId(10L);
        requestDTO.setPaymentMethod("CREDIT_CARD");
        requestDTO.setAmount(new Double("99.99"));

        Mockito.when(usersRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Mockito.when(orderRepository.findById(10L)).thenReturn(Optional.of(mockOrder));
        Mockito.when(paymentService.createPayment(any(Payment.class))).thenReturn(mockPayment);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.orderId", is(10)))
                .andExpect(jsonPath("$.paymentMethod", is("CREDIT_CARD")))
                .andExpect(jsonPath("$.amount", is(99.99)))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }
}

