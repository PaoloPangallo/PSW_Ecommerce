package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Order;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Hibernate;

@Getter
@Setter
public class OrderDTO {

    private Long id;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be a positive value")
    private BigDecimal total;

    private String shippingMethod;
    private BigDecimal shippingCost;


    private LocalDateTime createdAt;

    // Lista degli item (può essere vuota se non vengono caricati)
    private List<OrderItemDTO> items;

    public OrderDTO() {
    }

    public OrderDTO(Long id, BigDecimal total, LocalDateTime createdAt, List<OrderItemDTO> items) {
        this.id = id;
        this.total = total;
        this.createdAt = createdAt;
        this.items = items;
    }

    /**
     * Mappa l'entità Order in OrderDTO.
     * Se loadItems è true, tenterà di mappare anche gli orderItems, altrimenti li imposterà come lista vuota.
     */
    public OrderDTO(Long id, BigDecimal total, LocalDateTime createdAt, List<OrderItemDTO> items,
                    String shippingMethod, BigDecimal shippingCost) {
        this.id = id;
        this.total = total;
        this.createdAt = createdAt;
        this.items = items;
        this.shippingMethod = shippingMethod;
        this.shippingCost = shippingCost;
    }



    public static OrderDTO fromEntity(Order order, boolean loadItems) {
        List<OrderItemDTO> itemDTOs = Collections.emptyList();
        if (loadItems && order.getOrderItems() != null && Hibernate.isInitialized(order.getOrderItems())) {
            itemDTOs = order.getOrderItems().stream()
                    .map(OrderItemDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setTotal(order.getTotal());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(itemDTOs);
        dto.setShippingMethod(order.getShippingMethod() != null ? order.getShippingMethod().name() : null);
        dto.setShippingCost(order.getShippingCost());

        return dto;
    }



    // Metodo di default: carica gli item (utile per il dettaglio)

}
