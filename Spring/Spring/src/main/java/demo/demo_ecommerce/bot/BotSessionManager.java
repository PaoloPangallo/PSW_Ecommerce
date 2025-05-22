package demo.demo_ecommerce.bot;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BotSessionManager {
    private final Map<Long, String> lastIntent = new HashMap<>();
    private final Map<Long, Integer> fallbackCounter = new HashMap<>();

    public void setLastIntent(Long userId, String intent) {
        lastIntent.put(userId, intent);
        fallbackCounter.put(userId, 0);
    }

    public String getLastIntent(Long userId) {
        return lastIntent.get(userId);
    }

    public void incrementFallback(Long userId) {
        fallbackCounter.merge(userId, 1, Integer::sum);
    }

    public int getFallbackCount(Long userId) {
        return fallbackCounter.getOrDefault(userId, 0);
    }

    public void clear(Long userId) {
        lastIntent.remove(userId);
        fallbackCounter.remove(userId);
    }
}
