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
package org.unitils.database;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.reset;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;
import org.unitils.database.config.DataSourceFactory;
import org.unitils.spring.SpringModule;

/**
 * Base class for tests that verify the transactional behavior of the database
 * module
 * 
 * @author Flip Neven
 * @author Tim Ducheyne
 */
abstract public class DatabaseModuleTransactionalTest extends TestCase {

	protected DatabaseModule databaseModule;

	protected static MockDataSource mockDataSource;

	protected static Connection mockConnection1 = createMock(Connection.class);

	protected static Connection mockConnection2 = createMock(Connection.class);

	protected Properties configuration;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mockDataSource = new MockDataSource();
		reset(mockConnection1);
		reset(mockConnection2);

		configuration = new ConfigurationLoader().loadConfiguration();
		configuration.setProperty("org.unitils.database.config.DataSourceFactory.implClassName", MockDataSourceFactory.class.getName());
	}

	/**
	 * Mock DataSourceFactory, that returns the static mockDataSource
	 */
	public static class MockDataSourceFactory implements DataSourceFactory {

		public void init(Properties configuration) {
		}

		public DataSource createDataSource() {
			return mockDataSource;
		}
	}

	/**
	 * Mock DataSource, that returns connection1 when it is called the first
	 * time, and connection2 when it is called the second time, in order to
	 * simulate a connection pool.
	 */
	public static class MockDataSource implements DataSource {

		boolean firstTime = true;

		public Connection getConnection() throws SQLException {
			if (firstTime) {
				firstTime = false;
				return mockConnection1;
			} else {
				return mockConnection2;
			}
		}

		public Connection getConnection(String arg0, String arg1) throws SQLException {
			return null;
		}

		public PrintWriter getLogWriter() throws SQLException {
			return null;
		}

		public int getLoginTimeout() throws SQLException {
			return 0;
		}

		public void setLogWriter(PrintWriter arg0) throws SQLException {
		}

		public void setLoginTimeout(int arg0) throws SQLException {
		}
	}

	protected DatabaseModule getDatabaseModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
	}

	protected SpringModule getSpringModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
	}

}
