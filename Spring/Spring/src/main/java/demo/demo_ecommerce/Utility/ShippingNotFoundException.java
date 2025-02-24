package demo.demo_ecommerce.Utility;


public class ShippingNotFoundException extends RuntimeException {
    public ShippingNotFoundException(String message) {
        super(message);
    }
}

