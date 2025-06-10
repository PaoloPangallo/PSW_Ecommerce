package demo.demo_ecommerce.bot;

import demo.demo_ecommerce.entities.ComplaintCategory;
import demo.demo_ecommerce.services.ComplaintService;
import demo.demo_ecommerce.services.UsersService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ComplaintHandler implements IntentHandler {

    private final ComplaintService complaintService;
    private final UsersService usersService;

    public ComplaintHandler(ComplaintService complaintService, UsersService usersService) {
        this.complaintService = complaintService;
        this.usersService = usersService;
    }

    @Override
    public String getIntentName() {
        return "contatto_operatore";
    }

    @Override
    public List<String> getExamples() {
        return List.of(
                "Voglio parlare con un operatore",
                "Serve aiuto umano",
                "Ho bisogno di assistenza urgente",
                "Posso parlare con qualcuno?"
        );
    }

    @Override
    public boolean isFallback() {
        return false;
    }

    @Override
    public BotResponseDTO handle(Long userId, String message, String llamaResponse) {
        String email = usersService.getUserById(userId).getEmail();

        String descrizione = (llamaResponse != null && !llamaResponse.isBlank())
                ? llamaResponse
                : "Richiesta assistenza: " + message;

        complaintService.createComplaintWithFirstMessage(
                email,
                ComplaintCategory.SERVIZIO_CLIENTI, // ✅ categoria coerente
                descrizione
        );

        return BotResponseDTO.builder()
                .text("📬 Ho inoltrato la tua richiesta a un operatore. Ti risponderemo il prima possibile.")
                .intentName(getIntentName())
                .confidenceScore(1.0)
                .fallback(false)
                .build();
    }
}
