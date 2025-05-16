package demo.demo_ecommerce.bot;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class LlamaClientService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public String ask(String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "mistral"); // oppure "tinyllama"
        requestBody.put("prompt", userMessage);
        requestBody.put("stream", false); // se vuoi ricevere tutto insieme

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(OLLAMA_URL, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return (String) response.getBody().get("response");
        }

        return "Non riesco a contattare il modello LLaMA.";
    }
}
