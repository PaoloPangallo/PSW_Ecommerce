package demo.demo_ecommerce.bot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 1. Endpoint principale: invia messaggio e ricevi risposta dal bot
    @PostMapping("/message")
    public ResponseEntity<String> handleMessage(@RequestParam Long userId, @RequestBody String userMessage) {
        String sessionId = "user-" + userId; // costruzione semplice sessione
        String response = smartBotService.getBotResponse(sessionId, userMessage);

        // 🔄 Salva log (opzionale, attiva se ChatLog è configurato)
        ChatLog log = new ChatLog();
        log.setUserId(userId);
        log.setUserMessage(userMessage);
        log.setBotResponse(response);
        chatLogRepository.save(log);

        return ResponseEntity.ok(response);
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
}
