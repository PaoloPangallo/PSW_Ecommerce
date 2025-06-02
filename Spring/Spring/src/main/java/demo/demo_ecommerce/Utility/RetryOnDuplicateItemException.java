package demo.demo_ecommerce.Utility;

public class RetryOnDuplicateItemException extends RuntimeException {
    public RetryOnDuplicateItemException(String message, Throwable cause) {
        super(message, cause);
    }
}

