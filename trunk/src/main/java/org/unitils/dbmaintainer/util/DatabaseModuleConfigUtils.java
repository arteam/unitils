package org.unitils.dbmaintainer.util;

import org.apache.commons.configuration.Configuration;
import org.unitils.util.ReflectionUtils;
import org.unitils.core.UnitilsException;
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
        DatabaseTask instance = getConfiguredDbmsSpecificInstance(databaseTaskType, configuration);
        DbSupport dbSupport = DatabaseModuleConfigUtils.getConfiguredDbmsSpecificInstance(DbSupport.class, configuration);
        String schemaName = configuration.getString(PROPKEY_DATABASE_SCHEMANAME).toUpperCase();
        dbSupport.init(dataSource, schemaName, statementHandler);
        instance.init(configuration, dbSupport, dataSource, statementHandler);
        return (T) instance;
    }

    /**
     * Retrieves the concrete instance of the class with the given type as configured by the given <code>Configuration</code>.
     * Tries to retrieve the database specific implementation first (propery key = fully qualified name of the interface
     * type + '.impl.className.' + database dialect). If this key does not exist, the database dialect idendependent
     * instance is retrieved (same property key without the database dialect).
     *
     * @param type The type of the instance
     * @param configuration
     * @return The configured instance
     */
    public static <T> T getConfiguredDbmsSpecificInstance(Class type, Configuration configuration) {
        String propKey = type.getName() + ".implClassName";
        String dialect = configuration.getString(PROPKEY_DATABASE_DIALECT);
        String dbSpecificImplementationPropKey = propKey + "." + dialect;
        if (configuration.containsKey(dbSpecificImplementationPropKey)) {
            return (T) ReflectionUtils.createInstanceOfType(configuration.getString(dbSpecificImplementationPropKey));
        } else if (configuration.containsKey(propKey)) {
            return (T) ReflectionUtils.createInstanceOfType(configuration.getString(propKey));
        } else {
            throw new UnitilsException("Missing configuration for " + propKey);
        }
    }
}
