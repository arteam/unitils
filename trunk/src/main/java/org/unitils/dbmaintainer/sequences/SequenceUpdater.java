/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.sequences;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * Defines the contract for implementation classes that update the sequence of a unit testing database to a sufficiently
 * high value, so that test data be inserted easily.
 */
public interface SequenceUpdater {

    /**
     * Initializes the VersionSource

     * @param configuration 
     * @param dataSource
     * @param statementHandler
     */
    void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler);

    /**
     * Updates the database sequences
     * 
     * @throws StatementHandlerException 
     */
    void updateSequences() throws StatementHandlerException;

}
