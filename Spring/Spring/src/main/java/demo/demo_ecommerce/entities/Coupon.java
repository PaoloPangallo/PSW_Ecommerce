package demo.demo_ecommerce.entities;
import jakarta.validation.constraints.DecimalMax;


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


    @DecimalMax(value = "90.0", inclusive = true, message = "Lo sconto massimo è del 90%")
    @Column(nullable = false)
    private BigDecimal discountPercentage;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private BigDecimal minOrderValue;

    @ManyToMany
    @JoinTable(
            name = "coupon_products",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    public Coupon() {}

}
