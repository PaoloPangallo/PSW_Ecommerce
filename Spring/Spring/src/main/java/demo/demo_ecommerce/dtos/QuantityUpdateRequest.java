package demo.demo_ecommerce.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuantityUpdateRequest {
    @NotNull
    private Integer quantity;

}

