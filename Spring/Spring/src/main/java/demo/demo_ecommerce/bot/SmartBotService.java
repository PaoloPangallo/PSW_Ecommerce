package demo.demo_ecommerce.bot;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SmartBotService {
    private final LlamaClientService llamaClientService;
    private final List<BotIntent> intents = new ArrayList<>();
    private final IntentTrainerService intentTrainerService;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    public SmartBotService(BotSessionManager sessionManager,
                           ChatLogRepository chatLogRepository,
                           IntentTrainerService intentTrainerService,
                           ConversationManager conversationManager,
                           LlamaClientService llamaClientService) {
        this.intentTrainerService = intentTrainerService;
        this.llamaClientService = llamaClientService;
    }

    public String getBotResponse(String sessionId, String userMessage) {
        // 1. Risposta da LLaMA
        String llamaResponse = llamaClientService.ask(userMessage);

        // 2. Riconoscimento intent da lista predefinita (manuale)
        Optional<BotIntent> matchedManualIntent = matchIntent(userMessage);

        // 3. Riconoscimento intent tramite trainer (da log corretti)
        String predictedIntentName = intentTrainerService.predict(userMessage);
        BotIntent predictedIntent = findIntentByName(predictedIntentName);

        // 4. Se troviamo un intent, lo usiamo per rafforzare
        if (matchedManualIntent.isPresent()) {
            BotIntent intent = matchedManualIntent.get();
            return "**Risposta LLaMA:** " + llamaResponse + "\n\n" +
                    "**Suggerimento mirato (“" + intent.getName() + "”):**\n" +
                    intent.getResponse();
        } else if (predictedIntent != null) {
            return "**Risposta LLaMA:** " + llamaResponse + "\n\n" +
                    "**(Ho riconosciuto l'intento '" + predictedIntent.getName() + "' basato sui log):**\n" +
                    predictedIntent.getResponse();
        }

        // 5. Altrimenti solo LLaMA
        return llamaResponse;
    }

    private Optional<BotIntent> matchIntent(String message) {
        double threshold = 0.85;
        String msgLower = message.toLowerCase();

        BotIntent bestIntent = null;
        double bestScore = 0.0;

        for (BotIntent intent : intents) {
            for (String example : intent.getExamples()) {
                double score = similarity.apply(msgLower, example.toLowerCase());
                if (score > bestScore && score > threshold) {
                    bestScore = score;
                    bestIntent = intent;
                }
            }
        }

        return Optional.ofNullable(bestIntent);
    }

    private BotIntent findIntentByName(String name) {
        if (name == null) return null;
        return intents.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // Puoi popolare questa lista all'avvio o da admin
    public void addIntent(BotIntent intent) {
        intents.add(intent);
    }
}
