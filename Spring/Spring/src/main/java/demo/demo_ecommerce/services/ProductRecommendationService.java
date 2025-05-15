package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.entities.OrderItem;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRecommendationService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<Product> recommendProductsFor(Long productId) {
        Map<Long, Map<Long, Integer>> coPurchaseMap = new HashMap<>();

        List<Order> paidOrders = orderRepository.findByStatus(Order.OrderStatus.PAID);
        log.info("Trovati {} ordini con stato PAID", paidOrders.size());

        for (Order order : paidOrders) {
            List<OrderItem> items = order.getOrderItems();

            for (int i = 0; i < items.size(); i++) {
                Long pidA = items.get(i).getProduct().getId();
                int qtyA = items.get(i).getQuantity();
                coPurchaseMap.putIfAbsent(pidA, new HashMap<>());

                for (int j = 0; j < items.size(); j++) {
                    if (i == j) continue;
                    Long pidB = items.get(j).getProduct().getId();
                    int qtyB = items.get(j).getQuantity();

                    coPurchaseMap.get(pidA).merge(pidB, qtyA * qtyB, Integer::sum);
                }
            }
        }

        Map<Long, Integer> recommendations = coPurchaseMap.getOrDefault(productId, Collections.emptyMap());

        List<Long> recommendedIds = recommendations.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(5)
                .collect(Collectors.toList());

        if (recommendedIds.isEmpty()) return List.of();

        return productRepository.findAllById(recommendedIds).stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
    }
}
