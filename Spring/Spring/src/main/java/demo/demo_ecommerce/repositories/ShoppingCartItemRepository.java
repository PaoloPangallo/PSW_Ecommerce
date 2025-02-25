package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {
    Optional<ShoppingCartItem> findByCartIdAndProductId(Long cartId, Long productId);
    void deleteAllByCartId(Long cartId);
}
