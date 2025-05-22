package demo.demo_ecommerce.bot;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class BotResponseDTO {
    // Getter e Setter
    private String text;
    private List<BotButton> buttons;

    @Setter
    @Getter
    public static class BotButton {
        // Getter e Setter
        private String label;
        private String link;

        // ✅ Costruttore aggiunto
        public BotButton(String label, String link) {
            this.label = label;
            this.link = link;
        }

    }
}


