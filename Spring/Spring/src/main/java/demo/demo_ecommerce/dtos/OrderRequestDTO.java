package demo.demo_ecommerce.dtos;


import demo.demo_ecommerce.entities.Order.ShippingMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDTO {

    @NotNull(message = "Metodo di spedizione obbligatorio")
    private ShippingMethod shippingMethod;
}

