package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.services.UpvoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upvotes")
public class UpvoteController {

    @Autowired
    private UpvoteService upvoteService;

    // Endpoint per aggiungere un upvote a una recensione
    @PostMapping("/review/{reviewId}/user/{userId}")
    public ResponseEntity<Map<String, String>> addUpvote(@PathVariable Long reviewId, @PathVariable Long userId) {
        Map<String, String> response = new HashMap<>();
        try {
            upvoteService.addUpvote(reviewId, userId);
            response.put("message", "Upvote added successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (DataIntegrityViolationException ex) {
            response.put("message", "User has already upvoted this review.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception ex) {
            response.put("message", "Unexpected error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoint per rimuovere un upvote
    @DeleteMapping("/review/{reviewId}/user/{userId}")
    public ResponseEntity<Map<String, String>> removeUpvote(@PathVariable Long reviewId, @PathVariable Long userId) {
        Map<String, String> response = new HashMap<>();
        upvoteService.removeUpvote(reviewId, userId);
        response.put("message", "Upvote removed successfully.");
        return ResponseEntity.ok(response);
    }
}
