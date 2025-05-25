package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.dtos.ComplaintRequest;
import demo.demo_ecommerce.dtos.MessageResponse;
import demo.demo_ecommerce.entities.Complaint;
import demo.demo_ecommerce.entities.ComplaintCategory;
import demo.demo_ecommerce.entities.ComplaintMessage;
import demo.demo_ecommerce.entities.ComplaintStatus;
import demo.demo_ecommerce.services.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    // ✅ Crea un nuovo reclamo
    @PostMapping
    public ResponseEntity<MessageResponse> submitComplaint(@RequestBody ComplaintRequest req) {
        String userEmail = getCurrentUserEmail();
        complaintService.createComplaintWithFirstMessage(
                userEmail,
                ComplaintCategory.valueOf(req.getCategory()),
                req.getMessage()
        );
        return ResponseEntity.ok(new MessageResponse("Reclamo ricevuto correttamente."));
    }

    // ✅ Visualizza tutti i reclami (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    // ✅ Visualizza reclami per utente autenticato
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user")
    public ResponseEntity<List<Complaint>> getUserComplaints() {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(complaintService.getComplaintsByEmail(email));
    }

    // ✅ Cambia stato (solo ADMIN)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateComplaintStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            ComplaintStatus status = ComplaintStatus.valueOf(body.get("status"));
            complaintService.updateStatus(id, status);
            return ResponseEntity.ok(new MessageResponse("Stato aggiornato correttamente."));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("❌ Transizione non valida: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("❌ Stato non riconosciuto."));
        }
    }

    // ✅ Aggiunge messaggio al reclamo (owner o admin)
    @PostMapping("/{id}/messages")
    public ResponseEntity<?> addMessage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String email = getCurrentUserEmail();
        Complaint complaint = complaintService.getComplaintById(id);

        if (!isOwner(complaint, email) && !isAdmin()) {
            return ResponseEntity.status(403).body(new MessageResponse("Accesso negato."));
        }

        ComplaintMessage message = new ComplaintMessage();
        message.setSender(body.get("sender")); // USER o ADMIN
        message.setContent(body.get("content"));
        message.setTimestamp(LocalDateTime.now());

        ComplaintMessage saved = complaintService.addMessage(id, message);
        return ResponseEntity.ok(saved);
    }

    // ✅ Visualizza messaggi (solo owner o admin)
    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        Complaint complaint = complaintService.getComplaintById(id);

        if (!isOwner(complaint, email) && !isAdmin()) {
            return ResponseEntity.status(403).body(new MessageResponse("Accesso negato."));
        }

        return ResponseEntity.ok(complaintService.getMessages(id));
    }

    // === Utility methods ===

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isOwner(Complaint complaint, String email) {
        return complaint.getEmail().equalsIgnoreCase(email);
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
