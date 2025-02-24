package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Trova tutte le recensioni per un determinato prodotto
    List<Review> findByProductId(Long productId);

    // Trova tutte le recensioni per un determinato utente
    List<Review> findByUserId(Long userId);

    // Trova tutte le recensioni con paginazione
    Page<Review> findByProductId(Long productId, Pageable pageable);
    Page<Review> findByUserId(Long userId, Pageable pageable);

    // Trova tutte le recensioni ordinate per data
    List<Review> findByProductIdOrderByCreatedDateDesc(Long productId);

    // Conteggio delle recensioni per un prodotto
    long countByProductId(Long productId);

    // Trova recensioni con rating maggiore o uguale a un valore
    List<Review> findByProductIdAndRatingGreaterThanEqual(Long productId, int rating);
}
