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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.*;
import static org.unitils.database.DatabaseModule.PROPERTY_UPDATEDATABASESCHEMA_ENABLED;

/**
 * Tests for the DatabaseModule
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseUnitilsGetDataSourceTest extends UnitilsJUnit4 {

    /* Tested object */
    private DatabaseModule databaseModule;

    private Properties configuration;


    @Before
    public void initialize() throws Exception {
        databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);

        configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, "false");
    }

    @After
    public void resetDataSetModule() {
        configuration = new ConfigurationLoader().loadConfiguration();
        databaseModule.init(configuration);
    }


    @Test
    public void getDefaultDatabase() throws Exception {
        databaseModule.init(configuration);

        DataSource dataSource = DatabaseUnitils.getDataSource();
        assertNotNull(dataSource);
    }

    @Test
    public void noDefaultDatabaseConfigured() throws Exception {
        clearConfigForDefaultDatabase();
        databaseModule.init(configuration);

        try {
            DatabaseUnitils.getDataSource();
            fail("Expected DatabaseException");
        } catch (DatabaseException e) {
            assertEquals("Invalid database configuration: no driver class name defined.", e.getMessage());
        }
    }

    @Test
    public void namedDatabase() throws Exception {
        configuration.setProperty(PROPERTY_DATABASE_NAMES, "myDatabase");
        databaseModule.init(configuration);

        DataSource dataSource = DatabaseUnitils.getDataSource("myDatabase");
        assertNotNull(dataSource);
    }

    @Test
    public void unknownDatabaseName() throws Exception {
        databaseModule.init(configuration);

        try {
            DatabaseUnitils.getDataSource("xxxx");
            fail("Expected DatabaseException");
        } catch (DatabaseException e) {
            assertEquals("No database configuration found for database with name xxxx.", e.getMessage());
        }
    }

    private void clearConfigForDefaultDatabase() {
        configuration.remove(PROPERTY_DRIVERCLASSNAME);
        configuration.remove(PROPERTY_URL);
        configuration.remove(PROPERTY_USERNAME);
        configuration.remove(PROPERTY_PASSWORD);
    }
}
