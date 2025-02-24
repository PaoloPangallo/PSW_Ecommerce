package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.OrderItemNotFoundException;
import demo.demo_ecommerce.entities.OrderItem;
import demo.demo_ecommerce.repositories.OrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderItemService {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemService.class);

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    // Crea un nuovo OrderItem
    public OrderItem createOrderItem(OrderItem orderItem) {
        logger.info("Creating new OrderItem for order {} and product {}",
                orderItem.getOrder().getId(), orderItem.getProduct().getId());
        return orderItemRepository.save(orderItem);
    }

    // Recupera un OrderItem per ID
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException("OrderItem not found with ID: " + id));
    }

    // Recupera gli OrderItems per ID dell'ordine
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    // Recupera gli OrderItems per ID del prodotto
    public List<OrderItem> getOrderItemsByProductId(Long productId) {
        return orderItemRepository.findByProductId(productId);
    }

    // Elimina un OrderItem per ID
    public void deleteOrderItem(Long id) {
        logger.info("Deleting OrderItem with ID: {}", id);
        orderItemRepository.deleteById(id);
    }

    // Calcola il totale della quantità per un ordine
    public Integer getTotalQuantityByOrderId(Long orderId) {
        return orderItemRepository.findTotalQuantityByOrderId(orderId);
    }

    // Calcola il totale del prezzo per un ordine
    public BigDecimal getTotalPriceByOrderId(Long orderId) {
        return orderItemRepository.findTotalPriceByOrderId(orderId);
    }
}
