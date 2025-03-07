package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.ImageRequest;
import demo.demo_ecommerce.dtos.ImageUploadResponse;
import demo.demo_ecommerce.services.ImageGenerationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageGenerationService imageService;

    public ImageController(ImageGenerationService imageService) {
        this.imageService = imageService;
    }
    @PostMapping("/generate/{productId}")
    public ResponseEntity<?> generateImage(@PathVariable Long productId, @RequestBody ImageRequest request) {
        try {
            ImageUploadResponse response = imageService.generateProductImage(productId, request.getPrompt());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
