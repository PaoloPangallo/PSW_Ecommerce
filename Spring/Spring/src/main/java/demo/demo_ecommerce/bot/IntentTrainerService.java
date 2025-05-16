package demo.demo_ecommerce.bot;

import jakarta.annotation.PostConstruct;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IntentTrainerService {

    private final ChatLogRepository chatLogRepository;
    private final Map<String, String> trainingData = new HashMap<>();
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    public IntentTrainerService(ChatLogRepository chatLogRepository) {
        this.chatLogRepository = chatLogRepository;
    }

    @PostConstruct
    public void trainOnStartup() {
        trainModel();
    }

    @Scheduled(fixedRate = 30 * 60 * 1000) // ogni 30 minuti
    public void scheduledRetrain() {
        System.out.println("🔄 Avvio retrain automatico del modello...");
        trainModel();
    }

    public void trainModel() {
        trainingData.clear();
        List<ChatLog> samples = chatLogRepository.findByIntentCorrectedIsNotNull();
        for (ChatLog log : samples) {
            trainingData.put(log.getUserMessage().toLowerCase(), log.getIntentCorrected());
        }
        System.out.println("🤖 Modello simulato aggiornato con " + trainingData.size() + " frasi annotate.");
    }

    public String predict(String input) {
        if (trainingData.isEmpty()) return null;

        String cleaned = input.toLowerCase().trim();
        double bestScore = 0.0;
        String bestIntent = null;

        for (Map.Entry<String, String> entry : trainingData.entrySet()) {
            double score = similarity.apply(cleaned, entry.getKey());
            if (score > bestScore) {
                bestScore = score;
                bestIntent = entry.getValue();
            }
        }

        return bestScore > 0.80 ? bestIntent : null;
    }
}
