package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.ReviewDTO;
import demo.demo_ecommerce.entities.Review;
import demo.demo_ecommerce.services.ReviewService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Creazione di una nuova recensione
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody Review review) {
        logger.info("Creating new review for product ID: {} and user ID: {}",
                review.getProduct().getId(), review.getUser().getId());

        ReviewDTO createdReview = reviewService.createReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    // Recupera tutte le recensioni di un prodotto con paginazione
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.info("Fetching reviews for product ID: {} with pagination (page={}, size={})", productId, page, size);

        Page<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId, page, size);
        return ResponseEntity.ok(reviews);
    }

    // Recupera tutte le recensioni di un utente con paginazione
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.info("Fetching reviews for user ID: {} with pagination (page={}, size={})", userId, page, size);

        Page<ReviewDTO> reviews = reviewService.getReviewsByUserId(userId, page, size);
        return ResponseEntity.ok(reviews);
    }

    // Recupera tutte le recensioni di un prodotto senza paginazione
    @GetMapping("/product/{productId}/all")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsByProductId(@PathVariable Long productId) {
        logger.info("Fetching all reviews for product ID: {}", productId);
        List<ReviewDTO> reviews = reviewService.getAllReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }
}
