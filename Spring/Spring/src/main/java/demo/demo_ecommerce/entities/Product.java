package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
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

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relazione Many-to-One con Category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Aggiunta per l'URL dell'immagine
    @Column(length = 255)
    private String imageUrl;  // URL dell'immagine del prodotto

    public Product() {}

    public Product(String name, String description, BigDecimal price, int stock, Category category, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.imageUrl = imageUrl;  // Settaggio del campo imageUrl
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
