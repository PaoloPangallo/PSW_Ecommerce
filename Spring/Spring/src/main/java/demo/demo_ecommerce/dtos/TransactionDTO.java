package demo.demo_ecommerce.dtos;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    // Ad esempio "Visa", "Mastercard", "PayPal", etc.
    private String paymentMethod;
    private BigDecimal amount;
    // Usa valori come "PENDING", "SUCCESS", "FAILED"
    private String status;
}
