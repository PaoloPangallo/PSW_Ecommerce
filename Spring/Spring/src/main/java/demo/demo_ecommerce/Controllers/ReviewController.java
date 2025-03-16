package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.Utility.ResourceNotFoundException;
import demo.demo_ecommerce.Utility.ReviewLimitExceededException;
import demo.demo_ecommerce.dtos.ReviewDTO;
import demo.demo_ecommerce.entities.Review;
import demo.demo_ecommerce.repositories.ReviewRepository;
import demo.demo_ecommerce.services.ReviewService;

import demo.demo_ecommerce.services.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final UsersService userService; // Servizio per recuperare l'utente corrente

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository, UsersService userService) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
        this.userService = userService;
    }

    // Qualsiasi utente autenticato può creare una recensione
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody Review review) {
        try {
            ReviewDTO createdReview = reviewService.createReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (IllegalStateException | ReviewLimitExceededException e) {
            // Se l'utente ha già lasciato la recensione o ha superato il limite, restituiamo un 400 (Bad Request)
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Recupera tutte le recensioni di un prodotto con paginazione (accesso pubblico)
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
    // Permette l'accesso se l'utente autenticato è ADMIN o se l'ID utente passato corrisponde a quello dell'utente autenticato
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.info("Fetching reviews for user ID: {} with pagination (page={}, size={})", userId, page, size);
        Page<ReviewDTO> reviews = reviewService.getReviewsByUserId(userId, page, size);
        return ResponseEntity.ok(reviews);
    }

    // Permette l'aggiornamento della recensione solo se l'utente è ADMIN o se è l'autore della recensione
    @PreAuthorize("hasRole('ADMIN') or @reviewService.isReviewOwner(#reviewId, principal.username)")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody Review reviewDetails) {

        logger.info("Updating review with ID: {}", reviewId);
        ReviewDTO updatedReview = reviewService.updateReview(reviewId, reviewDetails);
        return ResponseEntity.ok(updatedReview);
    }

    // Recupera tutte le recensioni di un prodotto senza paginazione (accesso pubblico)
    @GetMapping("/product/{productId}/all")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsByProductId(@PathVariable Long productId) {
        logger.info("Fetching all reviews for product ID: {}", productId);
        List<ReviewDTO> reviews = reviewService.getAllReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    // Permette la cancellazione di una recensione solo se l'utente è ADMIN o è l'autore della recensione
    @PreAuthorize("hasRole('ADMIN') or @reviewService.isReviewOwner(#reviewId, principal.username)")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId, Principal principal) {
        // Dal momento che il controllo @PreAuthorize verifica che l'utente sia il proprietario o ADMIN,
        // possiamo procedere direttamente alla cancellazione.
        reviewService.deleteReview(reviewId, null); // Il service può ignorare il controllo dell'utente se già validato
        return ResponseEntity.noContent().build();
    }
}
