package org.unitils.sample.eshop.exception;

import org.unitils.sample.eshop.model.Product;
import org.unitils.sample.eshop.exception.EShopException;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class NotEnoughItemsInStockException extends EShopException {

    public NotEnoughItemsInStockException() {
    }

    public NotEnoughItemsInStockException(Throwable cause) {
        super(cause);
    }

    public NotEnoughItemsInStockException(String message) {
        super(message);
    }

    public NotEnoughItemsInStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
