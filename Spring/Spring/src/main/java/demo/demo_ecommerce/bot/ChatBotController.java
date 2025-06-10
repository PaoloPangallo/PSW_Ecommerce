package demo.demo_ecommerce.bot;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    private final SmartBotService smartBotService;
    private final ChatLogRepository chatLogRepository;

    public ChatBotController(SmartBotService smartBotService, ChatLogRepository chatLogRepository) {
        this.smartBotService = smartBotService;
        this.chatLogRepository = chatLogRepository;
    }



    // 2. Endpoint per correggere manualmente l'intento riconosciuto
    @PostMapping("/correct-intent")
    public ResponseEntity<String> correctIntent(@RequestParam Long chatLogId, @RequestParam String correctIntent) {
        Optional<ChatLog> logOpt = chatLogRepository.findById(chatLogId);
        if (logOpt.isPresent()) {
            ChatLog log = logOpt.get();
            log.setIntentCorrected(correctIntent);
            chatLogRepository.save(log);
            return ResponseEntity.ok("Intent aggiornato.");
        } else {
            return ResponseEntity.badRequest().body("Log non trovato.");
        }
    }

    @PostMapping("/message")
    public ResponseEntity<?> handleMessage(@Valid @RequestBody ChatRequestDTO chatReq) {
        try {
            System.out.println("✅ Richiesta chatbot ricevuta: " + chatReq);

            Long userId = chatReq.getUserId();
            String message = chatReq.getMessage();
            String sessionId = "user-" + userId;

            System.out.println("➡️ Invio a smartBotService...");
            BotResponseDTO response = smartBotService.getBotResponse(userId, sessionId, message);
            System.out.println("✅ Risposta bot ricevuta: " + response);

            ChatLog log = ChatLog.builder()
                    .userId(userId)
                    .userMessage(message)
                    .botResponse(response.getText())
                    .recognizedIntent(response.getIntentName())
                    .fallback(response.isFallback())
                    .timestamp(Instant.now())
                    .build();

            chatLogRepository.save(log);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();  // stampa stacktrace completo

            String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            System.out.println("❌ Errore nel controller: " + errorMsg);

            BotResponseDTO fallbackResponse = BotResponseDTO.builder()
                    .text("❌ Errore interno chatbot: " + errorMsg)
                    .fallback(true)
                    .build();

            return ResponseEntity.status(500).body(fallbackResponse);
        }
    }




    @GetMapping("/logs/{userId}")
    public ResponseEntity<List<ChatLog>> getLogsForUser(@PathVariable Long userId) {
        List<ChatLog> logs = chatLogRepository.findByUserIdOrderByTimestampDesc(userId);
        return ResponseEntity.ok(logs);
    }




}
