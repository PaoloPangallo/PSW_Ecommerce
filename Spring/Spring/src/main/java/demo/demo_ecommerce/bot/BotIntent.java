// src/main/java/demo/demo_ecommerce/entities/BotIntent.java
package demo.demo_ecommerce.bot;

import jakarta.persistence.Column;
import lombok.Getter;

import java.util.List;

@Getter
public class BotIntent {
    private final String name;
    private final List<String> examples;

    @Column(length = 2048)
    private String botResponse;

    private final String response;
    private final String followUp;

    public BotIntent(String name, List<String> examples, String response, String followUp) {
        this.name = name;
        this.examples = examples;
        this.response = response;
        this.followUp = followUp;
    }

}