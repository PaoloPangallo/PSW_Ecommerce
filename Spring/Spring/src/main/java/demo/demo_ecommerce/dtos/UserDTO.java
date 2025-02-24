package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class UserDTO {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Email(message = "Email should be valid")
    @NotBlank
    private String email;

    @NotNull(message = "Role must not be null")
    private Role role;
}
