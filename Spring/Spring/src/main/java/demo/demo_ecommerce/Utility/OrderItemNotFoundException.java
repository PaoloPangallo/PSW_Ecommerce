package demo.demo_ecommerce.Utility;

public class OrderItemNotFoundException extends RuntimeException {
    public OrderItemNotFoundException(String message) {
        super(message);
    }
}

