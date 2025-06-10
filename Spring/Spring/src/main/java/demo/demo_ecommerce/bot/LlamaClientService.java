package demo.demo_ecommerce.bot;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class LlamaClientService {

    private final RestTemplate restTemplate;
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public LlamaClientService() {
        this.restTemplate = new RestTemplate();
    }

    public String ask(String userMessage) {
        System.out.println("🎙️ [LlamaClientService] Richiesta ricevuta: " + userMessage);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "mistral");
        requestBody.put("prompt", userMessage);
        requestBody.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OLLAMA_URL, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();
                Object raw = body != null ? body.get("response") : null;

                if (raw instanceof String str && !str.isBlank()) {
                    System.out.println("🧠 [LlamaClientService] Risposta ricevuta: " + str);
                    return str;
                } else {
                    System.out.println("⚠️ [LlamaClientService] Risposta vuota o non valida");
                }
            } else {
                System.out.println("❌ [LlamaClientService] Errore HTTP: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("❌ [LlamaClientService] Eccezione durante la chiamata a Ollama:");
            e.printStackTrace();
        }

        return "🤖 Non riesco a formulare una risposta al momento.";
    }
}
