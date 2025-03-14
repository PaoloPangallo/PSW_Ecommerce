package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class CartDTO {

    @NotNull
    private final Long id;

    @NotNull
    private final List<ShoppingCartItemDTO> items;
    private final Map<Long, String> selectedCoupons; // productId -> couponCode


    public CartDTO(Long id, List<ShoppingCartItemDTO> items, Map<Long, String> selectedCoupons) {
        this.id = id;
        this.items = items;
        this.selectedCoupons = selectedCoupons;
    }
    public static CartDTO fromEntity(Cart cart) {
        List<ShoppingCartItemDTO> itemsList = cart.getItems().stream()
                .filter(item -> item.getProduct() != null)
                .map(ShoppingCartItemDTO::new)
                .collect(Collectors.toList());
        return new CartDTO(cart.getId(), itemsList, cart.getSelectedCoupons());
    }

}
