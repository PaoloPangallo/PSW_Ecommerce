package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    @NotBlank(message = "Il nome utente è obbligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Il nome utente contiene caratteri non validi")
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
    private String password;

    private String confirmPassword;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email deve essere valida")
    private String email;

    private Role role;

    // Campi opzionali aggiuntivi
    private String phone;
    private String address;
    private String cap;       // Codice di avviamento postale
    private String city;      // Città
    private String region;    // Regione
    private String country;   // Paese
}
