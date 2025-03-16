package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.ResourceNotFoundException;
import demo.demo_ecommerce.Utility.ReviewLimitExceededException;
import demo.demo_ecommerce.dtos.ReviewDTO;
import demo.demo_ecommerce.entities.Review;
import demo.demo_ecommerce.repositories.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // Crea una nuova recensione
    public ReviewDTO createReview(Review review) {
        if (review.getProduct() == null || review.getUser() == null) {
            throw new IllegalArgumentException("Product and User must not be null");
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        int existingReviewsCount = reviewRepository.countByUserIdAndProductId(
                review.getUser().getId(),
                review.getProduct().getId()
        );

        // Se l'utente ha già lasciato una recensione per questo prodotto, lanciamo l'eccezione custom
        if (existingReviewsCount >= 1) {
            throw new ReviewLimitExceededException("Hai già lasciato una recensione per questo prodotto!");
        }

        logger.info("Creating review for product ID: {} and user ID: {}",
                review.getProduct().getId(), review.getUser().getId());
        Review savedReview = reviewRepository.save(review);
        return ReviewDTO.fromEntity(savedReview);
    }

    // Recupera tutte le recensioni di un prodotto con paginazione
    public Page<ReviewDTO> getReviewsByProductId(Long productId, int page, int size) {
        logger.info("Fetching reviews for product ID: {} with pagination (page={}, size={})", productId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);

        // Se non ci sono recensioni, restituiamo una pagina vuota
        if (reviews.isEmpty()) {
            return Page.empty(pageable);
        }
        return reviews.map(ReviewDTO::fromEntity);
    }

    // Recupera tutte le recensioni di un utente con paginazione
    public Page<ReviewDTO> getReviewsByUserId(Long userId, int page, int size) {
        logger.info("Fetching reviews for user ID: {} with pagination (page={}, size={})", userId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findByUserId(userId, pageable);

        // Se non ci sono recensioni, restituiamo una pagina vuota
        if (reviews.isEmpty()) {
            return Page.empty(pageable);
        }
        return reviews.map(ReviewDTO::fromEntity);
    }

    // Recupera tutte le recensioni di un prodotto senza paginazione
    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllReviewsByProductId(Long productId) {
        logger.info("Fetching all reviews for product ID: {}", productId);
        List<Review> reviews = reviewRepository.findByProductId(productId);

        // Se non ci sono recensioni, restituiamo una lista vuota
        if (reviews.isEmpty()) {
            return List.of();
        }
        // Forza l'inizializzazione della collection upvotes per ogni review
        reviews.forEach(review -> review.getUpvotes().size());
        return reviews.stream().map(ReviewDTO::fromEntity).toList();
    }

    // Metodo per aggiornare una recensione esistente
    public ReviewDTO updateReview(Long reviewId, Review reviewDetails) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + reviewId));

        existingReview.setRating(reviewDetails.getRating());
        existingReview.setComment(reviewDetails.getComment());

        Review updatedReview = reviewRepository.save(existingReview);
        return ReviewDTO.fromEntity(updatedReview);
    }

    // Metodo per cancellare una recensione, controllando che appartenga all'utente corrente
    public void deleteReview(Long reviewId, Long currentUserId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + reviewId));
        if (!review.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Non hai il permesso di eliminare questa recensione.");
        }
        reviewRepository.delete(review);
    }
}
