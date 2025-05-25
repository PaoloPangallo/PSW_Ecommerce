package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Trova tutti gli order items per un determinato ordine
    List<OrderItem> findByOrderId(Long orderId);

    // Trova tutti gli order items per un determinato prodotto
    List<OrderItem> findByProductId(Long productId);

    // Aggregazioni: Somma delle quantità per un ordine
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Integer findTotalQuantityByOrderId(Long orderId);

    // Aggregazioni: Totale del prezzo per un ordine
    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    BigDecimal findTotalPriceByOrderId(Long orderId);

    @Modifying
    @Query("DELETE FROM OrderItem o WHERE o.id = :id")
    void deleteByIdWithoutVersion(@Param("id") Long id);

}
