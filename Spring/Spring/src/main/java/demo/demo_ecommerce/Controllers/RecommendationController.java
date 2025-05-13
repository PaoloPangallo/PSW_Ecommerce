package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.services.ProductRecommendationService;
import demo.demo_ecommerce.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/for-product/{productId}")
    public ResponseEntity<List<Product>> getRecommendationsForProduct(@PathVariable Long productId) {
        List<Product> recommended = recommendationService.recommendProductsForProduct(productId);
        return ResponseEntity.ok(recommended);
    }

    @GetMapping("/for-user/{userId}")
    public ResponseEntity<List<Product>> getRecommendationsForUser(@PathVariable Long userId) {
        List<Product> recommended = recommendationService.recommendProductsForUser(userId);
        return ResponseEntity.ok(recommended);
    }

    private final ProductRecommendationService productRecommendationService;

    @GetMapping("/also-bought/{productId}")
    public ResponseEntity<List<Product>> getAlsoBoughtProducts(@PathVariable Long productId) {
        List<Product> recommendations = productRecommendationService.recommendProductsFor(productId);
        return ResponseEntity.ok(recommendations);
    }


}