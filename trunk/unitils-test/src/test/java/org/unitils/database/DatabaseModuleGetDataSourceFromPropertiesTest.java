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
package org.unitils.database;

import org.dbmaintain.database.DatabaseException;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.assertNotNull;
import static org.unitils.database.DatabaseModule.PROPERTY_UPDATEDATABASESCHEMA_ENABLED;

/**
 * Tests for the DatabaseModule
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleGetDataSourceFromPropertiesTest extends UnitilsJUnit4 {

    /* Tested object */
    private DatabaseModule databaseModule = new DatabaseModule();

    private Properties configuration;


    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, "false");

    }


    @Test
    public void getDefaultDatabase() throws Exception {
        addConfigForDefaultDatabase(configuration);
        databaseModule.init(configuration);

        DataSource dataSource = databaseModule.getDataSource(null, null);
        assertNotNull(dataSource);
    }

    @Test(expected = DatabaseException.class)
    public void noDefaultDatabaseConfigured() throws Exception {
        clearConfigForDefaultDatabase(configuration);
        databaseModule.init(configuration);

        DataSource dataSource = databaseModule.getDataSource(null, null);
        assertNotNull(dataSource);
    }

    @Test
    public void namedDatabase() throws Exception {
        addConfigForDatabase("myDatabase", configuration);
        databaseModule.init(configuration);

        DataSource dataSource = databaseModule.getDataSource("myDatabase", null);
        assertNotNull(dataSource);
    }

    @Test(expected = DatabaseException.class)
    public void unknownDatabaseName() throws Exception {
        addConfigForDatabase("myDatabase", configuration);
        databaseModule.init(configuration);

        DataSource dataSource = databaseModule.getDataSource("xxxx", null);
        assertNotNull(dataSource);
    }


    private void addConfigForDefaultDatabase(Properties configuration) {
        configuration.setProperty(PROPERTY_DRIVERCLASSNAME, "org.hsqldb.jdbcDriver");
        configuration.setProperty(PROPERTY_URL, "jdbc:hsqldb:mem:unitils");
        configuration.setProperty(PROPERTY_USERNAME, "sa");
        configuration.setProperty(PROPERTY_PASSWORD, "");
    }

    private void addConfigForDatabase(String databaseName, Properties configuration) {
        configuration.setProperty(PROPERTY_DATABASE_NAMES, databaseName);
        configuration.setProperty(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_DRIVERCLASSNAME_END, "org.hsqldb.jdbcDriver");
        configuration.setProperty(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_URL_END, "jdbc:hsqldb:mem:unitils");
        configuration.setProperty(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_USERNAME_END, "sa");
        configuration.setProperty(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_PASSWORD_END, "");
    }

    private void clearConfigForDefaultDatabase(Properties configuration) {
        configuration.remove(PROPERTY_DRIVERCLASSNAME);
        configuration.remove(PROPERTY_URL);
        configuration.remove(PROPERTY_USERNAME);
        configuration.remove(PROPERTY_PASSWORD);
    }
}
