package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.ProductDTO;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.services.FirebaseStorageService;
import demo.demo_ecommerce.services.ProductService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final FirebaseStorageService firebaseStorageService;

    public ProductController(ProductService productService, FirebaseStorageService firebaseStorageService) {
        this.productService = productService;
        this.firebaseStorageService = firebaseStorageService;
    }

    // -----------------------------------------------------------
    //                  METODI DI LETTURA (PUBBLICI)
    // -----------------------------------------------------------

    // GET /api/products - Recupera tutti i prodotti (o filtra per categoria)
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String category) {
        logger.info("Fetching products" + (category != null ? " in category: " + category : ""));
        List<Product> products = (category != null)
                ? productService.getProductsByCategory(category)
                : productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // GET /api/products/{id} - Recupera un prodotto specifico
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.info("Fetching product with ID: {}", id);
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // GET /api/products/featured - Recupera prodotti in evidenza
    @GetMapping("/featured")
    public List<ProductDTO> getFeaturedProducts() {
        return productService.getFeaturedProducts().stream()
                .map(ProductDTO::fromEntity)
                .toList();
    }

    // -----------------------------------------------------------
    //             METODI DI SCRITTURA (RISERVATI ADMIN)
    // -----------------------------------------------------------

    // POST /api/products - Crea un nuovo prodotto (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product) {
        try {
            logger.info("Creating product: {}", product.getName());
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // PUT /api/products/{id} - Aggiorna un prodotto (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        logger.info("Updating product with ID: {}", id);
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    // DELETE /api/products/{id} - Elimina un prodotto (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/products/{id}/uploadImage - Carica un'immagine prodotto (solo ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/uploadImage")
    public ResponseEntity<Product> uploadProductImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = firebaseStorageService.uploadFile(file);
            Product updatedProduct = productService.updateProductImage(id, imageUrl);
            return ResponseEntity.ok(updatedProduct);
        } catch (IOException e) {
            logger.error("Errore durante l'upload dell'immagine", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
