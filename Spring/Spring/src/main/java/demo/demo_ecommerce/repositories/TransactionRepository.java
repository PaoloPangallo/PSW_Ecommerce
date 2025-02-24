package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Trova tutte le transazioni per un ordine specifico
    List<Transaction> findByOrderId(Long orderId);

    // Trova tutte le transazioni per un ordine con paginazione
    Page<Transaction> findByOrderId(Long orderId, Pageable pageable);

    // Trova tutte le transazioni per un pagamento specifico
    List<Transaction> findByPaymentId(Long paymentId);

    // Trova tutte le transazioni per uno stato specifico
    List<Transaction> findByStatus(Transaction.TransactionStatus status);

    // Trova tutte le transazioni con stato diverso da SUCCESS
    List<Transaction> findByStatusNot(Transaction.TransactionStatus status);

    // Conta il numero di transazioni per uno stato specifico
    long countByStatus(Transaction.TransactionStatus status);

    // Transazioni dopo una certa data
    List<Transaction> findByTransactionDateAfter(LocalDateTime date);

    // Transazioni ordinate per data
    List<Transaction> findByOrderId(Long orderId, Sort sort);

    // Transazioni con importo maggiore di un valore
    @Query("SELECT t FROM Transaction t WHERE t.order.id = :orderId AND t.amount > :amount")
    List<Transaction> findByOrderIdAndAmountGreaterThan(Long orderId, Double amount);
}
