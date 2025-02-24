package demo.demo_ecommerce.Utility;


/**
 * Eccezione personalizzata per segnalare che un utente non è stato trovato.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}

