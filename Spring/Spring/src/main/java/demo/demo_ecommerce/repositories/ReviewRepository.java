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

    // Trova tutte le recensioni con paginazione
    Page<Review> findByProductId(Long productId, Pageable pageable);
    Page<Review> findByUserId(Long userId, Pageable pageable);

    int countByUserIdAndProductId(Long userId, Long productId);


}
