package demo.demo_ecommerce.bot;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * Entity per log delle conversazioni del Bot.
 * Campo 'fallback' con default false e non-nullable per evitare errori DDL.
 */
@Entity
@Table(name = "chat_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 1024)
    private String userMessage;

    @Column(nullable = false, length = 2048)
    private String botResponse;

    @Column(length = 255)
    private String intentPredicted;

    @Column(length = 255)
    private String intentCorrected;

    private Double confidenceScore;

    /**
     * Indica se si tratta di risposta fallback (no intent manuale matchato)
     */
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean fallback = false;

    @Column(length = 255)
    private String recognizedIntent;

    /**
     * Timestamp della creazione, salvato come Instant (timestamptz)
     */
    @Column(nullable = false)
    private Instant timestamp;

    @PrePersist
    public void prePersist() {
        this.timestamp = Instant.now();
    }
}