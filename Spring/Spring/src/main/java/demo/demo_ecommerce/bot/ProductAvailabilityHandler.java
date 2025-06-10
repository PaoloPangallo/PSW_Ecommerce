package demo.demo_ecommerce.bot;


import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductAvailabilityHandler implements IntentHandler {

    private final ProductRepository productRepo;

    public ProductAvailabilityHandler(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public String getIntentName() {
        return "disponibilità_prodotto";
    }

    @Override
    public List<String> getExamples() {
        return List.of(
                "Il prodotto X è disponibile?",
                "Hai ancora il prodotto X?",
                "Mi serve il prodotto X",
                "Ce l’avete X?"
        );
    }

    @Override
    public boolean isFallback() {
        return false;
    }

    @Override
    public BotResponseDTO handle(Long userId, String message, String llamaResponse) {
        System.out.println("🔎 [ProductAvailabilityHandler] Messaggio: " + message);

        // Estrai il nome in modo più robusto
        String name = estraiNomeProdotto(message);
        System.out.println("🔍 Nome prodotto estratto: '" + name + "'");

        BotResponseDTO resp = new BotResponseDTO();

        if (name.isBlank()) {
            resp.setText("❓ Non ho capito quale prodotto ti interessa. Prova a scrivere il nome completo.");
            resp.setFallback(true);
            return resp;
        }

        List<Product> matches = productRepo.searchByName(name);

        if (matches.isEmpty()) {
            resp.setText("🤔 Non ho trovato nessun prodotto chiamato \"" + name + "\".");
        } else if (matches.size() == 1) {
            Product p = matches.get(0);
            if (p.getStock() > 0) {
                resp.setText("✅ Il prodotto **" + p.getName() + "** è disponibile: " + p.getStock() + " pezzi.");
                resp.addButton("Vai al prodotto", "/product/" + p.getId());
            } else {
                resp.setText("❌ Il prodotto **" + p.getName() + "** è esaurito al momento.");
            }
        } else {
            StringBuilder sb = new StringBuilder("🔎 Ho trovato più prodotti che corrispondono:\n");
            for (Product p : matches) {
                sb.append("• ").append(p.getName()).append(" (")
                        .append(p.getStock()).append(" pezzi disponibili)\n");
            }
            resp.setText(sb.toString());
        }

        return resp;
    }

    private String estraiNomeProdotto(String msg) {
        // Rimuove "il prodotto", "prodotto", articoli, segni di punteggiatura
        return msg.replaceAll("(?i).*prodotto\\s+", "")
                .replaceAll("(?i)il\\s+|la\\s+|lo\\s+|un\\s+|una\\s+", "")
                .replaceAll("[^a-zA-Z0-9À-ÿ\\s]", "") // rimuove ?!,. ecc
                .trim();
    }
}
