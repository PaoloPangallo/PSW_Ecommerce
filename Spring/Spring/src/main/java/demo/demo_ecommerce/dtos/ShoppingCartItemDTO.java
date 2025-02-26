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

    private final @Digits(integer = 10, fraction = 2, message = "Price must be a valid monetary amount")
    @NotNull(message = "Price cannot be null") BigDecimal price;  // ✅ Aggiunto il prezzo

    @NotNull
    private final int quantity;

    public ShoppingCartItemDTO(ShoppingCartItem item) {
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.price = item.getProduct().getPrice();
        this.quantity = item.getQuantity();
    }
}
