package org.unitils.dbmaintainer.clean.impl;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.unitils.core.dbsupport.DbSupportFactory.getDbSupport;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBClearer.PROPKEY_PRESERVE_SCHEMAS;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBClearer.PROPKEY_PRESERVE_SEQUENCES;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBClearer.PROPKEY_PRESERVE_TABLES;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBClearer.PROPKEY_PRESERVE_VIEWS;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.DatabaseModule;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import org.unitils.util.PropertyUtils;

/**
 * Test class for the {@link DBClearer} using multiple database schemas with configuration to preserve all items. <p/>
 * This test is currenlty only implemented for HsqlDb
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBClearerMultiSchemaPreserveTest extends UnitilsJUnit4 {

	/* The logger instance for this class */
	private static Log logger = LogFactory.getLog(DefaultDBClearerMultiSchemaPreserveTest.class);

	/* DataSource for the test database, is injected */
	private DataSource dataSource = null;

	/* Tested object */
	private DefaultDBClearer defaultDbClearer;

	/* The db support for the default PUBLIC schema */
	private DbSupport dbSupportPublic;

	/* The db support for the SCHEMA_A schema */
	private DbSupport dbSupportSchemaA;

	/* The db support for the SCHEMA_B schema */
	private DbSupport dbSupportSchemaB;

	/* The db support for the SCHEMA_C schema */
	private DbSupport dbSupportSchemaC;

	/* True if current test is not for the current dialect */
	private boolean disabled;

	private static String dialect;
        
        private List<String> schemas;
	/**
	 * Configures the tested object. Creates a test table, index, view and sequence
	 */
	@Before
	public void setUp() throws Exception {
            Properties configuration = new ConfigurationLoader().loadConfiguration();
            
		dialect = PropertyUtils.getString(DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT, configuration);
        this.disabled = !"hsqldb".equals(dialect);
		if (disabled) {
			return;
		}

		initDatabaseModule(configuration);
		
		// first create database, otherwise items to preserve do not yet exist
		cleanupTestDatabase();
		createTestDatabase();

		// configure 3 schemas
		configuration.setProperty(DbSupportFactory.PROPKEY_DATABASE_SCHEMA_NAMES, "PUBLIC, SCHEMA_A, \"SCHEMA_B\", schema_c");
		SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
		dbSupportPublic = getDbSupport(configuration, sqlHandler, "PUBLIC", dialect);
		dbSupportSchemaA = getDbSupport(configuration, sqlHandler, "SCHEMA_A", dialect);
		dbSupportSchemaB = getDbSupport(configuration, sqlHandler, "SCHEMA_B", dialect);
		dbSupportSchemaC = getDbSupport(configuration, sqlHandler, "SCHEMA_C", dialect);
		// configure items to preserve
		configuration.setProperty(PROPKEY_PRESERVE_SCHEMAS, "schema_c");
		configuration.setProperty(PROPKEY_PRESERVE_TABLES, "test_table, " + dbSupportSchemaA.quoted("SCHEMA_A") + "." + dbSupportSchemaA.quoted("TEST_TABLE"));
		configuration.setProperty(PROPKEY_PRESERVE_VIEWS, "test_view, " + "schema_a." + dbSupportSchemaA.quoted("TEST_VIEW"));
		configuration.setProperty(PROPKEY_PRESERVE_SEQUENCES, "test_sequence, " + dbSupportSchemaA.quoted("SCHEMA_A") + ".test_sequence");
		// create clearer instance
		defaultDbClearer = new DefaultDBClearer();
                schemas = PropertyUtils.getStringList("database.schemaNames", configuration);
		defaultDbClearer.init(configuration, sqlHandler, dialect, schemas);
	}


	/**
	 * Removes all test tables.
	 */
	@After
	public void tearDown() throws Exception {
		if (disabled) {
			return;
		}
		cleanupTestDatabase();
	}


	/**
	 * Checks if the tables are correctly dropped.
	 */
	@Test
	public void testClearDatabase_tables() throws Exception {
		if (disabled) {
			logger.warn("Test is not for current dialect. Skipping test.");
			return;
		}
		assertEquals(1, dbSupportPublic.getTableNames().size());
		assertEquals(1, dbSupportSchemaA.getTableNames().size());
		assertEquals(1, dbSupportSchemaB.getTableNames().size());
		defaultDbClearer.clearSchemas();
		assertEquals(1, dbSupportPublic.getTableNames().size());
		assertEquals(1, dbSupportSchemaA.getTableNames().size());
		assertEquals(0, dbSupportSchemaB.getTableNames().size());
		assertEquals(1, dbSupportSchemaC.getTableNames().size());
	}


	/**
	 * Checks if the views are correctly dropped
	 */
	@Test
	public void testClearDatabase_views() throws Exception {
		if (disabled) {
			logger.warn("Test is not for current dialect. Skipping test.");
			return;
		}
		assertEquals(1, dbSupportPublic.getViewNames().size());
		assertEquals(1, dbSupportSchemaA.getViewNames().size());
		assertEquals(1, dbSupportSchemaB.getViewNames().size());
		defaultDbClearer.clearSchemas();
		assertEquals(1, dbSupportPublic.getViewNames().size());
		assertEquals(1, dbSupportSchemaA.getViewNames().size());
		assertEquals(0, dbSupportSchemaB.getViewNames().size());
		assertEquals(1, dbSupportSchemaC.getViewNames().size());
	}


	/**
	 * Tests if the triggers are correctly dropped
	 */
	@Test
	public void testClearDatabase_sequences() throws Exception {
		if (disabled) {
			logger.warn("Test is not for current dialect. Skipping test.");
			return;
		}
		assertEquals(1, dbSupportPublic.getSequenceNames().size());
		assertEquals(1, dbSupportSchemaA.getSequenceNames().size());
		assertEquals(1, dbSupportSchemaB.getSequenceNames().size());
		defaultDbClearer.clearSchemas();
		assertEquals(1, dbSupportPublic.getSequenceNames().size());
		assertEquals(1, dbSupportSchemaA.getSequenceNames().size());
		assertEquals(0, dbSupportSchemaB.getSequenceNames().size());
		assertEquals(1, dbSupportSchemaC.getSequenceNames().size());
	}


	/**
	 * Creates all test database structures (view, tables...)
	 */
	private void createTestDatabase() throws Exception {
		// create schemas
		executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA", dataSource);
		executeUpdate("create schema SCHEMA_B AUTHORIZATION DBA", dataSource);
		executeUpdate("create schema SCHEMA_C AUTHORIZATION DBA", dataSource);
		// create tables
		executeUpdate("create table TEST_TABLE (col1 varchar(100))", dataSource);
		executeUpdate("create table SCHEMA_A.TEST_TABLE (col1 varchar(100))", dataSource);
		executeUpdate("create table SCHEMA_B.TEST_TABLE (col1 varchar(100))", dataSource);
		executeUpdate("create table SCHEMA_C.TEST_TABLE (col1 varchar(100))", dataSource);
		// create views
		executeUpdate("create view TEST_VIEW as select col1 from TEST_TABLE", dataSource);
		executeUpdate("create view SCHEMA_A.TEST_VIEW as select col1 from SCHEMA_A.TEST_TABLE", dataSource);
		executeUpdate("create view SCHEMA_B.TEST_VIEW as select col1 from SCHEMA_B.TEST_TABLE", dataSource);
		executeUpdate("create view SCHEMA_C.TEST_VIEW as select col1 from SCHEMA_C.TEST_TABLE", dataSource);
		// create sequences
		executeUpdate("create sequence TEST_SEQUENCE", dataSource);
		executeUpdate("create sequence SCHEMA_A.TEST_SEQUENCE", dataSource);
		executeUpdate("create sequence SCHEMA_B.TEST_SEQUENCE", dataSource);
		executeUpdate("create sequence SCHEMA_C.TEST_SEQUENCE", dataSource);
	}


	/**
	 * Drops all created test database structures (views, tables...)
	 */
	private void cleanupTestDatabase() throws Exception {
		// drop sequences
		executeUpdateQuietly("drop sequence TEST_SEQUENCE", dataSource);
		executeUpdateQuietly("drop sequence SCHEMA_A.TEST_SEQUENCE", dataSource);
		executeUpdateQuietly("drop sequence SCHEMA_B.TEST_SEQUENCE", dataSource);
		executeUpdateQuietly("drop sequence SCHEMA_C.TEST_SEQUENCE", dataSource);
		// drop views
		executeUpdateQuietly("drop view TEST_VIEW", dataSource);
		executeUpdateQuietly("drop view SCHEMA_A.TEST_VIEW", dataSource);
		executeUpdateQuietly("drop view SCHEMA_B.TEST_VIEW", dataSource);
		executeUpdateQuietly("drop view SCHEMA_C.TEST_VIEW", dataSource);
		// drop tables
		executeUpdateQuietly("drop table TEST_TABLE", dataSource);
		executeUpdateQuietly("drop table SCHEMA_A.TEST_TABLE", dataSource);
		executeUpdateQuietly("drop table SCHEMA_B.TEST_TABLE", dataSource);
		executeUpdateQuietly("drop table SCHEMA_C.TEST_TABLE", dataSource);
		// drop schemas
		executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
		executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
		executeUpdateQuietly("drop schema SCHEMA_C", dataSource);
	}

	private void initDatabaseModule(Properties configuration) {
	    configuration.setProperty("dbMaintainer.autoCreateExecutedScriptsTable", "false");
        configuration.setProperty("dbMaintainer.autoCreateDbMaintainScriptsTable", "false");
        configuration.setProperty("updateDataBaseSchema.enabled", "false");
        configuration.setProperty("dbMaintainer.autoCreateExecutedScriptsTable", "false");
        configuration.setProperty("dataSetStructureGenerator.xsd.dirName", "");
        
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        databaseModule.init(configuration);
        databaseModule.afterInit();
        dataSource = databaseModule.getWrapper("").getTransactionalDataSourceAndActivateTransactionIfNeeded(this);
	}
}
