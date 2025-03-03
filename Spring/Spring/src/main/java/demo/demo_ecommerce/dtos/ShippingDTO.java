package demo.demo_ecommerce.dtos;

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
}
