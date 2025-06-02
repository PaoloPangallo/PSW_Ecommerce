package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.User;
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
    private String profileImageUrl;


    public static UserResponseDTO fromEntity(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setCap(user.getCap());
        dto.setCity(user.getCity());
        dto.setRegion(user.getRegion());
        dto.setCountry(user.getCountry());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        return dto;
    }
}
