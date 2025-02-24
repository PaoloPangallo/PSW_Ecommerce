package demo.demo_ecommerce.repositories;

import demo.demo_ecommerce.entities.Category;
import demo.demo_ecommerce.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Trova i prodotti per nome contenente una stringa con paginazione
    Page<Product> findByNameContaining(String name, Pageable pageable);

    // Trova i prodotti con prezzo inferiore a un determinato valore con paginazione
    Page<Product> findByPriceLessThan(double price, Pageable pageable);

    // Trova i prodotti con stock maggiore di un determinato valore con paginazione
    Page<Product> findByStockGreaterThan(int stock, Pageable pageable);

    // Trova i prodotti con un prezzo in un determinato intervallo con paginazione
    Page<Product> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable);

    // Query personalizzata per trovare prodotti con una descrizione simile
    @Query("SELECT p FROM Product p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByDescription(String keyword, Pageable pageable);

    // Verifica se esiste un prodotto con un determinato nome
    boolean existsByName(String name);

    // Trova i prodotti per categoria con supporto per paginazione
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Verifica se esiste un prodotto con una descrizione contenente una determinata stringa
    boolean existsByDescriptionContaining(String description);

    // Trova i prodotti ordinati per data di creazione (più recenti prima)
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    Page<Product> findAllOrderByCreatedAtDesc(Pageable pageable);

    // Trova i prodotti per categoria con supporto per paginazione
    Page<Product> findByCategory(Category category, Pageable pageable);

    // Trova un prodotto per nome
    Optional<Product> findByName(String name);



        List<Product> findByCategory(Category category);
    }


