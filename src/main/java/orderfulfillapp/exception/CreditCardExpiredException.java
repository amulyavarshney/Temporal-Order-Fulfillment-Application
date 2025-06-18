package orderfulfillapp.exception;

/**
 * Exception thrown when a credit card is expired.
 * This exception is marked as non-retryable in the workflow configuration.
 */
public class CreditCardExpiredException extends Exception {
    
    public CreditCardExpiredException(String message) {
        super(message);
    }

    public CreditCardExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
} 