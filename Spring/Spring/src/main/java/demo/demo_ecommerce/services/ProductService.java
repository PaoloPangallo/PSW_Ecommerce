package demo.demo_ecommerce.services;
import demo.demo_ecommerce.repositories.CategoryRepository;


import demo.demo_ecommerce.Utility.ProductNotFoundException;
import demo.demo_ecommerce.entities.Category;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Crea un nuovo prodotto, assegnando un'immagine predefinita se non presente.
     */
    public Product createProduct(Product product) {
        if (productRepository.existsByName(product.getName())) {
            throw new IllegalArgumentException("Product with this name already exists");
        }

        // ✅ Se il prodotto non ha un'immagine, assegna una di default
        if (product.getImageUrl() == null || product.getImageUrl().isEmpty()) {
            product.setImageUrl("https://example.com/default-image.jpg"); // URL immagine predefinita
        }

        return productRepository.save(product);
    }

    private final CategoryRepository categoryRepository; // 🔹 Aggiungi il repository delle categorie


    /**
     * Ottieni un prodotto per ID.
     */
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            if (product.getImageUrl() == null || product.getImageUrl().isEmpty()) {
                product.setImageUrl("https://source.unsplash.com/300x300/?electronics");
            }
        }
        return products;
    }

    public Product getProductById(Long id) {
        logger.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato con ID: " + id));

        if (product.getImageUrl() == null || product.getImageUrl().isEmpty()) {
            product.setImageUrl("https://source.unsplash.com/300x300/?electronics");
        }

        return product;
    }

    /**
     * Aggiorna un prodotto, mantenendo l'immagine attuale se non viene fornita una nuova.
     */
    public Product updateProduct(Long id, Product productDetails) {
        logger.info("Updating product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato con ID: " + id));

        validateProduct(productDetails);
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setStock(productDetails.getStock());

        // ✅ Mantieni l'immagine esistente se il nuovo prodotto non ha un'immagine
        if (productDetails.getImageUrl() != null && !productDetails.getImageUrl().isEmpty()) {
            existingProduct.setImageUrl(productDetails.getImageUrl());
        }

        return productRepository.save(existingProduct);
    }

    /**
     * Elimina un prodotto dal database.
     */
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato con ID: " + id));
        productRepository.delete(product);
    }

    /**
     * Controlla se un prodotto esiste per ID.
     */
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }


    public List<Product> getProductsByCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Categoria non trovata: " + categoryName));
        return productRepository.findByCategory(category);
    }

    /**
     * Validazione del prodotto prima della creazione/aggiornamento.
     */
    private void validateProduct(Product product) {
        if (product.getName().length() < 3 || product.getName().length() > 50) {
            throw new IllegalArgumentException("Il nome del prodotto deve essere tra 3 e 50 caratteri.");
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Il prezzo deve essere maggiore di 0.");
        }
    }
}
