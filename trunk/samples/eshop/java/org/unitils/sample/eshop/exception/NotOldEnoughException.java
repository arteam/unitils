package org.unitils.sample.eshop.exception;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
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
