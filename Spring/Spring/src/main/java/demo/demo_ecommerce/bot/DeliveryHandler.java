package demo.demo_ecommerce.bot;

import demo.demo_ecommerce.entities.Order;
import demo.demo_ecommerce.services.DeliveryEstimationService;
import demo.demo_ecommerce.services.OrderService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DeliveryHandler implements IntentHandler {

    private final DeliveryEstimationService deliveryService;
    private final OrderService orderService;
    private static final Pattern capPattern = Pattern.compile("\\b(\\d{5})\\b");

    public DeliveryHandler(DeliveryEstimationService deliveryService, OrderService orderService) {
        this.deliveryService = deliveryService;
        this.orderService = orderService;
    }

    @Override
    public String getIntentName() {
        return "delivery_estimate";
    }

    @Override
    public List<String> getExamples() {
        return List.of(
                "Quando arriva il mio ordine?",
                "Quanto ci mette la spedizione a Roma?",
                "Consegna prevista a Milano",
                "Entro quando ricevo il pacco?"
        );
    }

    @Override
    public boolean isFallback() {
        return false;
    }

    @Override
    public BotResponseDTO handle(Long userId, String message, String llamaResponse) {
        // 1. Prova ad estrarre CAP dal messaggio
        Matcher matcher = capPattern.matcher(message);
        if (matcher.find()) {
            String cap = matcher.group(1);
            return buildResponseWithCap(cap);
        }

        // 2. Se fallisce, prova a recuperare CAP dall'ultimo ordine
        Optional<Order> maybeOrder = orderService.getLastOrderEntityForUser(userId);


        if (maybeOrder.isPresent()) {
            String cap = maybeOrder.get().getUser().getCap();
            if (cap != null && cap.matches("\\d{5}")) {
                return buildResponseWithCap(cap);
            }
        }

        // 3. Se ancora nulla, chiedi CAP all’utente
        return BotResponseDTO.builder()
                .text("📍 Per stimare la consegna, puoi indicarmi il tuo **CAP**? (es: `20100`)")
                .intentName(getIntentName())
                .confidenceScore(1.0)
                .fallback(false)
                .buttons(List.of(
                        new BotResponseDTO.BotButton("📦 Vedi ordini", "/order-history"),
                        new BotResponseDTO.BotButton("💬 Contatta supporto", "/contatti")
                ))
                .build();
    }

    private BotResponseDTO buildResponseWithCap(String cap) {
        LocalDate estimated = deliveryService.estimateDeliveryDate(cap);
        String formatted = estimated.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy"));

        return BotResponseDTO.builder()
                .text("📦 La consegna stimata per il CAP " + cap + " è: **" + formatted + "**.")
                .intentName(getIntentName())
                .confidenceScore(1.0)
                .fallback(false)
                .buttons(List.of(
                        new BotResponseDTO.BotButton("📦 Vedi ordini", "/order-history"),
                        new BotResponseDTO.BotButton("🛒 Vai al carrello", "/cart"),
                        new BotResponseDTO.BotButton("📍 Cambia indirizzo", "/profile")
                ))
                .build();
    }
}
