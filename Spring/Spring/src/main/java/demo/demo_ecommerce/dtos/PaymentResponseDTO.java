package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Payment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
public class PaymentResponseDTO {

    private Long id;
    private Long userId;
    private Long orderId;
    private String paymentMethod;
    private Double amount;
    private String status;
    private LocalDateTime timestamp;

    public static PaymentResponseDTO fromEntity(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUser().getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus().toString());
        dto.setTimestamp(payment.getTimestamp());
        return dto;
    }


}
