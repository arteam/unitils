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

import org.dbmaintain.database.Database;
import org.dbmaintain.database.DatabaseException;
import org.dbmaintain.database.Databases;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.datasource.impl.DefaultDataSourceFactory;

import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDatabaseManagerGetDatabaseTest {

    /* Tested object */
    private UnitilsDatabaseManager unitilsDatabaseManager;

    private Properties configuration;

    @Before
    public void initialize() {
        configuration = new ConfigurationLoader().loadConfiguration();
    }


    @Test
    public void getDatabaseWithName() {
        configuration.setProperty(PROPERTY_DATABASE_NAMES, "database1, database2");
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        Database database = unitilsDatabaseManager.getDatabase("database2");
        assertEquals("database2", database.getDatabaseName());
    }

    @Test
    public void getDefaultDatabase() {
        configuration.setProperty(PROPERTY_DATABASE_NAMES, "database1, database2");
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        Database database = unitilsDatabaseManager.getDatabase(null);
        assertEquals("database1", database.getDatabaseName());
    }

    @Test
    public void getDefaultDatabase_noDatabaseNamesSet() {
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        Database database = unitilsDatabaseManager.getDatabase(null);
        assertEquals("<no-name>", database.getDatabaseName());
    }

    @Test
    public void unknownDatabaseName() {
        configuration.setProperty(PROPERTY_DATABASE_NAMES, "database1, database2");
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        try {
            unitilsDatabaseManager.getDatabase("xxxx");
            Assert.fail("DatabaseException expected");
        } catch (DatabaseException e) {
            assertTrue(e.getMessage().contains("No database configured with name: xxxx"));
        }
    }

    @Test
    public void sameInstanceReturned() {
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        Database database1 = unitilsDatabaseManager.getDatabase(null);
        Database database2 = unitilsDatabaseManager.getDatabase(null);
        assertSame(database1, database2);
    }

    @Test
    public void getDatabases() {
        configuration.setProperty(PROPERTY_DATABASE_NAMES, "database1, database2");
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        Databases databases = unitilsDatabaseManager.getDatabases();
        assertEquals("database1", databases.getDatabase("database1").getDatabaseName());
        assertEquals("database2", databases.getDatabase("database2").getDatabaseName());
        assertEquals("database1", databases.getDefaultDatabase().getDatabaseName());
    }

    @Test
    public void connectionCouldNotBeCreated() {
        configuration.setProperty(PROPERTY_DRIVERCLASSNAME, "xxxxx");
        configuration.setProperty(PROPERTY_URL, "xxxxx");
        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());

        try {
            unitilsDatabaseManager.getDatabases();
        } catch (DatabaseException e) {
            assertTrue(e.getMessage().contains("Unable to connect to database. Driver: xxxxx, url: xxxxx, user: sa, password: <not shown>"));
        }
    }
}
