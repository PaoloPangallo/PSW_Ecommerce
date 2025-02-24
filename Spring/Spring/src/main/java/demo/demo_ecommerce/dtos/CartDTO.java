package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class CartDTO {

    @NotNull
    private final Long id;

    @NotNull
    private final Map<Long, Integer> items;

    public CartDTO(Long id, Map<Long, Integer> items) {
        this.id = id;
        this.items = items;
    }

    public static CartDTO fromEntity(Cart cart) {
        Map<Long, Integer> itemsMap = cart.getItems().stream()
                .collect(Collectors.toMap(
                        (ShoppingCartItem item) -> item.getProduct().getId(),
                        ShoppingCartItem::getQuantity
                ));
        return new CartDTO(cart.getId(), itemsMap);
    }
}
