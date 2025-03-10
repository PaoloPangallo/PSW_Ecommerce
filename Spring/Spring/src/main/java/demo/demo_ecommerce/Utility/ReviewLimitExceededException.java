package demo.demo_ecommerce.Utility;

public class ReviewLimitExceededException extends RuntimeException {

    public ReviewLimitExceededException(String message) {
        super(message);
    }

    public ReviewLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
