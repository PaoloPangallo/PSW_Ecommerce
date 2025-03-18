package demo.demo_ecommerce.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CouponResponseDTO {
    private Long id;
    private String code;
    private BigDecimal discountPercentage;
    private LocalDateTime expirationDate;
    private Boolean isActive;
    private BigDecimal minOrderValue;
    private List<String> productNames;

}
