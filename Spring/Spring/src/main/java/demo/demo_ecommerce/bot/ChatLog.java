package demo.demo_ecommerce.bot;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String userMessage;
    @Column(length = 2048) // o più, se vuoi ancora più spazio
    private String botResponse;         // ✅ Risposta generata
    private String intentPredicted;
    private String intentCorrected;
    private Double confidenceScore;

    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }
}
