package demo.demo_ecommerce.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserDTO {

    @Size(max = 255, message = "L'email non può superare 255 caratteri")
    private String email;

    @Size(max = 20, message = "Il numero di telefono non può superare 20 caratteri")
    private String phone;

    @Size(max = 255, message = "L'indirizzo non può superare 255 caratteri")
    private String address;

    @Size(max = 10, message = "Il CAP non può superare 10 caratteri")
    private String cap;

    @Size(max = 100, message = "La città non può superare 100 caratteri")
    private String city;

    @Size(max = 100, message = "La regione non può superare 100 caratteri")
    private String region;

    @Size(max = 100, message = "Il paese non può superare 100 caratteri")
    private String country;

    // NOTA: non includiamo username e password,
    //       così non obblighiamo l'utente a reinserirli.
}
