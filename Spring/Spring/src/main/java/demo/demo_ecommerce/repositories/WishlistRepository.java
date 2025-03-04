package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;


import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {



    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.products WHERE w.user.id = :userId")
    Optional<Wishlist> findByUserIdWithProducts(@Param("userId") Long userId);


    // Verifica se una wishlist esiste per un determinato utente
    boolean existsByUserId(Long userId);

    // Trova tutte le wishlist che contengono un determinato prodotto
    List<Wishlist> findByProductsId(Long productId);

    // Elimina la wishlist associata a un determinato utente
    void deleteByUserId(Long userId);
}
