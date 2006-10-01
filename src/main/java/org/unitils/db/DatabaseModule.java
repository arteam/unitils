package org.unitils.db;

import org.apache.commons.configuration.Configuration;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.UnitilsModule;
import org.unitils.dbmaintainer.config.DataSourceFactory;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.util.ReflectionUtils;
import org.unitils.util.UnitilsConfiguration;
import org.unitils.dbunit.DatabaseTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 */
public class DatabaseModule implements UnitilsModule {

    /* Property keys indicating if the database schema should be updated before performing the tests */
    private static final String PROPKEY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

    /* Property keys of the datasource factory classname */
    private static final String PROPKEY_DATASOURCEFACTORY_CLASSNAME = "dataSourceFactory.className";

    /* The pooled datasource instance */
    private DataSource dataSource;

    /* Database connection holder: ensures that if the method getCurrentConnection is always used for getting
      a connection to the database, at most one database connection exists per thread */
    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();

    /**
     * @param testClass
     * @return True if the test class is a database test, i.e. is annotated with the {@link DatabaseTest} annotation,
     * false otherwise
     */
    protected boolean isDatabaseTest(Class testClass) {
        return testClass.getAnnotation(DatabaseTest.class) != null;
    }

    /**
     * Creates a datasource by using the factory that is defined by the dataSourceFactory.className property
     *
     * @return the datasource
     */
    private DataSource createDataSource() {
        Configuration configuration = UnitilsConfiguration.getInstance();

        DataSourceFactory dataSourceFactory = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DATASOURCEFACTORY_CLASSNAME));
        dataSourceFactory.init();
        return dataSourceFactory.createDataSource();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Connection getCurrentConnection() {
        Connection currentConnection = connectionHolder.get();
        if (currentConnection == null) {
            try {
                currentConnection = getDataSource().getConnection();
            } catch (SQLException e) {
                throw new UnitilsException("Error while establishing connection to the database", e);
            }
            connectionHolder.set(currentConnection);
        }
        return currentConnection;
    }

    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link org.unitils.dbmaintainer.maintainer.DBMaintainer} for more information.
     */
    protected void updateDatabaseSchemaIfNeeded() throws StatementHandlerException {
        Configuration configuration = UnitilsConfiguration.getInstance();

        if (configuration.getBoolean(PROPKEY_UPDATEDATABASESCHEMA_ENABLED)) {
            DBMaintainer dbMaintainer = new DBMaintainer(dataSource);
            dbMaintainer.updateDatabase();
        }
    }

    public TestListener createTestListener() {
        return new DatabaseTestListener();
    }

    //todo javadoc
    // todo refactor
    private class DatabaseTestListener extends TestListener {

        public void beforeTestClass() {

            try {
                if (isDatabaseTest(Unitils.getTestContext().getTestClass()) && dataSource == null) {

                    //create the singleton datasource
                    dataSource = createDataSource();
                    //create the connection instance
                    updateDatabaseSchemaIfNeeded();
                }
            } catch (Exception e) {
                throw new UnitilsException("Error while intializing database connection", e);
            }
        }

    }
}
