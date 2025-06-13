package demo.demo_ecommerce.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginRateLimiterService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MS = 60_000; // 1 minuto

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null) return false;

        if (attempt.blockedUntil != null && attempt.blockedUntil > System.currentTimeMillis()) {
            return true; // ancora bloccato
        }

        if (attempt.blockedUntil != null && attempt.blockedUntil <= System.currentTimeMillis()) {
            attempts.remove(username);
        }

        return false;
    }

    public void loginFailed(String username) {
        LoginAttempt attempt = attempts.computeIfAbsent(username, k -> new LoginAttempt());

        attempt.count++;
        if (attempt.count >= MAX_ATTEMPTS) {
            attempt.blockedUntil = System.currentTimeMillis() + BLOCK_DURATION_MS;
        }
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
    }

    private static class LoginAttempt {
        int count = 0;
        Long blockedUntil = null;
    }
}

