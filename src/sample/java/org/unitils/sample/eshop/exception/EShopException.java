package org.unitils.sample.eshop.exception;

/**
 * 
 */
public class EShopException extends RuntimeException {

    public EShopException() {
    }

    public EShopException(String message) {
        super(message);
    }

    public EShopException(String message, Throwable cause) {
        super(message, cause);
    }

    public EShopException(Throwable cause) {
        super(cause);
    }
}
