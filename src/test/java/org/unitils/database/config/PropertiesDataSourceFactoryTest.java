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
package org.unitils.database.config;

import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.easymock.annotation.Mock;

/**
 * Tests for the properties data source factory.
 */
public class PropertiesDataSourceFactoryTest extends UnitilsJUnit4 {

	/* Object under test */
	private PropertiesDataSourceFactory propertiesFileDataSource;

	/* Mocked data source */
	@Mock
	private BasicDataSource mockBasicDataSource;


	/**
	 * Initializes the test
	 */
	@Before
	public void setUp() throws Exception {
		Properties configuration = new Properties();
		configuration.setProperty("database.driverClassName", "testdriver");
		configuration.setProperty("database.url", "testurl");
		configuration.setProperty("database.userName", "testusername");
		configuration.setProperty("database.password", "testpassword");

		propertiesFileDataSource = new PropertiesDataSourceFactory() {
			@Override
			protected BasicDataSource getNewDataSource() {
				return mockBasicDataSource;
			}
		};
		propertiesFileDataSource.init(configuration);
	}


	/**
	 * Test creating a data source.
	 */
	@Test
	public void testCreateDataSource() {
		// expectations
		mockBasicDataSource.setDriverClassName("testdriver");
		mockBasicDataSource.setUrl("testurl");
		mockBasicDataSource.setUsername("testusername");
		mockBasicDataSource.setPassword("testpassword");
		replay();

		propertiesFileDataSource.createDataSource();
	}

}
