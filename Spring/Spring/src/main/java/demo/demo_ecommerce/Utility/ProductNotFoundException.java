package demo.demo_ecommerce.Utility;



/**
 * Eccezione personalizzata per segnalare che un prodotto non è stato trovato.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}

