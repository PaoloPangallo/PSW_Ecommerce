package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relazione ManyToOne con Order
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Relazione ManyToOne con Product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "The quantity must not be null")
    @Min(value = 1, message = "The quantity must be at least 1")
    @Max(value = 1000, message = "The quantity cannot exceed 1000")
    private Integer quantity;

    @NotNull(message = "The price must not be null")
    @DecimalMin(value = "0.0", message = "The price must be 0 or greater")
    private BigDecimal price;

    @Transient
    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        return id != null && id.equals(((OrderItem) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
