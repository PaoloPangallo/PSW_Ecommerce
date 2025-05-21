package demo.demo_ecommerce.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class UserProfileSummaryDTO {
    private String username;
    private String email;
    private String city;
    private String country;
    private int totalOrders;
    private BigDecimal totalSpent;
    private int wishlistCount;
    private int reviewsCount;
    private String profileImageUrl; // ✅ AGGIUNTO




    // Getters e Setters
}

