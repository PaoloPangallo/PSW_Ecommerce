package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.ProductDTO;
import demo.demo_ecommerce.entities.Category;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.CategoryRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.Utility.ProductNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", unless = "#result == null or #result.isEmpty()")
    public List<Product> getAllProducts() {
        logger.info("Fetching all products from database");
        return enrichImages(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "#id", unless = "#result == null")
    public Product getProductById(Long id) {
        logger.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato con ID: " + id));
        enrichImage(product);
        return product;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id"),
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "productsByCategory", allEntries = true),
            @CacheEvict(value = "productsByCategoryPaged", allEntries = true),
            @CacheEvict(value = "productsPaged", allEntries = true)
    })
    public Product updateProduct(Long id, Product productDetails) {
        logger.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato con ID: " + id));

        validateProduct(productDetails);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setDiscountPercentage(productDetails.getDiscountPercentage());
        if (productDetails.getImageUrl() != null && !productDetails.getImageUrl().isEmpty()) {
            product.setImageUrl(productDetails.getImageUrl());
        }

        return productRepository.save(product);
    }

    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id"),
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "productsByCategory", allEntries = true),
            @CacheEvict(value = "productsByCategoryPaged", allEntries = true),
            @CacheEvict(value = "productsPaged", allEntries = true)
    })
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato con ID: " + id));
        productRepository.delete(product);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "productsByCategory", key = "#categoryName", unless = "#result == null or #result.isEmpty()")
    public List<Product> getProductsByCategory(String categoryName) {
        logger.info("Fetching products for category: {}", categoryName);
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Categoria non trovata: " + categoryName));
        return productRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "featuredProducts", unless = "#result == null or #result.isEmpty()")
    public List<Product> getFeaturedProducts() {
        logger.info("Fetching featured products");
        return productRepository.findByFeaturedTrue();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id"),
            @CacheEvict(value = "products", allEntries = true)
    })
    public Product updateProductImage(Long id, String imageUrl) {
        logger.info("Updating image for product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto non trovato con ID: " + id));
        product.setImageUrl(imageUrl);
        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product createProductFromDTO(ProductDTO dto) {
        logger.info("Creating product from DTO");
        if (productRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Product with this name already exists");
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setFeatured(dto.isFeatured());
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "productsByCategoryPaged", key = "#categoryName + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Product> getProductsByCategoryPaged(String categoryName, Pageable pageable) {
        logger.info("Fetching paged products for category: {}", categoryName);
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Categoria non trovata: " + categoryName));
        return productRepository.findByCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "productsPaged", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Product> getAllProductsPaged(Pageable pageable) {
        logger.info("Fetching all products paged");
        return productRepository.findAll(pageable);
    }

    private void validateProduct(Product product) {
        if (product.getName().length() < 3 || product.getName().length() > 50) {
            throw new IllegalArgumentException("Il nome del prodotto deve essere tra 3 e 50 caratteri.");
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Il prezzo deve essere maggiore di 0.");
        }
    }

    private List<Product> enrichImages(List<Product> products) {
        for (Product p : products) {
            enrichImage(p);
        }
        return products;
    }

    private void enrichImage(Product product) {
        if (product.getImageUrl() == null || product.getImageUrl().isEmpty()) {
            product.setImageUrl("https://source.unsplash.com/300x300/?electronics");
        }
    }
}
