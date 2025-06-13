package demo.demo_ecommerce.services;

import demo.demo_ecommerce.entities.Complaint;
import demo.demo_ecommerce.entities.ComplaintCategory;
import demo.demo_ecommerce.entities.ComplaintMessage;
import demo.demo_ecommerce.entities.ComplaintStatus;
import demo.demo_ecommerce.repositories.ComplaintMessageRepository;
import demo.demo_ecommerce.repositories.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepo;
    private final ComplaintMessageRepository msgRepo;

    @Transactional
    public void createComplaintWithFirstMessage(String email,
                                                ComplaintCategory category,
                                                String description) {
        Complaint complaint = new Complaint();
        complaint.setEmail(email);
        complaint.setCategory(category);
        complaint.setDescription(description);

        Complaint saved = complaintRepo.save(complaint);

        ComplaintMessage first = new ComplaintMessage();
        first.setComplaint(saved);
        first.setSender("USER");
        first.setContent(description);
        first.setTimestamp(LocalDateTime.now());

        msgRepo.save(first);
    }

    public List<Complaint> getComplaintsByEmail(String email) {
        return complaintRepo.findByEmail(email);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepo.findAll();
    }

    public ComplaintMessage addMessage(Long complaintId, ComplaintMessage message) {
        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Reclamo non trovato"));
        message.setComplaint(complaint);
        return msgRepo.save(message);
    }

    public List<ComplaintMessage> getMessages(Long complaintId) {
        return msgRepo.findByComplaintIdOrderByTimestampAsc(complaintId);
    }

    public Complaint updateStatus(Long id, ComplaintStatus newStatus) {
        Complaint complaint = complaintRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamo non trovato"));

        ComplaintStatus currentStatus = complaint.getStatus();
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    "Transizione da " + currentStatus + " a " + newStatus + " non consentita");
        }

        complaint.setStatus(newStatus);
        return complaintRepo.save(complaint);
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamo non trovato"));
    }

    private boolean isValidTransition(ComplaintStatus current, ComplaintStatus next) {
        return switch (current) {
            case PENDING -> next == ComplaintStatus.ACCEPTED || next == ComplaintStatus.REJECTED;
            case ACCEPTED -> next == ComplaintStatus.CLOSED;
            case REJECTED, CLOSED -> false;
        };
    }
}
