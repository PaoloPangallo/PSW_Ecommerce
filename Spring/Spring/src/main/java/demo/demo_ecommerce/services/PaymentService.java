package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.PaymentNotFoundException;
import demo.demo_ecommerce.entities.Payment;
import demo.demo_ecommerce.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Creazione di un nuovo pagamento
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Recupero di un pagamento per ID
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + id));
    }

    // Recupero di tutti i pagamenti
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Aggiornamento dello stato di un pagamento
    public Payment updatePaymentStatus(Long id, String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status. Valid values are: 'PENDING', 'COMPLETED', 'FAILED'");
        }

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + id));
        payment.setStatus(Payment.PaymentStatus.valueOf(status));
        return paymentRepository.save(payment);
    }

    // Eliminazione di un pagamento
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new PaymentNotFoundException("Payment not found with ID: " + id);
        }
        paymentRepository.deleteById(id);
    }

    // Metodo di validazione dello stato del pagamento
    private boolean isValidStatus(String status) {
        try {
            Payment.PaymentStatus.valueOf(status.toUpperCase()); // Enum validation
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
