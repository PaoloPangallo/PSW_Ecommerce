package demo.demo_ecommerce.bot;

import demo.demo_ecommerce.dtos.WishlistDTO;
import demo.demo_ecommerce.services.WishlistService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WishlistHandler implements IntentHandler {

    private final WishlistService wishlistService;

    public WishlistHandler(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @Override
    public String getIntentName() {
        return "wishlist";
    }

    @Override
    public List<String> getExamples() {
        return List.of(
                "Mostrami la mia lista desideri",
                "Cosa ho salvato nella wishlist",
                "Visualizza prodotti salvati",
                "Vorrei vedere i preferiti"
        );
    }

    @Override
    public boolean isFallback() {
        return false;
    }

    @Override
    public BotResponseDTO handle(Long userId, String message, String llamaResponse) {
        BotResponseDTO resp = new BotResponseDTO();
        try {
            WishlistDTO wishlist = wishlistService.getUserWishlist(userId);

            if (wishlist.getProducts().isEmpty()) {
                resp.setText("📭 La tua wishlist è vuota al momento.");
            } else {
                StringBuilder sb = new StringBuilder("📝 Hai salvato questi prodotti:\n\n");
                wishlist.getProducts().forEach(p -> sb.append("• ").append(p.getName()).append("\n"));
                resp.setText(sb.toString());
                resp.addButton("Vai alla wishlist", "/wishlist");
            }

        } catch (Exception e) {
            resp.setText("⚠️ Non riesco a recuperare la wishlist in questo momento.");
        }
        return resp;
    }
}

