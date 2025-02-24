package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    private static final Logger logger = LoggerFactory.getLogger(Review.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(1)
    @Max(5)
    @NotNull(message = "Rating must be between 1 and 5")
    private Integer rating;

    @NotBlank(message = "Comment cannot be blank")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @PrePersist
    public void prePersist() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
            logger.info("Setting createdDate for Review entity: {}", createdDate);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id != null && id.equals(review.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
