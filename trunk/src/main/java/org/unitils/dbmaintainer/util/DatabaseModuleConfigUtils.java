package org.unitils.dbmaintainer.util;

import org.apache.commons.configuration.Configuration;
import org.unitils.util.ConfigUtils;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.handler.StatementHandler;

import javax.sql.DataSource;

/**
 * Class containing configuration utility methods secifically for the DatabaseModule and related modules
 */
public class DatabaseModuleConfigUtils {

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /* Property keys of the database schema name */
    private static final String PROPKEY_DATABASE_SCHEMANAME = "dataSource.schemaName";

    /* Property key of the implementation of the DbSupport interface */
    public static final String PROPKEY_DBSUPPORT_CLASSNAME = "dbMaintainer.dbSupport.className";
    
    /**
     * Retrieves the concrete instance of the class with the given type as configured by the given <code>Configuration</code>.
     * The concrete instance must extend the class {@link DatabaseTask}.
     *
     * @param databaseTaskType The type of the DatabaseTask
     * @param configuration
     * @return The configured instance
     */
    public static <T> T getConfiguredDatabaseTaskInstance(Class<T> databaseTaskType,
                  Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {

        String databaseDialect = configuration.getString(PROPKEY_DATABASE_DIALECT);
        DatabaseTask instance = ConfigUtils.getConfiguredInstance(databaseTaskType, configuration, databaseDialect);
        DbSupport dbSupport = getConfiguredDbSupportInstance(configuration, dataSource, statementHandler);
        instance.init(configuration, dbSupport, dataSource, statementHandler);
        return (T) instance;
    }

    /**
     * Returns the concrete, dbms specific instance of {@link DbSupport} which is configured by the given <code>Configuration</code>.
     *
     * @param configuration
     * @param dataSource
     * @param statementHandler
     * @return The dbms specific instance of {@link DbSupport}
     */
    public static DbSupport getConfiguredDbSupportInstance(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {
        String databaseDialect = configuration.getString(PROPKEY_DATABASE_DIALECT);
        DbSupport dbSupport = ConfigUtils.getConfiguredInstance(DbSupport.class, configuration, databaseDialect);
        String schemaName = configuration.getString(PROPKEY_DATABASE_SCHEMANAME).toUpperCase();
        dbSupport.init(dataSource, schemaName, statementHandler);
        return dbSupport;
    }

    /**
     * Returns the configured instance of {@link StatementHandler} which is configured by the given <code>Configuration</code>.
     * @param configuration
     * @param dataSource
     * @return Returns the configured instance of {@link StatementHandler}
     */
    public static StatementHandler getConfiguredStatementHandlerInstance(Configuration configuration, DataSource dataSource) {
        StatementHandler st = ConfigUtils.getConfiguredInstance(StatementHandler.class, configuration);
        st.init(configuration, dataSource);
        return st;
    }

}
