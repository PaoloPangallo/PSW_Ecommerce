package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.OrderItem;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UsersRepository userRepository;

    public RecommendationService(ProductRepository productRepository, OrderRepository orderRepository, UsersRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }


    public List<Product> recommendProductsForUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            System.out.println("Nessun utente trovato con ID: " + userId);
            return List.of();
        }

        User user = userOpt.get();
        List<Order> orders = orderRepository.findByUserId(user.getId());

        Set<Product> purchasedProducts = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItem::getProduct)
                .collect(Collectors.toSet());

        System.out.println("Utente " + userId + " ha acquistato i seguenti prodotti:");
        purchasedProducts.forEach(p -> System.out.println(" - " + p.getName()));

        Map<Long, Long> categoryCount = purchasedProducts.stream()
                .filter(p -> p.getCategory() != null)
                .collect(Collectors.groupingBy(p -> p.getCategory().getId(), Collectors.counting()));

        if (categoryCount.isEmpty()) {
            System.out.println("Nessuna categoria trovata per i prodotti acquistati da " + userId);
            return List.of();
        }

        Long topCategoryId = Collections.max(categoryCount.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("Categoria preferita: " + topCategoryId);

        // 🔎 Prova a raccomandare prodotti che non sono già stati acquistati
        List<Product> recommended = productRepository.findAll().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(topCategoryId))
                .filter(p -> !purchasedProducts.contains(p))
                .limit(5)
                .collect(Collectors.toList());

        // 🛟 Fallback: mostra anche prodotti già acquistati se non ne trovi di nuovi
        if (recommended.isEmpty()) {
            System.out.println("Nessun prodotto nuovo da raccomandare, uso fallback con prodotti già visti.");
            recommended = productRepository.findAll().stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(topCategoryId))
                    .limit(5)
                    .collect(Collectors.toList());
        }

        return recommended;
    }


    public List<Product> getRecommendationsForEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            System.out.println("❌ Nessun utente trovato con email: " + email);
            return List.of();
        }

        User user = userOpt.get();
        return recommendProductsForUser(user.getId());
    }



    // Raccomandazioni per un prodotto (simili per categoria)
    public List<Product> recommendProductsForProduct(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) return List.of();

        Product product = productOpt.get();
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;

        if (categoryId == null) return List.of();


        return productRepository.findAll().stream()
                .filter(p -> !p.getId().equals(product.getId()))
                .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                .limit(5)
                .collect(Collectors.toList());

    }
}