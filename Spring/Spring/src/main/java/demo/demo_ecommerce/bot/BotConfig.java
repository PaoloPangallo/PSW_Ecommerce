package demo.demo_ecommerce.bot;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    /** soglia fuzzy matching, es. 0.85 */
    @Bean
    public Double intentThreshold(@Value("${bot.intent.threshold:0.85}") double threshold) {
        return threshold;
    }

    /** default CircuitBreaker registry */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    /** CircuitBreaker specifico per la chiamata a LLaMA */
    @Bean
    public CircuitBreaker llamaCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("llamaClient");
    }
}

