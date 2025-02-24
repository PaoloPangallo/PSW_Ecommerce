package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Wishlist;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class WishlistDTO {
    private Long id;
    private Long userId;
    private LocalDateTime createdDate;
    private List<Long> productIds;

    public static WishlistDTO fromEntity(Wishlist wishlist) {
        WishlistDTO dto = new WishlistDTO();
        dto.setId(wishlist.getId());
        dto.setUserId(wishlist.getUser().getId());
        dto.setCreatedDate(wishlist.getCreatedDate());
        dto.setProductIds(wishlist.getProducts()
                .stream()
                .map(product -> product.getId())
                .collect(Collectors.toList()));
        return dto;
    }
}
