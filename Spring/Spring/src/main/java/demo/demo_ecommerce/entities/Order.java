package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version = 0L;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be a positive value")
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relazione OneToMany con OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    // Nuovo campo per salvare il coupon applicato all'ordine (opzionale)
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.CREATED;
        }
        if (version == null) {
            version = 0L;
        }
    }




    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_method", nullable = false)
    private ShippingMethod shippingMethod;

    @Column(name = "shipping_cost", nullable = false)
    private BigDecimal shippingCost = BigDecimal.ZERO;


    public enum ShippingMethod {
        STANDARD,
        EXPRESS,
        PREMIUM
    }



    public enum OrderStatus {
        CREATED, PAID, SHIPPED, DELIVERED, CANCELLED
    }
}
