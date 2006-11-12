package org.unitils.dbmaintainer.dbsupport;

import org.apache.commons.configuration.Configuration;
import org.unitils.dbmaintainer.handler.StatementHandler;

import javax.sql.DataSource;

/**
 * Base class for a task that can be performed on a database schema.
 */
abstract public class DatabaseTask {

    public static final String PROPKEY_SCHEMANAME = "dataSource.schemaName";

    protected DbSupport dbSupport;

    protected DataSource dataSource;

    protected String schemaName;

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

    abstract protected void doInit(Configuration configuration);

}
