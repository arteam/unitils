/*
 * Copyright Unitils.org
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
package org.unitils.database.transaction;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.database.datasource.impl.DefaultDataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.*;
import static org.unitils.database.transaction.UnitilsDatabaseManager.PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDatabaseManagerGetDataSourceFromPropertiesTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsDatabaseManager unitilsDatabaseManager;

    private Properties configuration;

    @Before
    public void initialize() {
        configuration = new ConfigurationLoader().loadConfiguration();
    }


    @Test
    public void dataSourceForDatabaseWithName() throws Exception {
        configuration.setProperty(PROPERTY_DATABASE_NAMES, "database1, database2");
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        DataSource dataSource = unitilsDatabaseManager.getDataSource("database1", null);
        assertNotNull(dataSource.getConnection());
    }

    @Test
    public void defaultDataSource() throws Exception {
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        DataSource dataSource = unitilsDatabaseManager.getDataSource(null, null);
        assertNotNull(dataSource.getConnection());
    }

    @Test
    public void unknownDatabaseName() throws Exception {
        try {
            unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

            unitilsDatabaseManager.getDataSource("xxxx", null);
            fail("DatabaseException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Unable to create data source for database name xxxx"));
        }
    }

    @Test
    public void unknownDefaultDatabase() throws Exception {
        try {
            configuration.remove(PROPERTY_DRIVERCLASSNAME);
            configuration.remove(PROPERTY_URL);
            configuration.remove(PROPERTY_USERNAME);
            configuration.remove(PROPERTY_PASSWORD);
            unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

            unitilsDatabaseManager.getDataSource(null, null);
            fail("DatabaseException expected");
        } catch (UnitilsException e) {
            e.printStackTrace();
            assertTrue(e.getMessage().contains("Unable to create data source for the default database"));
        }
    }


    @Test
    public void dataSourceWrappedInTransactionAwareProxyByDefault() throws Exception {
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        DataSource dataSource = unitilsDatabaseManager.getDataSource(null, null);
        assertTrue(dataSource instanceof TransactionAwareDataSourceProxy);
    }

    @Test
    public void disableWrappingInTransactionAwareProxy() throws Exception {
        configuration.setProperty(PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY, "false");
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        DataSource dataSource = unitilsDatabaseManager.getDataSource(null, null);
        assertFalse(dataSource instanceof TransactionAwareDataSourceProxy);
    }

}
