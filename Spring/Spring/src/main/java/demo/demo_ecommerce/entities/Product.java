package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 50)
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @Digits(integer = 10, fraction = 2, message = "Price must be a valid monetary amount")
    @NotNull(message = "Price cannot be null")
    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "discount_percentage")
    private Integer discountPercentage; // es: 20 per 20%



    @Version
    private Long version = 0L;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "image_url", length = 512) // o un valore adeguato alle tue necessità
    private String imageUrl;


    // Nuovo campo per identificare i prodotti in evidenza
    @Column(nullable = false)
    private boolean featured = false;

    public BigDecimal getDiscountedPrice() {
        if (discountPercentage != null && discountPercentage > 0) {
            BigDecimal discount = price.multiply(BigDecimal.valueOf(discountPercentage)).divide(BigDecimal.valueOf(100));
            return price.subtract(discount).setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }

    public Product() {}

    public Product(String name, String description, BigDecimal price, int stock, Category category, String imageUrl, boolean featured) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.imageUrl = imageUrl;
        this.featured = featured;
    }

    @PrePersist
    public void prePersist() {
        if (version == null) version = 0L;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
