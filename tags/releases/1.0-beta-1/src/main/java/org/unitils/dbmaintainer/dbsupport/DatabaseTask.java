package org.unitils.dbmaintainer.dbsupport;

import org.apache.commons.configuration.Configuration;
import org.unitils.dbmaintainer.handler.StatementHandler;

import javax.sql.DataSource;

/**
 * Base class for a task that can be performed on a database schema.
 *
 * @author Filip Neven
 */
abstract public class DatabaseTask {

    /* Property key for the database schema name */
    public static final String PROPKEY_SCHEMANAME = "dataSource.schemaName";

    /* Implementation of DbSupport, for executing all sorts of database operations and queries */
    protected DbSupport dbSupport;

    /* Provides connections to the unit test database*/
    protected DataSource dataSource;

    /* Name of the unit test database schema */
    protected String schemaName;

    /* Handles update statements that are issued to the database */
    protected StatementHandler statementHandler;

    /**
     * Initializes the database operation class with the given {@link Configuration}, {@link DbSupport}, {@link DataSource}
     * and {@link StatementHandler}
     *
     * @param configuration
     * @param dbSupport
     * @param dataSource
     * @param statementHandler
     */
    public void init(Configuration configuration, DbSupport dbSupport, DataSource dataSource, StatementHandler statementHandler) {
        this.dbSupport = dbSupport;
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;

        schemaName = configuration.getString(PROPKEY_SCHEMANAME).toUpperCase();

        doInit(configuration);
    }

    /**
     * Allows subclasses to perform some extra configuration using the given <code>Configuration</code> object
     * @param configuration
     */
    abstract protected void doInit(Configuration configuration);

}
