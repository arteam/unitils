package org.unitils.dbmaintainer.clean.impl;

import static org.junit.Assert.assertEquals;
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
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.util.TestUtils;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import org.unitils.util.PropertyUtils;

/**
 * Test class for the {@link DBClearer} using multiple database schemas with configuration to preserve all items. <p/>
 * This test is currenlty only implemented for HsqlDb
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBClearerMultiSchemaPreserveTest {

	/* The logger instance for this class */
	private static Log logger = LogFactory.getLog(DefaultDBClearerMultiSchemaPreserveTest.class);

	/* DataSource for the test database */
	private DataSource dataSource;

	/* Tested object */
	private DefaultDBClearer defaultDbClearer;

	/* The db support */
	private DbSupport dbSupport;

	/* True if current test is not for the current dialect */
	private boolean disabled;


	/**
	 * Configures the tested object. Creates a test table, index, view and sequence
	 */
	@Before
	public void setUp() throws Exception {
		Properties configuration = new ConfigurationLoader().loadConfiguration();
		this.disabled = !"hsqldb".equals(PropertyUtils.getString(DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT, configuration));
		if (disabled) {
			return;
		}
		
		// configure 3 schemas
		configuration.setProperty("database.schemaNames", "PUBLIC, SCHEMA_A, \"SCHEMA_B\", schema_c");

		dbSupport = TestUtils.getDefaultDbSupport(configuration);
		dataSource = dbSupport.getDataSource();

		// configure items to preserve
		configuration.setProperty(PROPKEY_PRESERVE_SCHEMAS, "schema_c");
		configuration.setProperty(PROPKEY_PRESERVE_TABLES, "test_table, " + dbSupport.quoted("SCHEMA_A") + "." + dbSupport.quoted("TEST_TABLE"));
		configuration.setProperty(PROPKEY_PRESERVE_VIEWS, "test_view, " + "schema_a." + dbSupport.quoted("TEST_VIEW"));
		configuration.setProperty(PROPKEY_PRESERVE_SEQUENCES, "test_sequence, " + dbSupport.quoted("SCHEMA_A") + ".test_sequence");
		
		
		// first create database, otherwise items to preserve do not yet exist
		cleanupTestDatabase();
		createTestDatabase();

		// create clearer instance
		defaultDbClearer = TestUtils.getDefaultDBClearer(configuration, dbSupport);
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
		assertEquals(1, dbSupport.getTableNames("PUBLIC").size());
		assertEquals(1, dbSupport.getTableNames("SCHEMA_A").size());
		assertEquals(1, dbSupport.getTableNames("SCHEMA_B").size());
		defaultDbClearer.clearSchemas();
		assertEquals(1, dbSupport.getTableNames("PUBLIC").size());
		assertEquals(1, dbSupport.getTableNames("SCHEMA_A").size());
		assertEquals(0, dbSupport.getTableNames("SCHEMA_B").size());
		assertEquals(1, dbSupport.getTableNames("SCHEMA_C").size());
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
		assertEquals(1, dbSupport.getViewNames("PUBLIC").size());
		assertEquals(1, dbSupport.getViewNames("SCHEMA_A").size());
		assertEquals(1, dbSupport.getViewNames("SCHEMA_B").size());
		defaultDbClearer.clearSchemas();
		assertEquals(1, dbSupport.getViewNames("PUBLIC").size());
		assertEquals(1, dbSupport.getViewNames("SCHEMA_A").size());
		assertEquals(0, dbSupport.getViewNames("SCHEMA_B").size());
		assertEquals(1, dbSupport.getViewNames("SCHEMA_C").size());
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
		assertEquals(1, dbSupport.getSequenceNames("PUBLIC").size());
		assertEquals(1, dbSupport.getSequenceNames("SCHEMA_A").size());
		assertEquals(1, dbSupport.getSequenceNames("SCHEMA_B").size());
		defaultDbClearer.clearSchemas();
		assertEquals(1, dbSupport.getSequenceNames("PUBLIC").size());
		assertEquals(1, dbSupport.getSequenceNames("SCHEMA_A").size());
		assertEquals(0, dbSupport.getSequenceNames("SCHEMA_B").size());
		assertEquals(1, dbSupport.getSequenceNames("SCHEMA_C").size());
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

}
