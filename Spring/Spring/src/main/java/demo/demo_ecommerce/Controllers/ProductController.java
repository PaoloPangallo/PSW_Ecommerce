package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Crea un nuovo prodotto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prodotto creato con successo", content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Errore nella validazione del prodotto")
    })
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

    @Operation(summary = "Recupera un prodotto tramite ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prodotto recuperato con successo", content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Prodotto non trovato")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.info("Fetching product with ID: {}", id);
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }



    @Operation(summary = "Aggiorna un prodotto tramite ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prodotto aggiornato con successo", content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Prodotto non trovato")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        logger.info("Updating product with ID: {}", id);
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Elimina un prodotto tramite ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Prodotto eliminato con successo"),
            @ApiResponse(responseCode = "404", description = "Prodotto non trovato")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Recupera tutti i prodotti o filtra per categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista di prodotti recuperata con successo")
    })
    @GetMapping // ✅ Unico metodo per gestire entrambe le situazioni
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String category) {
        logger.info("Fetching products" + (category != null ? " in category: " + category : ""));
        List<Product> products = (category != null) ? productService.getProductsByCategory(category) : productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}