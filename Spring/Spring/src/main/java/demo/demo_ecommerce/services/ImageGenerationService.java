package demo.demo_ecommerce.services;

import demo.demo_ecommerce.dtos.ImageUploadResponse;
import demo.demo_ecommerce.entities.Product;
import demo.demo_ecommerce.repositories.ProductRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ImageGenerationService {

    private final WebClient webClient;
    private final ProductRepository productRepository;
    private final String apiKey = "2favL6mbBvbwLSor5jooNw" ;



    public ImageGenerationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.webClient = WebClient.builder()
                .baseUrl("https://stablehorde.net/api/v2/generate/async")
                .defaultHeader("Client-Agent", "demo-ecommerce-app")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("apikey", apiKey) // 🔹 Qui impostiamo la tua API Key
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ImageUploadResponse generateProductImage(Long productId, String prompt) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato con ID: " + productId));

        // 1️⃣ Invia la richiesta a AI Horde con l'API Key corretta
        Mono<Map> responseMono = webClient.post()
                .bodyValue(Map.of(
                        "prompt", prompt,
                        "params", Map.of(
                                "width", 512,
                                "height", 512,
                                "steps", 30,
                                "sampler_name", "k_euler",
                                "cfg_scale", 7,
                                "n", 1
                        ),
                        "models", new String[]{"stable_diffusion"}
                ))
                .retrieve()
                .bodyToMono(Map.class);

        Map response = responseMono.block();
        if (response == null || !response.containsKey("id")) {
            throw new RuntimeException("Errore nella generazione dell'immagine.");
        }

        // 2️⃣ Ottieni l'ID della richiesta
        String requestId = (String) response.get("id");

        // 3️⃣ Attendi che l'immagine sia generata
        String imageUrl = waitForImage(requestId);

        // 4️⃣ Aggiorna il prodotto con l'immagine generata
        product.setImageUrl(imageUrl);
        productRepository.save(product);

        return new ImageUploadResponse("generated-image.png", imageUrl);
    }

    // Metodo per controllare lo stato della generazione
    private String waitForImage(String requestId) {
        String statusUrl = "https://stablehorde.net/api/v2/generate/status/" + requestId;

        while (true) {
            Mono<Map> statusResponseMono = webClient.get()
                    .uri(statusUrl)
                    .retrieve()
                    .bodyToMono(Map.class);

            Map statusResponse = statusResponseMono.block();
            if (statusResponse != null && statusResponse.containsKey("generations")) {
                // ✅ Usa List<Map> invece di array Map[]
                List<Map> generations = (List<Map>) statusResponse.get("generations");

                if (!generations.isEmpty()) {
                    return (String) generations.get(0).get("img");
                }
            }

            try {
                Thread.sleep(20000); // Aspetta 5 secondi prima di controllare di nuovo
            } catch (InterruptedException e) {
                throw new RuntimeException("Errore durante l'attesa dell'immagine.", e);
            }
        }
    }

}
