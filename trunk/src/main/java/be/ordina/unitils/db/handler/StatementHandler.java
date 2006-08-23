/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.handler;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Interface for classes that handle SQL statements.
 */
public interface StatementHandler {

    /**
     * Provides a <code>Properties</code> and a <code>DataSource</code> object for initialization
     *
     * @param properties
     * @param dataSource
     */
    void init(Properties properties, DataSource dataSource);

    /**
     * Handles the given SQL statement
     *
     * @param statement the SQL statement
     * @throws StatementHandlerException If the statement could not org handled correctly
     */
    void handle(String statement) throws StatementHandlerException;

}
