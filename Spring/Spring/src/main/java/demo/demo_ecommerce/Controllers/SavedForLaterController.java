package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.entities.SavedForLaterItem;
import demo.demo_ecommerce.services.SavedForLaterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved")
public class SavedForLaterController {

    private final SavedForLaterService savedService;

    public SavedForLaterController(SavedForLaterService savedService) {
        this.savedService = savedService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> saveForLater(@RequestParam Long userId,
                                                            @RequestParam Long productId,
                                                            @RequestParam int quantity) {
        savedService.saveForLater(userId, productId, quantity);
        return ResponseEntity.ok(Map.of("message", "Elemento salvato correttamente"));
    }


    @GetMapping("/{userId}")
    public ResponseEntity<List<SavedForLaterItem>> getSaved(@PathVariable Long userId) {
        return ResponseEntity.ok(savedService.getSavedItems(userId));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeSaved(@RequestParam Long userId,
                                                           @RequestParam Long productId) {
        savedService.removeSavedItem(userId, productId);
        return ResponseEntity.ok(Map.of("message", "Elemento rimosso dai salvati"));
    }
}
