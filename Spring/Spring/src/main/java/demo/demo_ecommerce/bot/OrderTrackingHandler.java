package demo.demo_ecommerce.bot;


import demo.demo_ecommerce.services.OrderService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OrderTrackingHandler implements IntentHandler {

    private final OrderService orderService;
    private final Pattern orderPattern = Pattern.compile("ordine(?: numero)?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    public OrderTrackingHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public String getIntentName() {
        return "tracking_ordine";
    }

    @Override
    public List<String> getExamples() {
        return List.of(
                "Dov'è il mio ordine",
                "A che punto è l'ordine",
                "Stato ordine numero 123"
        );
    }

    @Override
    public boolean isFallback() {
        return false;
    }

    @Override
    public BotResponseDTO handle(Long userId, String message, String llamaResponse) {
        System.out.println("📦 [OrderTrackingHandler] Messaggio ricevuto: " + message);

        Matcher m = orderPattern.matcher(message);
        BotResponseDTO resp = new BotResponseDTO();

        if (m.find()) {
            String orderIdStr = m.group(1);
            System.out.println("🔍 Estratto ID ordine: " + orderIdStr);

            try {
                Long orderId = Long.parseLong(orderIdStr);
                String stato = orderService.getOrderStatus(userId, orderId);

                resp.setText("📦 Stato dell’ordine **#" + orderId + "**: " + stato);
                resp.addButton("Storico Ordini", "/order-history");
            } catch (Exception e) {
                e.printStackTrace();
                resp.setText("⚠️ Errore durante il recupero dello stato dell’ordine `" + orderIdStr + "`.");
                resp.setFallback(true);
            }
        } else {
            resp.setText("❓ Per favore indicami **il numero dell’ordine** da controllare, es. *ordine 123*.");
            resp.setFallback(true);
        }

        return resp;
    }
}
