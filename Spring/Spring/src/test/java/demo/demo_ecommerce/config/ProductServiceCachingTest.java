package demo.demo_ecommerce.config;

import demo.demo_ecommerce.entities.Category;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.CategoryRepository;
import demo.demo_ecommerce.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.cache.type=caffeine"
})
public class ProductServiceCachingTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void getProductById_shouldUseCacheAfterFirstCall() {
        Long productId = 42L;

        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setName("Test Product");
        mockProduct.setPrice(BigDecimal.valueOf(10));
        mockProduct.setImageUrl("");

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        // Prima chiamata => dal DB
        Product p1 = productService.getProductById(productId);
        // Seconda chiamata => dalla cache
        Product p2 = productService.getProductById(productId);

        assertEquals(p1, p2);
        verify(productRepository, times(1)).findById(productId); // ✅ una sola chiamata
    }

    @Test
    void updateProduct_shouldEvictCache() {
        Long productId = 99L;

        Product existing = new Product();
        existing.setId(productId);
        existing.setName("Old Name");
        existing.setPrice(BigDecimal.valueOf(20));
        existing.setStock(5);
        existing.setDiscountPercentage(0); // Integer

        Product updated = new Product();
        updated.setName("Updated Name");
        updated.setPrice(BigDecimal.valueOf(25));
        updated.setDescription("Updated Desc");
        updated.setStock(10);
        updated.setDiscountPercentage(15); // Integer

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArguments()[0]);

        productService.getProductById(productId); // Popola la cache
        productService.updateProduct(productId, updated); // Deve invalidare la cache

        // Adesso nuova chiamata dovrebbe rientrare nel repository
        when(productRepository.findById(productId)).thenReturn(Optional.of(updated));
        Product p3 = productService.getProductById(productId);

        assertEquals("Updated Name", p3.getName());
        verify(productRepository, atLeastOnce()).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getAllProducts_shouldCacheResult() {
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);

        // Primo fetch: cache miss
        List<Product> result1 = productService.getAllProducts();
        // Secondo fetch: dovrebbe essere cache hit
        List<Product> result2 = productService.getAllProducts();

        // Verifica che il repository sia stato chiamato solo una volta
        verify(productRepository, times(1)).findAll();
        assertThat(result1).isEqualTo(result2);
    }


    @Test
    void getProductsByCategoryPaged_shouldCacheResult() {
        // Arrange
        String categoryName = "Libri";
        Pageable pageable = PageRequest.of(0, 10);

        Category category = new Category();
        category.setName(categoryName);

        Product product = new Product();
        product.setId(1L);
        product.setName("Libro test");
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(productRepository.findByCategory(category, pageable)).thenReturn(productPage);

        // Act
        Page<Product> result1 = productService.getProductsByCategoryPaged(categoryName, pageable);
        Page<Product> result2 = productService.getProductsByCategoryPaged(categoryName, pageable); // da cache

        // Assert
        assertEquals(productPage, result1);
        assertEquals(productPage, result2);

        // Verifica che productRepository sia stato chiamato una sola volta (la seconda è cache)
        verify(productRepository, times(1)).findByCategory(category, pageable);
        verify(categoryRepository, times(1)).findByName(categoryName);
    }



}