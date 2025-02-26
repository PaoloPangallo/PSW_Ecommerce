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

    // Campo opzionale per la conferma della password (eventualmente validato con un validatore custom)
    private String confirmPassword;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email deve essere valida")
    private String email;

    // Ruolo opzionale: se non viene specificato, nel processo di registrazione potresti impostarlo di default (ad es. Role.USER)
    private Role role;

    // Campi opzionali aggiuntivi, ad esempio:
    private String phone;
    private String address;
}
