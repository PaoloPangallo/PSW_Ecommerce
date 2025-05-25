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
    private final BigDecimal price;

    @NotNull
    private final int quantity;

    private final Long id;
    private final BigDecimal oldPrice;
    private final String couponCode;
    private final Integer discountPercentage;

    public ShoppingCartItemDTO(ShoppingCartItem item) {
        this.id = item.getId();
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();

        // Prezzo effettivo: scontato o standard
        // Prezzo effettivo (potenzialmente scontato)
        this.price = item.getPrice() != null ? item.getPrice() : item.getProduct().getPrice();

// Prezzo pieno del prodotto
        BigDecimal productPrice = item.getProduct().getPrice();

// Se item.getPrice() è inferiore al prezzo del prodotto, allora c'è sconto
        if (item.getAppliedCoupon() == null && productPrice != null && productPrice.compareTo(this.price) > 0) {
            // Solo se NON c'è coupon applicato e il prezzo è più basso → sconto di prodotto
            this.oldPrice = productPrice;
        } else {
            this.oldPrice = null;
        }



        this.quantity = item.getQuantity();

        this.couponCode = (item.getAppliedCoupon() != null)
                ? item.getAppliedCoupon().getCode()
                : null;

        // Calcolo percentuale sconto se oldPrice è valido
        if (this.oldPrice != null) {
            BigDecimal discount = this.oldPrice.subtract(this.price)
                    .divide(this.oldPrice, 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            this.discountPercentage = discount.intValue();
        } else {
            this.discountPercentage = 0;
        }


        // Debug utile in console backend
        System.out.println("CartDTO -> " + productName + " | prezzo: " + this.price + " | old: " + this.oldPrice + " | sconto: " + this.discountPercentage + "%");
    }

}
