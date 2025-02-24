package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class TransactionDTO {
    private Long id;
    private String transactionId;
    private @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero") BigDecimal amount;
    @Getter
    private Transaction.TransactionStatus status;

    // Costruttore parametrizzato
    public TransactionDTO(Long id, String transactionId, @NotNull @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero") BigDecimal amount, Transaction.TransactionStatus status) {
        this.id = id;
        this.transactionId = transactionId;
        this.amount = amount;
        this.status = status;
    }




    // Metodo di conversione
    public static TransactionDTO fromEntity(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getStatus()
        );
    }
}
