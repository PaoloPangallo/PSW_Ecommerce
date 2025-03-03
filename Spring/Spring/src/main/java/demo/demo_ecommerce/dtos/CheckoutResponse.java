package demo.demo_ecommerce.dtos;

import demo.demo_ecommerce.entities.Order;

public class CheckoutResponse {
    private Long orderId;
    private Order.OrderStatus status;
    private String message;

    public CheckoutResponse(Long orderId, Order.OrderStatus status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
