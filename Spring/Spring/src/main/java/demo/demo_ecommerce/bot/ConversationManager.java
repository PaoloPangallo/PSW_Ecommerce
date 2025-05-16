package demo.demo_ecommerce.bot;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationManager {
    private final ConcurrentHashMap<Long, ConversationContext> userContexts = new ConcurrentHashMap<>();

    public ConversationContext getContext(Long userId) {
        return userContexts.computeIfAbsent(userId, id -> new ConversationContext());
    }

    public void resetContext(Long userId) {
        userContexts.remove(userId);
    }
}

