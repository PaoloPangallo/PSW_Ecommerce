package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Trova gli ordini per un determinato utente
    Page<Order> findByUserId(Long userId, Pageable pageable);


    // Trova un ordine per un determinato ID e un determinato ID utente
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    // Trova gli ordini creati dopo una certa data
    Page<Order> findByCreatedAtAfter(LocalDateTime date, Pageable pageable);


    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.price > :price")
    List<OrderItem> findByOrderIdAndPriceGreaterThan(Long orderId, BigDecimal price);


    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Integer findTotalQuantityByOrderId(Long orderId);

    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    BigDecimal findTotalPriceByOrderId(Long orderId);
}
