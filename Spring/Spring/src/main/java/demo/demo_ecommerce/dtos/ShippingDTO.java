package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Order;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDTO {

    private String address;
    private String city;
    private String zipCode;
    private String country;
    // Usa valori come "PENDING", "SHIPPED", "DELIVERED"
    private String status;
    @NotNull(message = "Il metodo di spedizione è obbligatorio")
    private Order.ShippingMethod shippingMethod;






}
