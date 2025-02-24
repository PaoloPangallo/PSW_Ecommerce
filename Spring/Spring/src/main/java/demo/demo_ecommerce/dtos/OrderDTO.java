package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Order;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class OrderDTO {

    private final Long id;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be a positive value")
    private final BigDecimal total;

    private final LocalDateTime createdAt;

    // Costruttore parametrizzato
    public OrderDTO(Long id, BigDecimal total, LocalDateTime createdAt) {
        this.id = id;
        this.total = total;
        this.createdAt = createdAt;
    }

    // Metodo statico per la conversione da entità a DTO
    public static OrderDTO fromEntity(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        return new OrderDTO(order.getId(), order.getTotal(), order.getCreatedAt());
    }
}
