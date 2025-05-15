package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.entities.NewsletterSubscriber;
import demo.demo_ecommerce.services.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService newsletterService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email non valida."));
        }

        try {
            NewsletterSubscriber subscriber = newsletterService.subscribe(email);
            String confirmationLink = "http://localhost:4200/newsletter/confirm?token=" + subscriber.getConfirmationToken();

            return ResponseEntity.ok(Map.of(
                    "message", "Iscrizione avviata. Controlla la tua email per confermare.",
                    "confirmationUrl", confirmationLink
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Errore durante l'iscrizione."));
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token mancante."));
        }

        boolean confirmed = newsletterService.confirmSubscription(token);
        if (confirmed) {
            return ResponseEntity.ok(Map.of("message", "Iscrizione confermata con successo!"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Token non valido o già utilizzato."));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<NewsletterSubscriber>> getAllSubscribers() {
        return ResponseEntity.ok(newsletterService.getAllSubscribers());
    }

    @PostMapping("/send-now")
    public ResponseEntity<?> sendNow() {
        try {
            newsletterService.sendWeeklyNewsletter(); // invio immediato
            return ResponseEntity.ok(Map.of("message", "Newsletter inviata manualmente con successo."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Errore durante l’invio manuale."));
        }
    }

}
