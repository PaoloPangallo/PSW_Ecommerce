package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.entities.OrderItem;
import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.services.OrderItemService;
import demo.demo_ecommerce.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final OrderService orderService;

    // 🔒 Solo admin può creare manualmente un OrderItem
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderItem> createOrderItem(@Valid @RequestBody OrderItem orderItem) {
        OrderItem createdOrderItem = orderItemService.createOrderItem(orderItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderItem);
    }

    // 🔒 Solo admin o proprietario dell’ordine
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderItemById(@PathVariable Long id) {
        OrderItem orderItem = orderItemService.getOrderItemById(id);
        if (!isAdmin() && !isOwner(orderItem.getOrder())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
        return ResponseEntity.ok(orderItem);
    }

    // 🔒 Solo admin o proprietario dell’ordine
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderItemsByOrderId(@PathVariable Long orderId) {
        Order order = orderService.getOrderEntityById(orderId); // ✅ Corretto
        if (!isAdmin() && !isOwner(order)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }

        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    // 🔒 Solo admin può interrogare per prodotto
    @GetMapping("/product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderItem>> getOrderItemsByProductId(@PathVariable Long productId) {
        List<OrderItem> orderItems = orderItemService.getOrderItemsByProductId(productId);
        return ResponseEntity.ok(orderItems);
    }

    // 🔒 Solo admin può eliminare un OrderItem
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }

    // === UTILITY ===

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isOwner(Order order) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return order.getUser().getEmail().equalsIgnoreCase(userEmail);
    }
}
