package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {



    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.products WHERE w.user.id = :userId")
    Optional<Wishlist> findByUserIdWithProducts(@Param("userId") Long userId);

    int countByUserId(Long userId);







    // Verifica se una wishlist esiste per un determinato utente
    boolean existsByUserId(Long userId);

}
