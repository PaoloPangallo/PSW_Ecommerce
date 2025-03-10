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
    private String userName;

    // Nuovi campi per gli upvote
    private int upvotesCount;
    private boolean hasUpvoted; // questo flag andrà impostato in base alla logica (es. passando l'ID dell'utente corrente)

    // Metodo statico per convertire l'entità in DTO
    public static ReviewDTO fromEntity(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setProductId(review.getProduct().getId());
        dto.setUserId(review.getUser().getId());
        dto.setCreatedDate(review.getCreatedDate());
        dto.setUserName(review.getUser().getUsername());
        dto.setUpvotesCount(review.getUpvotes() != null ? review.getUpvotes().size() : 0);
        // Imposta a false di default, oppure aggiorna nel service in base all'utente corrente
        dto.setHasUpvoted(false);
        return dto;
    }
}
