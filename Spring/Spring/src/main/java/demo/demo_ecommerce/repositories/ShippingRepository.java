package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Shipping;
import demo.demo_ecommerce.entities.Shipping.ShippingStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    // Trova tutte le spedizioni per stato (utilizzando l'enum)
    List<Shipping> findByStatus(ShippingStatus status);

    // Trova tutte le spedizioni per un determinato ordine
    List<Shipping> findByOrderId(Long orderId);

    // Paginazione: trova spedizioni per stato
    Page<Shipping> findByStatus(ShippingStatus status, Pageable pageable);

    // Paginazione: trova spedizioni per un ordine
    Page<Shipping> findByOrderId(Long orderId, Pageable pageable);

    // Filtra spedizioni per data prima o dopo
    List<Shipping> findByShippingDateBefore(LocalDateTime date);
    List<Shipping> findByShippingDateAfter(LocalDateTime date);
}
