package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "orderItems")
    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);




    @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "orderItems.product.category"})
    List<Order> findByUserId(Long userId);

    // Metodo per ottenere l'ordine per ID e user (con join fetch per il dettaglio)
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findByIdAndUserIdFetchItems(@Param("orderId") Long orderId, @Param("userId") Long userId);

    // Nuovo metodo: restituisce gli ordini con join fetch degli orderItems e supporta la paginazione
    @Query(value = "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.user.id = :userId ORDER BY o.createdAt DESC",
            countQuery = "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Page<Order> findByUserIdFetchItems(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT SUM(o.total) FROM Order o WHERE o.user.id = :userId")
    BigDecimal sumTotalByUserId(@Param("userId") Long userId);

    int countByUserId(Long userId);


    // Altri metodi...
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.price > :price")
    List<OrderItem> findByOrderIdAndPriceGreaterThan(Long orderId, BigDecimal price);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Integer findTotalQuantityByOrderId(Long orderId);

    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
    BigDecimal findTotalPriceByOrderId(Long orderId);

    @EntityGraph(attributePaths = {"orderItems"})
    List<Order> findAll();  // ora funzionante


    @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "coupon", "user"})
    Optional<Order> findByUserIdAndId(Long userId, Long orderId);
}
