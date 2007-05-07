package org.unitils.sample.eshop.exception;

/**
 * Exception indicating that a user has not enough credit to pay a certain amount
 */
public class InsufficientCreditException extends EShopException {

    public InsufficientCreditException() {
    }

    public InsufficientCreditException(Throwable cause) {
        super(cause);
    }

    public InsufficientCreditException(String message) {
        super(message);
    }

    public InsufficientCreditException(String message, Throwable cause) {
        super(message, cause);
    }
}
