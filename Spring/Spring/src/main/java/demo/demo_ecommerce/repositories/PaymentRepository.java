package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Trova i pagamenti per stato
    List<Payment> findByStatus(String status);

    // Trova i pagamenti per ordine
    List<Payment> findByOrderId(Long orderId);

    // Query personalizzata per trovare pagamenti con stato e importo maggiore di un certo valore
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.amount > :minAmount")
    List<Payment> findPaymentsByStatusAndAmountGreaterThan(@Param("status") String status, @Param("minAmount") Double minAmount);
}
