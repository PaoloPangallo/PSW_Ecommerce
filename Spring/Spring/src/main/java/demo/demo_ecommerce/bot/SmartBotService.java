package demo.demo_ecommerce.bot;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmartBotService {

    private final Map<Long, PendingIntent> pendingIntents = new ConcurrentHashMap<>();

    private record PendingIntent(String intentName, LocalDateTime timestamp) {}

    private final List<IntentHandler> handlers;
    private final IntentHandler fallbackHandler;
    private final LlamaClientService llamaClientService;
    private final CircuitBreaker llamaCircuitBreaker;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
    private final double intentThreshold;

    public SmartBotService(
            List<IntentHandler> handlers,
            LlamaClientService llamaClientService,
            CircuitBreakerRegistry cbRegistry,
            @Value("${bot.intent.threshold:0.85}") double intentThreshold
    ) {
        this.handlers = handlers;
        this.llamaClientService = llamaClientService;
        this.llamaCircuitBreaker = cbRegistry.circuitBreaker("llamaClient");
        this.intentThreshold = intentThreshold;
        this.fallbackHandler = handlers.stream()
                .filter(IntentHandler::isFallback)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Fallback handler mancante"));
        System.out.println("🧠 [SmartBotService] Inizializzato con " + handlers.size() + " handler.");
    }

    public BotResponseDTO getBotResponse(Long userId, String sessionId, String userMessage) {
        try {
            // 1. Controlla se c’è un intent sospeso per l’utente
            PendingIntent pending = pendingIntents.get(userId);
            if (pending != null && !isExpired(pending)) {
                String intentName = pending.intentName();
                Optional<IntentHandler> handlerOpt = handlers.stream()
                        .filter(h -> h.getIntentName().equals(intentName))
                        .findFirst();

                if (handlerOpt.isPresent()) {
                    System.out.println("🔁 Intent sospeso: " + intentName);
                    pendingIntents.remove(userId); // pulizia dopo gestione
                    return safeHandle(handlerOpt.get(), userId, userMessage, null);
                }
            } else {
                pendingIntents.remove(userId); // cleanup scaduto
            }

            // 2. Matching intent con Jaro-Winkler
            System.out.println("🔍 Rilevo intent con Jaro-Winkler...");
            Optional<IntentHandler> best = detectIntent(userMessage);

            if (best.isPresent()) {
                IntentHandler handler = best.get();
                System.out.println("✅ Intent rilevato: " + handler.getClass().getSimpleName());

                return safeHandle(handler, userId, userMessage, null);
            } else {
                System.out.println("⚠️ Intent non rilevato → uso fallback con LLaMA");

                String llamaResponse = llamaCircuitBreaker.executeSupplier(
                        () -> llamaClientService.ask(userMessage)
                );
                System.out.println("🧠 Risposta LLaMA: " + llamaResponse);

                return safeHandle(fallbackHandler, userId, userMessage, llamaResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
            String err = e.getMessage() != null ? e.getMessage() : e.toString();
            return BotResponseDTO.builder()
                    .text("❌ Errore interno chatbot (AI): " + err)
                    .fallback(true)
                    .build();
        }
    }

    private BotResponseDTO safeHandle(IntentHandler handler, Long userId, String msg, String llamaResponse) {
        try {
            System.out.println("🔧 Eseguo handler: " + handler.getClass().getSimpleName());
            BotResponseDTO response = handler.handle(userId, msg, llamaResponse);
            if (response == null || response.getText() == null) {
                throw new IllegalStateException("❗ Handler " + handler.getClass().getSimpleName() + " ha restituito una risposta nulla");
            }
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
            String err = ex.getMessage() != null ? ex.getMessage() : ex.toString();
            System.out.println("❌ Errore handler " + handler.getClass().getSimpleName() + ": " + err);
            return BotResponseDTO.builder()
                    .text("❌ Errore nel gestore intent: " + handler.getClass().getSimpleName() + " → " + err)
                    .fallback(true)
                    .build();
        }
    }

    private Optional<IntentHandler> detectIntent(String message) {
        String lower = message.toLowerCase();
        IntentHandler best = null;
        double bestScore = 0.0;

        for (IntentHandler h : handlers) {
            if (h.isFallback()) continue;
            for (String ex : h.getExamples()) {
                double score = similarity.apply(lower, ex.toLowerCase());
                if (score > intentThreshold && score > bestScore) {
                    bestScore = score;
                    best = h;
                }
            }
        }
        return Optional.ofNullable(best);
    }

    public void setPendingIntent(Long userId, String intentName) {
        pendingIntents.put(userId, new PendingIntent(intentName, LocalDateTime.now()));
    }

    private boolean isExpired(PendingIntent pending) {
        return Duration.between(pending.timestamp(), LocalDateTime.now()).toMinutes() > 5;
    }
}
