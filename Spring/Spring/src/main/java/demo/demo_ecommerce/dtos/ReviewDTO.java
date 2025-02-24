package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private Long productId;
    private Long userId;
    private LocalDateTime createdDate;

    // Metodo statico per convertire l'entità in DTO
    public static ReviewDTO fromEntity(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getProduct().getId(),
                review.getUser().getId(),
                review.getCreatedDate()
        );
    }
}
