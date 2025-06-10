package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Order.ShippingMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {

    @NotNull(message = "Metodo di spedizione obbligatorio")
    private ShippingMethod shippingMethod;

    @NotNull(message = "Lista di item confermati obbligatoria")
    private List<Long> confirmedItemIds; // ✅ aggiunto
}
