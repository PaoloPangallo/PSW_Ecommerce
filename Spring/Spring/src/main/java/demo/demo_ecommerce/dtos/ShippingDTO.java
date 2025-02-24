package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Shipping;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShippingDTO {
    private Long id;
    private Long orderId;
    private String address;
    private String city;
    private String zipCode;
    private String country;
    private LocalDateTime shippingDate;
    private Shipping.ShippingStatus status;

    public static ShippingDTO fromEntity(Shipping shipping) {
        ShippingDTO dto = new ShippingDTO();
        dto.setId(shipping.getId());
        dto.setOrderId(shipping.getOrder().getId());
        dto.setAddress(shipping.getAddress());
        dto.setCity(shipping.getCity());
        dto.setZipCode(shipping.getZipCode());
        dto.setCountry(shipping.getCountry());
        dto.setShippingDate(shipping.getShippingDate());
        dto.setStatus(shipping.getStatus());
        return dto;
    }
}

