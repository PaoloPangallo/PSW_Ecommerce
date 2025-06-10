// src/main/java/demo/demo_ecommerce/entities/BotIntent.java
package demo.demo_ecommerce.bot;

import lombok.Getter;

import java.util.List;

@Getter
public class BotIntent {
    private final String name;
    private final List<String> examples;
    private final String response;
    private final List<BotResponseDTO.BotButton> buttons;

    public BotIntent(String name, List<String> examples, String response, List<BotResponseDTO.BotButton> buttons) {
        this.name = name;
        this.examples = examples;
        this.response = response;
        this.buttons = buttons;
    }


}
