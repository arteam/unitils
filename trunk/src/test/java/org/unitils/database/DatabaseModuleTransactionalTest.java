/*
 * Copyright 2006 the original author or authors.
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

import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.util.Order;
import org.unitils.dbmaintainer.util.BaseDataSourceDecorator;
import org.unitils.database.config.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class DatabaseModuleTransactionalTest extends UnitilsJUnit3 {

    protected DatabaseModule databaseModule;

    protected static MockDataSource mockDataSource;

    @Mock
    protected static Connection mockConnection1, mockConnection2;

    protected Properties configuration;


    protected void setUp() throws Exception {
        super.setUp();

        mockDataSource = new MockDataSource();

        configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty("org.unitils.database.config.DataSourceFactory.implClassName", MockDataSourceFactory.class.getName());
    }

    public static class MockDataSourceFactory implements DataSourceFactory {

        public void init(Properties configuration) {
        }

        public DataSource createDataSource() {
            return mockDataSource;
        }
    }

    public static class MockDataSource extends BaseDataSourceDecorator {

        boolean firstTime = true;

        /**
         * Creates a new instance that wraps the given <code>DataSource</code>
         */
        public MockDataSource() {
            super(null);
        }


        public Connection getConnection() throws SQLException {
            if (firstTime) {
                firstTime = false;
                return mockConnection1;
            } else {
                return mockConnection2;
            }
        }
    }
}
