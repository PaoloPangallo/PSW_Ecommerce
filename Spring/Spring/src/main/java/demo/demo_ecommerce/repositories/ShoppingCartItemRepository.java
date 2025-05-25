package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {
    Optional<ShoppingCartItem> findByCartIdAndProductId(Long cartId, Long productId);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ShoppingCartItem i WHERE i.id = :id")
    void deleteByIdWithoutVersion(@Param("id") Long id);



    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ShoppingCartItem i WHERE i.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);


}
