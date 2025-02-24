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

    private static final Logger logger = LoggerFactory.getLogger(Shipping.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order ID cannot be null")
    private Order order;

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "ZIP Code cannot be blank")
    @Pattern(regexp = "\\d{5}", message = "ZIP Code must be exactly 5 digits")
    @Column(nullable = false)
    private String zipCode;

    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Column(nullable = false)
    private String country;

    @NotNull(message = "Shipping date cannot be null")
    @FutureOrPresent(message = "Shipping date must be in the present or future")
    @Column(nullable = false)
    private LocalDateTime shippingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus status;

    @PrePersist
    public void prePersist() {
        if (shippingDate == null) {
            shippingDate = LocalDateTime.now().plusDays(2);
            logger.info("Shipping date not provided. Defaulting to: {}", shippingDate);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipping shipping = (Shipping) o;
        return id != null && id.equals(shipping.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Enum per lo stato della spedizione
    public enum ShippingStatus {
        PENDING,
        SHIPPED,
        DELIVERED
    }
}
