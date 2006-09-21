/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.dbmaintainer.handler;

/**
 * This Exception is thrown when a problem occurs handling a statement
 */
public class StatementHandlerException extends Exception {

    /**
     * Constructs a StatementHandlerException with the given message
     *
     * @param message
     */
    public StatementHandlerException(String message) {
        super(message);
    }

    /**
     * Constructs a StatementHandlerException with the given message and cause
     *
     * @param message
     * @param cause
     */
    public StatementHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a StatementHandlerException with the given cause
     *
     * @param cause
     */
    public StatementHandlerException(Throwable cause) {
        super(cause);
    }

}
