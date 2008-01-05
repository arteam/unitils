/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.dbmaintainer.version.impl;

import static org.apache.commons.lang.time.DateUtils.parseDate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.reflectionassert.ReflectionAssert.assertRefEquals;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.version.Version;
import static org.unitils.dbmaintainer.version.impl.DBVersionSource.PROPKEY_AUTO_CREATE_VERSION_TABLE;

/**
 * Test class for {@link org.unitils.dbmaintainer.version.impl.DBVersionSource}. The implementation is tested using a
 * test database. The dbms that is used depends on the database configuration in test/resources/unitils.properties
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBVersionSourceTest extends UnitilsJUnit4 {

	/* The tested instance */
	private DBVersionSource dbVersionSource;

	/* The tested instance with auto-create configured */
	private DBVersionSource dbVersionSourceAutoCreate;

	/* The dataSource */
	@TestDataSource
	private DataSource dataSource = null;


	/**
	 * Initialize test fixture and creates a test version table.
	 */
	@Before
	public void setUp() throws Exception {
		Properties configuration = new ConfigurationLoader().loadConfiguration();
		SQLHandler sqlHandler = new SQLHandler(dataSource);
		
		configuration.setProperty(PROPKEY_AUTO_CREATE_VERSION_TABLE, "false");
		dbVersionSource = new DBVersionSource();
		dbVersionSource.init(configuration, sqlHandler);

		configuration.setProperty(PROPKEY_AUTO_CREATE_VERSION_TABLE, "true");
		dbVersionSourceAutoCreate = new DBVersionSource();
		dbVersionSourceAutoCreate.init(configuration, sqlHandler);

		dropVersionTable();
		createVersionTable();
	}


	/**
	 * Cleanup by dropping the test version table.
	 */
	@After
	public void tearDown() throws Exception {
		dropVersionTable();
	}


	/**
	 * Test setting and getting version
	 */
	@Test
	public void testGetAndSetDBVersion() throws Exception {
		Version version = new Version(3L, parseDate("2006-10-08 12:00", new String[] { "yyyy-MM-dd hh:mm" }).getTime());

		dbVersionSource.setDbVersion(version);
		Version result = dbVersionSource.getDbVersion();
		assertRefEquals(version, result);
	}


	/**
	 * Tests getting the version, but no version table yet (e.g. first use)
	 */
	@Test(expected = UnitilsException.class)
	public void testGetDBVersion_noVersionTable() throws Exception {
		dropVersionTable();
		dbVersionSource.getDbVersion();
	}


	/**
	 * Tests getting the version, but no version table yet and auto-create is true.
	 */
	@Test
	public void testGetDBVersion_noVersionTableAutoCreate() throws Exception {
		dropVersionTable();

		Version result = dbVersionSourceAutoCreate.getDbVersion();
		assertRefEquals(0L, result.getIndex());
	}


	/**
	 * Tests getting the version but table is empty.
	 */
	@Test
	public void testGetAndSetDBVersion_emptyVersionTable() throws Exception {
		clearVersionTable();
		Version version = new Version(3L, parseDate("2006-10-08 12:00", new String[] { "yyyy-MM-dd hh:mm" }).getTime());

		dbVersionSource.setDbVersion(version);
		Version result = dbVersionSource.getDbVersion();
		assertRefEquals(version, result);
	}


	/**
	 * Test whether the update succeeded value can be correclty set and retrieved, when succeeded is true
	 */
	@Test
	public void testRegisterUpdateSucceeded_succeeded() throws Exception {
		dbVersionSource.setUpdateSucceeded(true);
		boolean result = dbVersionSource.isLastUpdateSucceeded();

		assertTrue(result);
	}


	/**
	 * Test whether the update succeeded value can be correclty set and retrieved, when succeeded is false
	 */
	@Test
	public void testRegisterUpdateSucceeded_notSucceeded() throws Exception {
		dbVersionSource.setUpdateSucceeded(false);
		boolean result = dbVersionSource.isLastUpdateSucceeded();

		assertFalse(result);
	}


	/**
	 * Utility method to create the test version table.
	 */
	private void createVersionTable() throws SQLException {
		executeUpdate(dbVersionSource.getCreateVersionTableStatement(), dataSource);
	}


	/**
	 * Utility method to drop the test version table.
	 */
	private void dropVersionTable() throws SQLException {
		executeUpdateQuietly("drop table db_version", dataSource);
	}


	/**
	 * Utility method to clear the test version table.
	 */
	private void clearVersionTable() throws SQLException {
		executeUpdate("delete from db_version", dataSource);
	}
}
