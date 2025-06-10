package demo.demo_ecommerce.bot;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handler di fallback quando nessun intent manuale è stato matchato.
 * Deve essere l'unico handler con isFallback() true.
 */
@Component
public class FallbackHandler implements IntentHandler {

    @Override
    public String getIntentName() {
        return "fallback";
    }

    @Override
    public List<String> getExamples() {
        return List.of(); // fallback non ha esempi
    }

    @Override
    public boolean isFallback() {
        return true;
    }

    @Override
    public BotResponseDTO handle(Long userId, String message, String llamaResponse) {
        System.out.println("🤖 [FallbackHandler] Attivato per userId=" + userId);
        System.out.println("📩 Messaggio utente: " + message);
        System.out.println("🧠 Risposta LLaMA: " + (llamaResponse != null ? llamaResponse : "null"));

        String reply = (llamaResponse != null && !llamaResponse.isBlank())
                ? "🤖 " + llamaResponse + "\n\n"
                : "🤖 Non ho capito bene la richiesta.";

        reply += """
                Ecco alcune opzioni rapide:
                - 📦 Storico ordini (/order-history)
                - 🔍 Cerca prodotti (/search)
                - 💬 Assistenza (/contatti)
                """;

        return BotResponseDTO.builder()
                .text(reply)  // ✅ mai null
                .intentName("fallback")
                .confidenceScore(1.0)
                .fallback(true)
                .buttons(List.of(
                        new BotResponseDTO.BotButton("Ordini", "/order-history"),
                        new BotResponseDTO.BotButton("Cerca", "/search"),
                        new BotResponseDTO.BotButton("Contatti", "/contatti")
                ))
                .build();

    }
}