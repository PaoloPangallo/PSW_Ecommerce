package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Review;
import demo.demo_ecommerce.entities.Upvote;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.ReviewRepository;
import demo.demo_ecommerce.repositories.UpvoteRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpvoteService {

    @Autowired
    private UpvoteRepository upvoteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UsersRepository userRepository;

    @Transactional
    public void addUpvote(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Controlla se l'utente ha già votato la recensione
        boolean alreadyUpvoted = upvoteRepository.existsByReviewAndUser(review, user);
        if (alreadyUpvoted) {
            throw new IllegalArgumentException("User has already upvoted this review.");
        }

        Upvote upvote = Upvote.builder()
                .review(review)
                .user(user)
                .build();

        upvoteRepository.save(upvote);
    }

    @Transactional
    public void removeUpvote(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Upvote upvote = upvoteRepository.findByReviewAndUser(review, user)
                .orElseThrow(() -> new IllegalArgumentException("Upvote not found for review ID " + reviewId + " and user ID " + userId));

        upvoteRepository.delete(upvote);
    }

    @Transactional(readOnly = true)
    public int countUpvotes(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));
        return upvoteRepository.countByReview(review);
    }
}
