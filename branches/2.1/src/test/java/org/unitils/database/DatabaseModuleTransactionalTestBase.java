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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Before;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.spring.SpringModule;

/**
 * Base class for tests that verify the transactional behavior of the database module
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class DatabaseModuleTransactionalTestBase {

    /**
     * Test datasource that returns connection 1 and 2
     */
    protected static DataSource mockDataSource = createMock(DataSource.class);

    /**
     * Test connection 1
     */
    protected static Connection mockConnection1 = createMock(Connection.class);

    /**
     * Test connection 2
     */
    protected static Connection mockConnection2 = createMock(Connection.class);
    
    /**
     * Mock DbSupport that returns mock datasource
     */
    protected static DbSupport mockDbSupport = createMock(DbSupport.class);

    /**
     * The unitils configuration
     */
    protected Properties configuration;


    /**
     * Initializes the mocked datasource and connections.
     */
    @Before
    public void initialize() throws Exception {
        reset(mockConnection1);
        reset(mockConnection2);
        reset(mockDataSource);
        expect(mockDbSupport.getDataSource()).andStubReturn(mockDataSource);
        expect(mockDataSource.getConnection()).andReturn(mockConnection1);
        expect(mockDataSource.getConnection()).andReturn(mockConnection2);
        replay(mockDataSource);

        configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(DbSupportFactory.class.getName() + ".implClassName", MockDbSupportFactory.class.getName());
    }


    /**
     * Mock DataSourceFactory, that returns the static mockDataSource
     */
    public static class MockDbSupportFactory implements DbSupportFactory {

		public DbSupport createDbSupport(String databaseName, SQLHandler sqlHandler) {
			return null;
		}

		public DbSupport createDefaultDbSupport(SQLHandler sqlHandler) {
			return null;
		}

		public void init(Properties configuration) {
		}

    }
    
    

    /**
     * Utility method to retrieve the database module instance.
     *
     * @return The database module, not null
     */
    protected DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
    }


    /**
     * Utility method to retrieve the spring module instance.
     *
     * @return The spring module, not null
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }

}
