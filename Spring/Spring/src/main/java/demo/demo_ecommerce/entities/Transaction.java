package demo.demo_ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
@Builder
public class Transaction {

    public Transaction() {
        this.id = null; // Valore predefinito per il campo final
    }

    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotBlank(message = "Transaction ID cannot be blank")
    private String transactionId;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @PrePersist
    public void prePersist() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
            logger.info("Setting transaction date for TransactionId: {}", transactionId);
        }
    }

    // Enum per lo stato della transazione
    public enum TransactionStatus {
        SUCCESS, FAILED, PENDING
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
