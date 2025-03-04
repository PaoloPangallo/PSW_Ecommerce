package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Wishlist;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistDTO {
    private Long id;
    private Long userId;
    private List<ProductDTO> products;

    // Metodo per convertire un'entità Wishlist in un DTO
    public static WishlistDTO fromEntity(Wishlist wishlist) {
        return WishlistDTO.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId()) // Assumi che User non sia null
                .products(wishlist.getProducts().stream()
                        .map(ProductDTO::fromEntity) // Converte ogni prodotto in DTO
                        .collect(Collectors.toList()))
                .build();
    }
}
