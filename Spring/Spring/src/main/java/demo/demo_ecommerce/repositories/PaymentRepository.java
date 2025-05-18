package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Trova i pagamenti per stato
    List<Payment> findByStatus(String status);



    // Metodo aggiornato per cercare in base al campo "paymentMethod"
    Payment findByPaymentMethod(String paymentMethod);
}
