package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Product;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;

    // Metodo per convertire l'entità Product in un DTO
    public static ProductDTO fromEntity(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }
}
