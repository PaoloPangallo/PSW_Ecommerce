package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Entity
@Table(name = "shippings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mantieni il vincolo a livello DB, ma togli @NotNull a livello di Bean Validation
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 5)
    private String zipCode;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false)
    private LocalDateTime shippingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus status;

    @PrePersist
    public void prePersist() {
        // Se shippingDate non è settata dal service, la imposto di default a "adesso + 2 giorni"
        if (shippingDate == null) {
            shippingDate = LocalDateTime.now().plusDays(2);
        }
    }

    public enum ShippingStatus {
        PENDING, SHIPPED, DELIVERED
    }

    // equals, hashCode ...
}
