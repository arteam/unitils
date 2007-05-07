package org.unitils.sample.eshop.exception;

/**
 * Exception that is thrown when a user is not old enough to buy a certain Product
 */
public class NotOldEnoughException extends EShopException {

    public NotOldEnoughException() {
    }

    public NotOldEnoughException(Throwable cause) {
        super(cause);
    }

    public NotOldEnoughException(String message) {
        super(message);
    }

    public NotOldEnoughException(String message, Throwable cause) {
        super(message, cause);
    }
}
