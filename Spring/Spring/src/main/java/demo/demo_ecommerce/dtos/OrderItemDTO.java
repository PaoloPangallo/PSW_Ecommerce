package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDTO {

    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    public OrderItemDTO() {
    }

    public OrderItemDTO(Long productId, String productName, int quantity, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // Converte l'entità OrderItem in OrderItemDTO
    public static OrderItemDTO fromEntity(OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("OrderItem cannot be null");
        }
        // productName e productId derivano dall'entità Product associata
        return new OrderItemDTO(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }
}
