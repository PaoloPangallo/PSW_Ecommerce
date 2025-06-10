package demo.demo_ecommerce.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotNull
    private TransactionDTO transaction;

    @NotNull
    private ShippingDTO shipping;

    @NotNull
    private List<Long> confirmedItemIds; // ✅ Aggiunto
}
