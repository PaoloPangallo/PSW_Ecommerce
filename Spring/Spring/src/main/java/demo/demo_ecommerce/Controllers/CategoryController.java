package demo.demo_ecommerce.Controllers;


import demo.demo_ecommerce.entities.Category;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Creazione di una nuova categoria
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);  // HTTP 201 CREATED
    }

    // Ottieni tutte le categorie
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Ottieni una categoria per ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Gestione errore
        }
    }

    // Recupera i prodotti di una categoria
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<Page<Product>> getProductsByCategory(@PathVariable Long categoryId, Pageable pageable) {
        Page<Product> products = categoryService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }
}

