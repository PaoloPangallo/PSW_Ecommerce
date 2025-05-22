package demo.demo_ecommerce.bot;

import demo.demo_ecommerce.entities.Cart;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.entities.ShoppingCartItem;
import demo.demo_ecommerce.repositories.ProductRepository;
import demo.demo_ecommerce.services.CartService;
import demo.demo_ecommerce.services.OrderService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmartBotService {
    private final LlamaClientService llamaClientService;
    private final List<BotIntent> intents = new ArrayList<>();
    private final IntentTrainerService intentTrainerService;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final CartService  cartService;



    public SmartBotService(
            BotSessionManager sessionManager,
            ChatLogRepository chatLogRepository,
            IntentTrainerService intentTrainerService,
            ConversationManager conversationManager,
            LlamaClientService llamaClientService,
            OrderService orderService,
            ProductRepository productRepository,
            CartService cartService
    ) {
        this.intentTrainerService = intentTrainerService;
        this.llamaClientService = llamaClientService;
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @PostConstruct
    public void init() {
        addIntent(new BotIntent(
                "tracking_ordine",
                List.of(
                        "Dov'è il mio ordine", "A che punto è l'ordine", "Stato ordine numero 123"
                ),
                "Controllo lo stato del tuo ordine..."
        ));

        addIntent(new BotIntent(
                "disponibilità_prodotto",
                List.of(
                        "Il prodotto X è disponibile?",
                        "Hai ancora il prodotto X?",
                        "Mi serve il prodotto X",
                        "Ce l’avete X?",
                        "C’è ancora X disponibile?",
                        "C’è disponibilità di X?",
                        "Quantità disponibile per X?"
                ),
                "Controllo la disponibilità del prodotto indicato..."
        ));

        addIntent(new BotIntent(
                "carrello",
                List.of("Mostrami il carrello", "Cosa ho nel carrello", "Fammi vedere i prodotti nel carrello", "Voglio controllare il carrello"),
                "Controllo il tuo carrello..."
        ));

    }


    private String extractProductName(String message) {
        // Rimuove parole comuni prima del nome
        String clean = message.toLowerCase()
                .replace("prodotto", "")
                .replace("mi serve", "")
                .replace("voglio", "")
                .replace("vorrei", "")
                .replace("ce l’avete", "")
                .replace("c’è", "")
                .replace("hai", "")
                .replace("avete", "")
                .replace("il", "")
                .replace("la", "")
                .replace("un", "")
                .replace("una", "")
                .replace("?", "")
                .trim();

        // Togli eventuale punteggiatura e spazi doppi
        clean = clean.replaceAll("[^a-z0-9àèéìòù'\\s-]", "").replaceAll("\\s+", " ").trim();

        return clean;
    }








    public BotResponseDTO getBotResponse(Long userId, String sessionId, String userMessage) {
        // 1. Risposta generica LLaMA
        String llamaResponse = llamaClientService.ask(userMessage);

        // 2. Intent manuale
        Optional<BotIntent> matchedManualIntent = matchIntent(userMessage);

        // 3. Intent da trainer (non prioritario ora)
        String predictedIntentName = intentTrainerService.predict(userMessage);
        BotIntent predictedIntent = findIntentByName(predictedIntentName);

        BotResponseDTO response = new BotResponseDTO();
        List<BotResponseDTO.BotButton> buttons = new ArrayList<>();
        String finalText = llamaResponse;

        if (matchedManualIntent.isPresent()) {
            BotIntent intent = matchedManualIntent.get();

            switch (intent.getName()) {
                case "tracking_ordine" -> {
                    String orderIdStr = extractOrderId(userMessage);
                    if (orderIdStr != null) {
                        try {
                            Long orderId = Long.parseLong(orderIdStr);
                            String stato = orderService.getOrderStatus(userId, orderId);
                            finalText = "📦 Stato ordine **#" + orderId + "**: " + stato;
                            buttons.add(new BotResponseDTO.BotButton("Storico Ordini", "/order-history"));
                        } catch (Exception e) {
                            finalText = "⚠️ Non riesco a recuperare lo stato per l’ordine `" + orderIdStr + "`.";
                        }
                    } else {
                        finalText = "❓ Mi serve il numero dell’ordine per controllarne lo stato.";
                    }
                }

                case "disponibilità_prodotto" -> {
                    String productName = extractProductName(userMessage);
                    if (productName != null) {
                        Optional<Product> opt = productRepository.searchByName(productName);
                        if (opt.isPresent()) {
                            Product p = opt.get();
                            int stock = p.getStock();
                            finalText = stock > 0
                                    ? "✅ Sì! Il prodotto **" + p.getName() + "** è disponibile. Ne abbiamo ancora **" + stock + "** pezzi."
                                    : "❌ Purtroppo il prodotto **" + p.getName() + "** è attualmente esaurito.";
                            buttons.add(new BotResponseDTO.BotButton("Vai al prodotto", "/product/" + p.getId()));
                        } else {
                            finalText = "🤔 Non trovo nessun prodotto chiamato \"" + productName + "\".";
                        }
                    } else {
                        finalText = "❓ Quale prodotto vuoi controllare?";
                    }
                }

                case "carrello" -> {
                    try {
                        CartService.CartSummary summary = cartService.getCartSummary(userId);
                        finalText = String.format(
                                "🛒 Hai **%d** prodotti nel carrello, per un totale di **%s L**.",
                                summary.getItemCount(),
                                summary.getTotal().toPlainString()
                        );
                        buttons.add(new BotResponseDTO.BotButton("Vai al checkout", "/checkout"));
                        buttons.add(new BotResponseDTO.BotButton("Modifica carrello", "/cart"));
                    } catch (Exception e) {
                        finalText = "⚠️ Non riesco a recuperare il tuo carrello in questo momento.";
                    }
                }

                default -> {
                    finalText = "**Risposta LLaMA:** " + llamaResponse + "\n\n" +
                            "**Suggerimento generico:**\n" +
                            intent.getResponse();

                    // fallback suggeriti se presenti
                    buttons.addAll(List.of(
                            new BotResponseDTO.BotButton("📦 Storico ordini", "/order-history"),
                            new BotResponseDTO.BotButton("🔍 Cerca prodotti", "/search"),
                            new BotResponseDTO.BotButton("💬 Assistenza", "/contatti")
                    ));
                }}


                // Aggiungi eventuali bottoni definiti nell’intento
            if (intent.getButtons() != null) {
                buttons.addAll(intent.getButtons());
            }
        }

        response.setText(finalText);
        response.setButtons(buttons);
        return response;
    }



    private Optional<BotIntent> matchIntent(String message) {
        double threshold = 0.85;
        String msgLower = message.toLowerCase();

        BotIntent bestIntent = null;
        double bestScore = 0.0;

        for (BotIntent intent : intents) {
            for (String example : intent.getExamples()) {
                double score = similarity.apply(msgLower, example.toLowerCase());
                if (score > bestScore && score > threshold) {
                    bestScore = score;
                    bestIntent = intent;
                }
            }
        }

        return Optional.ofNullable(bestIntent);
    }

    private String extractOrderId(String message) {
        Pattern p = Pattern.compile("ordine(?:\\s+numero)?\\s*(\\d+)");
        Matcher m = p.matcher(message.toLowerCase());
        if (m.find()) return m.group(1);
        return null;
    }


    private BotIntent findIntentByName(String name) {
        if (name == null) return null;
        return intents.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // Puoi popolare questa lista all'avvio o da admin
    public void addIntent(BotIntent intent) {
        intents.add(intent);
    }
}
