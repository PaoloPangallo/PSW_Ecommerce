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
    private boolean featured;  // Aggiunto campo featured
    private String description;
    private Integer stock;
    private String imageUrl;


    public static ProductDTO fromEntity(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .featured(product.isFeatured())
                .description(product.getDescription())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .build();
    }


}
