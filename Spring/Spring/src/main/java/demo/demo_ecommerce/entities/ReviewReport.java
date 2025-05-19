package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @PrePersist
    public void prePersist() {
        if (reportedAt == null) {
            reportedAt = LocalDateTime.now();
        }
    }
}

