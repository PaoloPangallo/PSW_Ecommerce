package demo.demo_ecommerce.services;

import demo.demo_ecommerce.Utility.OrderItemNotFoundException;
import demo.demo_ecommerce.entities.OrderItem;
import demo.demo_ecommerce.repositories.OrderItemRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemService.class);

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem createOrderItem(OrderItem orderItem) {
        logger.info("✅ Creating new OrderItem for order {} and product {}",
                orderItem.getOrder().getId(), orderItem.getProduct().getId());
        return orderItemRepository.save(orderItem);
    }

    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException("OrderItem not found with ID: " + id));
    }

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public List<OrderItem> getOrderItemsByProductId(Long productId) {
        return orderItemRepository.findByProductId(productId);
    }

    @Transactional
    public void deleteOrderItem(Long id) {
        logger.info("🗑 Tentativo di eliminazione dell'OrderItem con ID: {}", id);
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException("OrderItem non trovato con ID: " + id));

        if (item.getVersion() == null) {
            logger.warn("⚠️ Version is NULL per OrderItem con ID: {} — forzatura eliminazione via deleteByIdWithoutVersion()", id);
            orderItemRepository.deleteByIdWithoutVersion(id); // 👈 da implementare se non c’è
        } else {
            orderItemRepository.delete(item); // ✅ Hibernate gestisce il @Version
            logger.info("✅ OrderItem eliminato con version: {}", item.getVersion());
        }
    }
}
