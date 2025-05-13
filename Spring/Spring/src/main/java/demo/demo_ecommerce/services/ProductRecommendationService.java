package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.OrderItem;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductRecommendationService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * Raccomanda prodotti basati sulla co-occorrenza pesata per frequenza e quantità.
     * Considera solo ordini con stato "PAID" per maggiore attendibilità.
     */
    public List<Product> recommendProductsFor(Long productId) {
        Map<Long, Map<Long, Integer>> coPurchaseMap = new HashMap<>();

        List<Order> paidOrders = orderRepository.findByStatus(Order.OrderStatus.PAID);
        System.out.println("Totale ordini PAID trovati: " + paidOrders.size());

        for (Order order : paidOrders) {
            // Usa una mappa productId -> quantity per questo ordine
            Map<Long, Integer> productQuantities = order.getOrderItems().stream()
                    .collect(Collectors.toMap(
                            oi -> oi.getProduct().getId(),
                            OrderItem::getQuantity,
                            Integer::sum
                    ));

            for (Long pidA : productQuantities.keySet()) {
                coPurchaseMap.putIfAbsent(pidA, new HashMap<>());
                for (Long pidB : productQuantities.keySet()) {
                    if (!pidA.equals(pidB)) {
                        int weight = productQuantities.get(pidA) * productQuantities.get(pidB);
                        coPurchaseMap.get(pidA).merge(pidB, weight, Integer::sum);
                    }
                }
            }
        }

        System.out.println("Mappa co-acquisti per " + productId + ": " + coPurchaseMap.get(productId));

        Map<Long, Integer> recommendations = coPurchaseMap.getOrDefault(productId, Map.of());

        List<Long> recommendedIds = recommendations.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(5)
                .collect(Collectors.toList());

        // ✅ Escludiamo prodotti non disponibili
        return productRepository.findAllById(recommendedIds).stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
    }
}