package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.services.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ utile se accedi da frontend Angular
public class SearchController {

    private final ProductSearchService searchService;

    @GetMapping
    public List<Product> search(@RequestParam String query) {
        return searchService.fuzzySearch(query);
    }
}
