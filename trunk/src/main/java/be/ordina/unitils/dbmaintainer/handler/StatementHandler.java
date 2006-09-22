/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.dbmaintainer.handler;

import javax.sql.DataSource;

/**
 * Interface for classes that handle SQL statements.
 */
public interface StatementHandler {

    /**
     * Provides a <code>Properties</code> and a <code>DataSource</code> object for initialization
     *
     * @param dataSource
     */
    void init(DataSource dataSource);

    /**
     * Handles the given SQL statement
     *
     * @param statement the SQL statement
     * @throws StatementHandlerException If the statement could not org handled correctly
     */
    void handle(String statement) throws StatementHandlerException;

}
