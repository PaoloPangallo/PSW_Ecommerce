package demo.demo_ecommerce.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotNull
    private TransactionDTO transaction;

    @NotNull
    private ShippingDTO shipping;
}
