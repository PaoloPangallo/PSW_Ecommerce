package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.ShoppingCartItem;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ShoppingCartItemDTO {

    @NotNull
    private final Long productId;

    @NotNull
    private final String productName;

    @Digits(integer = 10, fraction = 2, message = "Price must be a valid monetary amount")
    @NotNull(message = "Price cannot be null")
    private final BigDecimal price;   // Prezzo attuale (potenzialmente scontato)

    @NotNull
    private final int quantity;

    private final Long id;

    private final BigDecimal oldPrice; // Prezzo originale (se presente)
    private final String couponCode;   // Codice coupon applicato (se presente)

    public ShoppingCartItemDTO(ShoppingCartItem item) {
        this.id = item.getId();
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();

        // Se ShoppingCartItem memorizza il prezzo attuale in item.getPrice(),
        // usiamo quello; se è null, fallback al prezzo del prodotto
        this.price = (item.getPrice() != null) ? item.getPrice() : item.getProduct().getPrice();

        // Stessa logica: se oldPrice è memorizzato in ShoppingCartItem, la recuperiamo
        this.oldPrice = item.getOldPrice(); // potrebbe essere null se non c'è stato uno sconto

        this.quantity = item.getQuantity();

        // Se è presente un coupon, salviamo il suo codice
        if (item.getAppliedCoupon() != null) {
            this.couponCode = item.getAppliedCoupon().getCode();
        } else {
            this.couponCode = null;
        }
    }
}
