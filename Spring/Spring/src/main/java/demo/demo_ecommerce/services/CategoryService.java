package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Category;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.CategoryRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional // Sovrascrive readOnly = true e abilita la scrittura
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Cacheable("categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Cacheable(value = "category", key = "#id")
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // ⚠️ Opzionale: caching paginato richiede chiavi dinamiche uniche per page
    @Cacheable(value = "productsByCategoryPaged", key = "#categoryId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }
}
