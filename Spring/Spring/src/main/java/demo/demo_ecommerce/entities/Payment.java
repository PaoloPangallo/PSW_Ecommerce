package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data  // Lombok per generare automaticamente getter, setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order cannot be null")
    private Order order;

    @NotBlank(message = "Payment method cannot be blank")
    @Column(nullable = false)
    private String paymentMethod;

    @Positive(message = "Amount must be positive")
    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)  // Usato per lo stato del pagamento come Enum
    @NotNull(message = "Payment status cannot be null")
    @Column(nullable = false)
    private PaymentStatus status;

    @NotNull(message = "Timestamp cannot be null")
    @FutureOrPresent(message = "Timestamp cannot be in the past")
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Enum per lo stato del pagamento
    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }
}
