package demo.demo_ecommerce.services;


import demo.demo_ecommerce.entities.Complaint;
import demo.demo_ecommerce.entities.ComplaintCategory;
import demo.demo_ecommerce.entities.ComplaintMessage;
import demo.demo_ecommerce.entities.ComplaintStatus;
import demo.demo_ecommerce.repositories.ComplaintMessageRepository;
import demo.demo_ecommerce.repositories.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComplaintService {


    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ComplaintMessageRepository messageRepository;

    private final ComplaintRepository complaintRepo;
    private final ComplaintMessageRepository msgRepo;

    @Transactional
    public void createComplaintWithFirstMessage(String email,
                                                ComplaintCategory category,
                                                String description) {

        // 1. Crea reclamo
        Complaint complaint = new Complaint();
        complaint.setEmail(email);
        complaint.setCategory(category);
        complaint.setDescription(description);      // campo riassuntivo
        Complaint saved = complaintRepo.save(complaint);

        // 2. Crea PRIMO messaggio con stesso testo
        ComplaintMessage first = new ComplaintMessage();
        first.setComplaint(saved);
        first.setSender("USER");
        first.setContent(description);
        first.setTimestamp(LocalDateTime.now());
        msgRepo.save(first);
    }

    public Complaint submitComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }


    public Complaint createComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getComplaintsByEmail(String email) {
        return complaintRepository.findByEmail(email);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    public ComplaintMessage addMessage(Long complaintId, ComplaintMessage message) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        message.setComplaint(complaint);
        return messageRepository.save(message);
    }

    public List<ComplaintMessage> getMessages(Long complaintId) {
        return messageRepository.findByComplaintIdOrderByTimestampAsc(complaintId);
    }

    public Complaint updateStatus(Long id, ComplaintStatus newStatus) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        ComplaintStatus currentStatus = complaint.getStatus();

        // Regole di transizione valide
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    "Transizione da " + currentStatus + " a " + newStatus + " non consentita");
        }

        complaint.setStatus(newStatus);
        return complaintRepository.save(complaint);
    }

    private boolean isValidTransition(ComplaintStatus current, ComplaintStatus next) {
        return switch (current) {
            case PENDING -> next == ComplaintStatus.ACCEPTED || next == ComplaintStatus.REJECTED;
            case ACCEPTED -> next == ComplaintStatus.CLOSED;
            case REJECTED -> false;
            case CLOSED -> false;
        };
    }




}
