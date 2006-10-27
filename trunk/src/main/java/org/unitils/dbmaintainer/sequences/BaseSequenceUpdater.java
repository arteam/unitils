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

/**
 * Base convenience implementation of {@link SequenceUpdater}
 */
abstract public class BaseSequenceUpdater implements SequenceUpdater {

    /* Property key for the lowest acceptacle sequence value */
    public static final String PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE = "sequenceUpdater.sequencevalue.lowestacceptable";

    /* Property key for the name of the database schema */
    public static final String PROPKEY_SCHEMA_NAME = "dataSource.schemaName";
    
    /* The <code>TestDataSource</code> that provides the connection to the database */
    protected DataSource dataSource;

    /* The StatementHandler on which the sequence update statements will be executed */
    protected StatementHandler statementHandler;

    /* The lowest acceptable sequence value */
    protected long lowestAcceptableSequenceValue;
    
    /* The name of the database schema */
    protected String schemaName;

    /**
     * @see SequenceUpdater#init(Configuration, DataSource, StatementHandler)
     */
    public void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;

        lowestAcceptableSequenceValue = configuration.getLong(PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE);
        schemaName = configuration.getString(PROPKEY_SCHEMA_NAME).toUpperCase();
    }

}
