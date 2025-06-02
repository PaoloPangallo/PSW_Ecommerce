package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.Utility.ResourceNotFoundException;
import demo.demo_ecommerce.dtos.PaymentRequestDTO;
import demo.demo_ecommerce.dtos.PaymentResponseDTO;
import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.Payment;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import demo.demo_ecommerce.services.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final UsersRepository userRepository;
    private final OrderRepository orderRepository;

    public PaymentController(PaymentService paymentService,
                             UsersRepository userRepository,
                             OrderRepository orderRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {

        User user = userRepository.findById(paymentRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + paymentRequestDTO.getUserId()));

        Order order = orderRepository.findById(paymentRequestDTO.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + paymentRequestDTO.getOrderId()));

        Payment payment = Payment.builder()
                .user(user)
                .order(order)
                .paymentMethod(paymentRequestDTO.getPaymentMethod())
                .amount(paymentRequestDTO.getAmount())
                .status(Payment.PaymentStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .build();

        Payment createdPayment = paymentService.createPayment(payment);

        PaymentResponseDTO responseDTO = PaymentResponseDTO.fromEntity(createdPayment);

        logger.info("Created payment with ID: {}", createdPayment.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found with ID: " + id);
        }
        return ResponseEntity.ok(PaymentResponseDTO.fromEntity(payment));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentResponseDTO> responseList = payments.stream()
                .map(PaymentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        if (!isValidStatus(status)) {
            return ResponseEntity.badRequest()
                    .body(null); // oppure potresti restituire un DTO errore più strutturato
        }
        Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
        if (updatedPayment == null) {
            throw new ResourceNotFoundException("Payment not found with ID: " + id);
        }
        return ResponseEntity.ok(PaymentResponseDTO.fromEntity(updatedPayment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isValidStatus(String status) {
        try {
            Payment.PaymentStatus.valueOf(status.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
