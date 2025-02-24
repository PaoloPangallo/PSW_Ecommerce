package demo.demo_ecommerce.services;


import demo.demo_ecommerce.entities.Category;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.CategoryRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // Crea una nuova categoria
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Ottieni tutte le categorie
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Ottieni una categoria per ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // Trova i prodotti di una categoria
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }
}

