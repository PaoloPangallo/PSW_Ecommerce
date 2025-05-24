package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.ShoppingCartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;


public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {
    Optional<ShoppingCartItem> findByCartIdAndProductId(Long cartId, Long productId);


    List<ShoppingCartItem> findByCartId(Long cartId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ShoppingCartItem i WHERE i.id = :id")
    void deleteByIdWithoutVersion(@Param("id") Long id);



    @Modifying
    @Query("DELETE FROM ShoppingCartItem i WHERE i.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);

}
