package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.services.UpvoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/upvotes")
public class UpvoteController {

    @Autowired
    private UpvoteService upvoteService;

    // Endpoint per aggiungere un upvote a una recensione
    @PostMapping("/review/{reviewId}/user/{userId}")
    public ResponseEntity<String> addUpvote(@PathVariable Long reviewId, @PathVariable Long userId) {
        try {
            upvoteService.addUpvote(reviewId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Upvote added successfully.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }


    // Endpoint per rimuovere un upvote
    @DeleteMapping("/review/{reviewId}/user/{userId}")
    public ResponseEntity<String> removeUpvote(@PathVariable Long reviewId, @PathVariable Long userId) {
        upvoteService.removeUpvote(reviewId, userId);
        return ResponseEntity.ok("Upvote removed successfully.");
    }
}
