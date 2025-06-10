package demo.demo_ecommerce.bot;


import demo.demo_ecommerce.services.CartService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartSummaryHandler implements IntentHandler {

    private final CartService cartService;

    public CartSummaryHandler(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public String getIntentName() {
        return "carrello";
    }

    @Override
    public List<String> getExamples() {
        return List.of(
                "Mostrami il carrello",
                "Cosa ho nel carrello",
                "Fammi vedere i prodotti nel carrello"
        );
    }

    @Override
    public boolean isFallback() {
        return false;
    }

    @Override
    public BotResponseDTO handle(Long userId, String message, String llamaResponse) {
        System.out.println("🛒 [CartSummaryHandler] Richiesta da userId=" + userId + ", messaggio='" + message + "'");

        BotResponseDTO resp = new BotResponseDTO();
        try {
            var summary = cartService.getCartSummary(userId);
            System.out.println("🛒 Carrello: " + summary.getItemCount() + " articoli, totale " + summary.getTotal());

            if (summary.getItemCount() == 0) {
                resp.setText("🛒 Il tuo carrello è vuoto.");
            } else {
                resp.setText("🛒 Hai **" + summary.getItemCount()
                        + "** articoli per un totale di **"
                        + summary.getTotal().toPlainString() + " L.**");
                resp.addButton("Vai al checkout", "/checkout");
                resp.addButton("Modifica carrello", "/cart");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setText("⚠️ Impossibile recuperare il carrello in questo momento.");
            resp.setFallback(true);
        }

        return resp;
    }
}
