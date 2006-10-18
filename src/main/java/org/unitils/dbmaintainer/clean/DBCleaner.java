/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.clean;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.apache.commons.configuration.Configuration;

import javax.sql.DataSource;

/**
 * Defines the contract for implementations that delete all data from the database, except for the tables that have been
 * configured as <i>tablesToPreserve</i>, and the table in which the database version is stored.
 */
public interface DBCleaner {

    /**
     * Initializes this {@link DBCleaner}
     * @param configuration
     * @param dataSource
     * @param statementHandler
     */
    void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler);

    /**
     * delete all data from the database, except for the tables that have been
     * configured as <i>tablesToPreserve</i>, and the table in which the database version is stored,
     * in the {@link #init} method.
     * @throws StatementHandlerException
     */
    void cleanDatabase() throws StatementHandlerException;

}
