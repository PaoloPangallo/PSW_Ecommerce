package demo.demo_ecommerce.bot;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO per le risposte del bot, con testo e pulsanti.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotResponseDTO {

    /** Testo da mostrare all'utente */
    private String text;

    private String intentName;
    private Double confidenceScore;
    private boolean fallback;
    /**
     * Lista di pulsanti; inizializzata di default a lista vuota per evitare NPE.
     */
    @Builder.Default
    private List<BotButton> buttons = new ArrayList<>();

    /**
     * Aggiunge un pulsante alla risposta in modo fluido.
     *
     * @param label etichetta del pulsante
     * @param link  percorso o URL di destinazione
     * @return this per chaining
     */
    public BotResponseDTO addButton(String label, String link) {
        this.buttons.add(new BotButton(label, link));
        return this;
    }

    /**
     * Pulsante del bot, con etichetta e link.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BotButton {
        private String label;
        private String link;
    }
}