package demo.demo_ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
    @JsonIgnore
    private Cart cart;

    @Version
    private Long version;


    @ManyToOne
    private Product product;

    private int quantity;

    @ManyToOne
    private Coupon appliedCoupon;

    /**
     * Prezzo originale del prodotto prima di qualsiasi sconto o coupon.
     * Viene impostato al momento dell’aggiunta del prodotto al carrello,
     * oppure quando aggiorniamo la quantità se non esiste già.
     */
    private BigDecimal oldPrice;

    /**
     * Prezzo finale che l’utente paga (potenzialmente scontato).
     * Se non è mai stato applicato un coupon, coincide con oldPrice.
     * Se applichi un coupon, aggiorni questo campo con il prezzo scontato.
     */
    private BigDecimal price;
}
