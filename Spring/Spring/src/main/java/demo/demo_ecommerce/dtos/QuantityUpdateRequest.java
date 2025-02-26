package demo.demo_ecommerce.dtos;

import jakarta.validation.constraints.NotNull;

public class QuantityUpdateRequest {
    @NotNull
    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

