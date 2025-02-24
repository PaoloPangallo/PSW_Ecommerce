package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Trova tutti gli order items per un determinato ordine
    List<OrderItem> findByOrderId(Long orderId);

    // Trova tutti gli order items per un determinato prodotto
    List<OrderItem> findByProductId(Long productId);

    // Supporto alla paginazione
    Page<OrderItem> findByOrderId(Long orderId, Pageable pageable);

    // Supporto all'ordinamento
    List<OrderItem> findByOrderId(Long orderId, Sort sort);

    // Query personalizzata: Trovare OrderItems con prezzo superiore
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.price > :price")
    List<OrderItem> findByOrderIdAndPriceGreaterThan(Long orderId, BigDecimal price);

    // Aggregazioni: Somma delle quantità per un ordine
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Integer findTotalQuantityByOrderId(Long orderId);

    // Aggregazioni: Totale del prezzo per un ordine
    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    BigDecimal findTotalPriceByOrderId(Long orderId);
}
