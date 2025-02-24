package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.entities.Payment;
import demo.demo_ecommerce.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Creazione di un nuovo pagamento
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        Payment createdPayment = paymentService.createPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment); // HTTP 201 Created
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);  // Chiama direttamente il servizio
        if (payment != null) {
            return ResponseEntity.ok(payment);  // Se trovato, restituisci OK
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Payment not found with ID: " + id);  // Se non trovato, restituisci errore 404
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
