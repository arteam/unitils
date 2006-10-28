package org.unitils.core;

/**
 * todo javadoc
 * <p/>
 * todo switch IllegalArgumentExceptions to UnitilsException
 */
public class UnitilsException extends RuntimeException {

    public UnitilsException() {
    }

    public UnitilsException(String message) {
        super(message);
    }

    public UnitilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnitilsException(Throwable cause) {
        super(cause);
    }

}
