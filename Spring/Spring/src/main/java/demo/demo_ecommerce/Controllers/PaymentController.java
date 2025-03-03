package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.PaymentRequestDTO;
import demo.demo_ecommerce.dtos.PaymentResponseDTO;
import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.Payment;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import demo.demo_ecommerce.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Aggiungi i repository necessari per recuperare l'utente e l'ordine
    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Creazione di un nuovo pagamento
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@RequestBody @Valid PaymentRequestDTO paymentRequestDTO) {

        // 1) Recupera l'utente dal database
        User user = userRepository.findById(paymentRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with ID: " + paymentRequestDTO.getUserId()
                ));

        // 2) Recupera l'ordine dal database
        Order order = orderRepository.findById(paymentRequestDTO.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with ID: " + paymentRequestDTO.getOrderId()
                ));

        // 3) Mappa i campi dal DTO all’entity Payment
        Payment payment = Payment.builder()
                .user(user)
                .order(order)
                .paymentMethod(paymentRequestDTO.getPaymentMethod())
                .amount(paymentRequestDTO.getAmount())
                .status(Payment.PaymentStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .build();

        // Salva il pagamento
        Payment createdPayment = paymentService.createPayment(payment);

        // 4) Crea un PaymentResponseDTO per restituirlo al client
        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        responseDTO.setId(createdPayment.getId());
        responseDTO.setUserId(createdPayment.getUser().getId());
        responseDTO.setOrderId(createdPayment.getOrder().getId());
        responseDTO.setPaymentMethod(createdPayment.getPaymentMethod());
        responseDTO.setAmount(createdPayment.getAmount());
        responseDTO.setStatus(createdPayment.getStatus().toString());
        responseDTO.setTimestamp(createdPayment.getTimestamp());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // Recupera un pagamento per ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Payment not found with ID: " + id);
        }
    }

    // Recupero di tutti i pagamenti
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // Aggiornamento dello stato di un pagamento
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        // Validazione dello stato
        if (!isValidStatus(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid status. Valid values are: 'PENDING', 'COMPLETED', 'FAILED'");
        }

        try {
            Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
            return ResponseEntity.ok(updatedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Payment not found with ID: " + id);
        }
    }

    // Eliminazione di un pagamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Metodo per validare lo stato del pagamento
    private boolean isValidStatus(String status) {
        try {
            Payment.PaymentStatus.valueOf(status.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
