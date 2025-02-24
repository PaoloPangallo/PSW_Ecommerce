package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithItems(Long userId);

    Page<Cart> findAll(Pageable pageable);


    Optional<Cart> findByUserId(Long userId);
}
