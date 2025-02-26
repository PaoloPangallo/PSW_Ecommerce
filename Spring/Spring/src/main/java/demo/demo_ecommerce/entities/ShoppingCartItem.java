package demo.demo_ecommerce.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "shopping_cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore  // 🔥 Evitiamo il ciclo di serializzazione JSON
    private Cart cart;

    @ManyToOne
    private Product product;

    private int quantity;
}
