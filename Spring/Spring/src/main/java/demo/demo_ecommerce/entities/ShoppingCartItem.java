package demo.demo_ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "shopping_cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version = 0L;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    private Cart cart;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_coupon_id")
    private Coupon appliedCoupon;

    @Column(name = "old_price")
    private BigDecimal oldPrice;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "saved_for_later", nullable = false)
    private boolean savedForLater = false;

    // ✅ Costruttore rapido per logica "crea nuovo item"
    public ShoppingCartItem(Cart cart, Product product, int quantity, BigDecimal price, boolean savedForLater) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.savedForLater = savedForLater;
        this.version = 0L;
    }

    // ⚠️ Protezione contro versioni nulle (per Hibernate < 6 in certi contesti)
    @PrePersist
    public void prePersist() {
        if (version == null) version = 0L;
    }
}
