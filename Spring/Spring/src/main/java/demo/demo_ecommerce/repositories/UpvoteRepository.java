package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Review;
import demo.demo_ecommerce.entities.Upvote;
import demo.demo_ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UpvoteRepository extends JpaRepository<Upvote, Long> {
    boolean existsByReviewAndUser(Review review, User user);
    Optional<Upvote> findByReviewAndUser(Review review, User user);
    int countByReview(Review review);
}
