/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.clear;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * Defines the contract for implementations that clear a database schema. This means, all the tables, views, constraints,
 * triggers and sequences are dropped, so that the database schema is empty.
 */
public interface DBClearer {

    /**
     * Initializes the DBClearer

     * @param configuration 
     * @param dataSource
     * @param statementHandler
     */
    void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler);

    /**
     * Clears the database schema. This means, all the tables, views, constraints, triggers and sequences are
     * dropped, so that the database schema is empty.
     
     * @throws StatementHandlerException 
     */
    void clearDatabase() throws StatementHandlerException;

}
