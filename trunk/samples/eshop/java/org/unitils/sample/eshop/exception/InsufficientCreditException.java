package org.unitils.sample.eshop.exception;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
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
