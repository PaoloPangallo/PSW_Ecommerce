package demo.demo_ecommerce.bot;


import java.util.List;

public interface IntentHandler {
    /** Nome univoco dell’intent (es. “tracking_ordine”) */
    String getIntentName();
    /** Frasi di esempio per il matching fuzzy */
    List<String> getExamples();
    /** Se vero, è il handler di fallback */
    boolean isFallback();
    /**
     * Costruisce la risposta per questo intent.
     *
     * @param userId        id dell’utente
     * @param message       testo originale
     * @param llamaResponse risposta raw di LLaMA (può essere usata/fallback)
     */
    BotResponseDTO handle(Long userId, String message, String llamaResponse);
}
