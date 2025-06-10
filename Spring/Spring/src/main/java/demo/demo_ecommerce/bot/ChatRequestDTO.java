package demo.demo_ecommerce.bot;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {
    @NotNull(message = "userId è obbligatorio")
    private Long userId;

    @NotBlank(message = "message non può essere vuoto")
    private String message;
}

