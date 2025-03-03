package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String phone;
    private String address;
    private String cap;
    private String city;
    private String region;
    private String country;
}

