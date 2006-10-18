/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.handler;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;

/**
 * Defines the contract for classes that handle SQL statements. E.g. by logging these statements or by executing them
 * on a database.
 */
public interface StatementHandler {

    /**
     * Provides a <code>Configuration</code> and a <code>TestDataSource</code> object for initialization
     *
     * @param dataSource
     */
    void init(Configuration configuration, DataSource dataSource);

    /**
     * Handles the given SQL statement
     *
     * @param statement the SQL statement
     * @throws StatementHandlerException If the statement could not org handled correctly
     */
    void handle(String statement) throws StatementHandlerException;

}
