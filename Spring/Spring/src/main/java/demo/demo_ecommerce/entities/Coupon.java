package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private Double discountPercentage;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private BigDecimal minOrderValue;

    // Relazione ManyToMany con Product
    @ManyToMany
    @JoinTable(
            name = "coupon_products",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    public Coupon() {}

    public Coupon(String code, Double discountPercentage, LocalDateTime expirationDate, Boolean isActive, BigDecimal minOrderValue) {
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.expirationDate = expirationDate;
        this.isActive = isActive;
        this.minOrderValue = minOrderValue;
    }
}
