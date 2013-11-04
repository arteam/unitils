package org.unitils.database;

import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.core.util.ConfigUtils;
import org.unitils.database.config.DataSourceFactory;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.database.transaction.TransactionHandler;
import org.unitils.database.transaction.UnitilsTransactionManager;
import org.unitils.dbmaintainer.DBMaintainer;
import org.unitils.dbmaintainer.clean.DBCleaner;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.dbmaintainer.structure.SequenceUpdater;
import org.unitils.dbmaintainer.util.DatabaseAccessing;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import org.unitils.util.PropertyUtils;


/**
 * DataSourceWrapper.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public class DataSourceWrapper {
	private static final Log LOGGER = LogFactory.getLog(DataSourceWrapper.class);

	private DataSource wrappedDataSource;
	protected DatabaseConfiguration databaseConfiguration;
	private TransactionHandler transactionHandler;
	private DataSourceFactory dataSourceFactory;
	private boolean updateDatabaseSchemaEnabled;
	private Properties configuration;
	private String databaseName;

	private boolean wrapDataSourceInTransactionalProxy;

	public DataSourceWrapper(DatabaseConfiguration databaseConfiguration) {
		// Get the factory for the data source and create it
		configuration = Unitils.getInstance().getConfiguration();
		dataSourceFactory = ConfigUtils.getConfiguredInstanceOf(DataSourceFactory.class, configuration);
		dataSourceFactory.init(databaseConfiguration);
		updateDatabaseSchemaEnabled = PropertyUtils.getBoolean(DatabaseModule.PROPERTY_UPDATEDATABASESCHEMA_ENABLED, configuration);
		wrapDataSourceInTransactionalProxy = PropertyUtils.getBoolean(DatabaseModule.PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY, configuration);
		transactionHandler = new TransactionHandler();
		databaseName = databaseConfiguration.getDatabaseName();
		this.databaseConfiguration = databaseConfiguration;
	}

	/**
	 * @return A connection from the data source, not null
	 */
	public Connection getConnection() {
		try {
			return DataSourceUtils.getConnection(wrappedDataSource);
		} catch (Exception e) {
			throw new UnitilsException("Unable to connect to database for " + databaseConfiguration + ".", e);
		}
	}

	/**
	 * Returns the <code>DataSource</code> that provides connection to the unit test database. When invoked the first
	 * time, the DBMaintainer is invoked to make sure the test database is up-to-date (if database updating is enabled)
	 * If the property {@link #PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY} has been set to true, the <code>DataSource</code>
	 * returned will make sure that, for the duration of a transaction, the same <code>java.sql.Connection</code> is returned,
	 * and that invocations of the close() method of these connections are suppressed.
	 *
	 * @param testObject The test instance, not null
	 * @param wrapDataSourceInTransactionalProxy 
	 * @return The <code>DataSource</code> (default database).
	 */
	public DataSource getTransactionalDataSourceAndActivateTransactionIfNeeded(Object testObject) {
		if (wrapDataSourceInTransactionalProxy) {
			return transactionHandler.getTransactionManager().getTransactionalDataSource(getDataSourceAndActivateTransactionIfNeeded());
		}
		return getDataSourceAndActivateTransactionIfNeeded();
	} 

	/**
	 * Creates a datasource by using the factory that is defined by the dataSourceFactory.className property
	 *
	 * @return the datasource
	 */
	public DataSource createDataSource() {
		// Get the factory for the data source and create it
		DataSourceFactory dataSourceFactory = ConfigUtils.getConfiguredInstanceOf(DataSourceFactory.class, configuration);
		dataSourceFactory.init(configuration, databaseName);
		DataSource dataSource = dataSourceFactory.createDataSource();

		// Call the database maintainer if enabled
		if (updateDatabaseSchemaEnabled) {
			updateDatabase(new DefaultSQLHandler(dataSource));
		}
		return dataSource;
	}

	/**
	 * Assigns the <code>TestDataSource</code> to every field annotated with {@link TestDataSource} and calls all methods
	 * annotated with {@link TestDataSource}
	 *
	 * @param testObject The test instance, not null
	 */
	public void injectDataSource(Set<Field> fields, Set<Method> methods, Object obj) {
		if (fields.isEmpty() && methods.isEmpty()) {
			// Nothing to do. Jump out to make sure that we don't try to instantiate the DataSource
			return;
		}
		setFieldAndSetterValue(obj, fields, methods, getTransactionalDataSourceAndActivateTransactionIfNeeded(getTestObject()));
	}
	/**
	 * Determines whether the test database is outdated and, if this is the case, updates the database with the
	 * latest changes. See {@link DBMaintainer} for more information.
	 */
	public void updateDatabase() {
		updateDatabase(getDefaultSqlHandler());
	}


	/**
	 * Determines whether the test database is outdated and, if that is the case, updates the database with the
	 * latest changes.
	 *
	 * @param sqlHandler SQLHandler that needs to be used for the database updates
	 * @see {@link DBMaintainer}
	 */
	public void updateDatabase(SQLHandler sqlHandler) {
		LOGGER.info("Checking if database has to be updated.");
		DBMaintainer dbMaintainer = new DBMaintainer(configuration, sqlHandler, databaseConfiguration.getDialect());
		dbMaintainer.updateDatabase();
	}

	/**
	 * @return The default SQLHandler, which simply executes the sql statements on the unitils-configured
	 *         test database
	 */
	protected SQLHandler getDefaultSqlHandler() {
		return new DefaultSQLHandler(getDataSourceAndActivateTransactionIfNeeded());
	}

	/**
	 * Returns the <code>DataSource</code> that provides connection to the unit test database. When invoked the first
	 * time, the DBMaintainer is invoked to make sure the test database is up-to-date (if database updating is enabled)
	 *
	 * @return The <code>DataSource</code>
	 */
	public DataSource getDataSourceAndActivateTransactionIfNeeded() {
		if (wrappedDataSource == null) {
			wrappedDataSource = createDataSource();
			activateTransactionIfNeeded();
		}
		return wrappedDataSource;
	}

	public void activateTransactionIfNeeded() {
		UnitilsTransactionManager transactionManager = transactionHandler.getTransactionManager();
		if (transactionManager != null) {
			transactionManager.activateTransactionIfNeeded(getTestObject());
		}
	}

	protected Object getTestObject() {
		return Unitils.getInstance().getTestContext().getTestObject();
	}

	public DataSource getDataSource() {
		if (wrappedDataSource == null) {
			wrappedDataSource = createDataSource();
		}
		return wrappedDataSource;
	}

	/**
	 * Updates the database version to the current version, without issuing any other update to the database.
	 * This method can be used for example after you've manually brought the database to the latest version, but
	 * the database version is not yet set to the current one. This method can also be useful for example for
	 * Reinitialising the database after having reorganised the scripts folder.
	 */
	public void resetDatabaseState() {
		resetDatabaseState(getDefaultSqlHandler());
	}

	/**
	 * Updates the database version to the current version, without issuing any other updates to the database.
	 * This method can be used for example after you've manually brought the database to the latest version, but
	 * the database version is not yet set to the current one. This method can also be useful for example for
	 * Reinitialising the database after having reorganised the scripts folder.
	 *
	 * @param sqlHandler The {@link DefaultSQLHandler} to which all commands are issued
	 */
	public void resetDatabaseState(SQLHandler sqlHandler) {
		DBMaintainer dbMaintainer = new DBMaintainer(configuration, sqlHandler, databaseName);
		dbMaintainer.resetDatabaseState();
	}

	/**
	 * Clears all configured schema's. I.e. drops all tables, views and other database objects.
	 */
	public void clearSchemas() {
		getConfiguredDatabaseTaskInstance(DBClearer.class).clearSchemas();
	}


	/**
	 * Cleans all configured schema's. I.e. removes all data from its database tables.
	 */
	public void cleanSchemas() {
		getConfiguredDatabaseTaskInstance(DBCleaner.class).cleanSchemas();
	}


	/**
	 * Disables all foreigh key and not-null constraints on the configured schema's.
	 */
	public void disableConstraints() {
		getConfiguredDatabaseTaskInstance(ConstraintsDisabler.class).disableConstraints();
	}


	/**
	 * Updates all sequences that have a value below a certain configurable treshold to become equal
	 * to this treshold
	 */
	public void updateSequences() {
		getConfiguredDatabaseTaskInstance(SequenceUpdater.class).updateSequences();
	}

	/**
	 * @return A configured instance of {@link DatabaseAccessing} of the given type
	 *
	 * @param databaseTaskType The type of database task, not null
	 */
	protected <T extends DatabaseAccessing> T getConfiguredDatabaseTaskInstance(Class<T> databaseTaskType) {
		return DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(databaseTaskType, configuration, getDefaultSqlHandler(), databaseConfiguration.getDialect());
	}

	/**
	 * Generates a definition file that defines the structure of dataset's, i.e. a XSD of DTD that
	 * describes the structure of the database.
	 */
	/*public void generateDatasetDefinition() {
        getConfiguredDatabaseTaskInstance(DataSetStructureGenerator.class).generateDataSetStructure();
    }*/

	public boolean isDataSourceLoaded() {
		return wrappedDataSource != null;
	} 
	public DatabaseConfiguration getDatabaseConfiguration() {
		return databaseConfiguration;
	}

	/**
	 * @return the databaseName
	 */
	public String getDatabaseName() {
		return databaseName;
	}

}
