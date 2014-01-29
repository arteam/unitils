/*
 * Copyright 2008,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbmaintainer.clean.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.core.dbsupport.DbSupportFactory.PROPKEY_DATABASE_SCHEMA_NAMES;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.database.SQLUnitils.isEmpty;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBCleaner.PROPKEY_PRESERVE_DATA_SCHEMAS;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBCleaner.PROPKEY_PRESERVE_DATA_TABLES;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import org.unitils.util.PropertyUtils;

/**
 * Test class for the DBCleaner with multiple schemas with configuration to preserve all tables. <p/> Currently this is
 * only implemented for HsqlDb.
 * 
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDBCleanerMultiSchemaPreserveTest extends UnitilsJUnit4 {

	/* The logger instance for this class */
	private static Log logger = LogFactory.getLog(DefaultDBCleanerMultiSchemaPreserveTest.class);

	/* DataSource for the test database, is injected */
	@TestDataSource
	private DataSource dataSource = null;

	/* Tested object */
	private DefaultDBCleaner defaultDbCleaner;

	/* The DbSupport object */
	private DbSupport dbSupport;

	/* True if current test is not for the current dialect */
	private boolean disabled;
	
	private static String dialect;


	/**
	 * Initializes the test fixture.
	 */
	@Before
	public void setUp() throws Exception {
		Properties configuration = new ConfigurationLoader().loadConfiguration();
		dialect = PropertyUtils.getString(PROPKEY_DATABASE_DIALECT, configuration);
		this.disabled = !"hsqldb".equals(dialect);
		if (disabled) {
			return;
		}

		// configure 3 schemas
		configuration.setProperty(PROPKEY_DATABASE_SCHEMA_NAMES, "PUBLIC, SCHEMA_A, \"SCHEMA_B\", schema_c");
		SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
		dbSupport = getDefaultDbSupport(configuration, sqlHandler, dialect);
		// items to preserve
		configuration.setProperty(PROPKEY_PRESERVE_DATA_SCHEMAS, "schema_c");
		configuration.setProperty(PROPKEY_PRESERVE_DATA_TABLES, "test, " + dbSupport.quoted("SCHEMA_A") + "." + dbSupport.quoted("TEST"));
		defaultDbCleaner = new DefaultDBCleaner();
		defaultDbCleaner.init(configuration, sqlHandler, dialect);

		dropTestTables();
		createTestTables();
	}


	/**
	 * Removes the test database tables from the test database, to avoid inference with other tests
	 */
	@After
	public void tearDown() throws Exception {
		if (disabled) {
			return;
		}
		dropTestTables();
	}


	/**
	 * Tests if the tables in all schemas are correctly cleaned.
	 */
	@Test
	public void testCleanDatabase() throws Exception {
		if (disabled) {
			logger.warn("Test is not for current dialect. Skipping test.");
			return;
		}
		assertFalse(isEmpty("TEST", dataSource));
		assertFalse(isEmpty("SCHEMA_A.TEST", dataSource));
		assertFalse(isEmpty("SCHEMA_B.TEST", dataSource));
		assertFalse(isEmpty("SCHEMA_C.TEST", dataSource));
		defaultDbCleaner.cleanSchemas();
		assertFalse(isEmpty("TEST", dataSource));
		assertFalse(isEmpty("SCHEMA_A.TEST", dataSource));
		assertTrue(isEmpty("SCHEMA_B.TEST", dataSource));
		assertFalse(isEmpty("SCHEMA_C.TEST", dataSource));
	}


	/**
	 * Creates the test tables.
	 */
	private void createTestTables() {
		// PUBLIC SCHEMA
		executeUpdate("create table TEST (dataset varchar(100))", dataSource);
		executeUpdate("insert into TEST values('test')", dataSource);
		// SCHEMA_A
		executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA", dataSource);
		executeUpdate("create table SCHEMA_A.TEST (dataset varchar(100))", dataSource);
		executeUpdate("insert into SCHEMA_A.TEST values('test')", dataSource);
		// SCHEMA_B
		executeUpdate("create schema SCHEMA_B AUTHORIZATION DBA", dataSource);
		executeUpdate("create table SCHEMA_B.TEST (dataset varchar(100))", dataSource);
		executeUpdate("insert into SCHEMA_B.TEST values('test')", dataSource);
		// SCHEMA_C
		executeUpdate("create schema SCHEMA_C AUTHORIZATION DBA", dataSource);
		executeUpdate("create table SCHEMA_C.TEST (dataset varchar(100))", dataSource);
		executeUpdate("insert into SCHEMA_C.TEST values('test')", dataSource);
	}


	/**
	 * Removes the test database tables
	 */
	private void dropTestTables() {
		executeUpdateQuietly("drop table TEST", dataSource);
		executeUpdateQuietly("drop table SCHEMA_A.TEST", dataSource);
		executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
		executeUpdateQuietly("drop table SCHEMA_B.TEST", dataSource);
		executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
		executeUpdateQuietly("drop table SCHEMA_C.TEST", dataSource);
		executeUpdateQuietly("drop schema SCHEMA_C", dataSource);
	}


}
