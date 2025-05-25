package demo.demo_ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shopping_cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Cart cart;

    @Version
    private Long version = 0L;

    @ManyToOne
    private Product product;

    private int quantity;

    @ManyToOne
    private Coupon appliedCoupon;

    private BigDecimal oldPrice;

    private BigDecimal price;

    @PrePersist
    public void prePersist() {
        if (version == null) version = 0L;
    }
}
