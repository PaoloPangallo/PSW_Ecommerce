package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "The order must not be null")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "The product must not be null")
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
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem that = (OrderItem) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
