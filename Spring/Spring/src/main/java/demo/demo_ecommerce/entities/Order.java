package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data  // Lombok per getter, setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be a positive value")
    private BigDecimal total = BigDecimal.ZERO;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Order must be associated with a user")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;


    // Imposta la data di creazione automaticamente prima di persistente
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
