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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCRIPT_LOCATIONS;
import static org.junit.Assert.assertEquals;
import static org.unitils.database.DatabaseModule.PROPERTY_UPDATEDATABASESCHEMA_ENABLED;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;

/**
 * Tests for the DatabaseModule
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleUpdateDatabaseListenerTest extends UnitilsJUnit4 {

    /* Tested object */
    private DatabaseModule databaseModule = new DatabaseModule();

    @TestDataSource
    protected DataSource dataSource;


    @Before
    public void initialize() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();

        File scriptLocation = getScriptLocation();
        configuration.setProperty(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, "true");
        configuration.setProperty(PROPERTY_SCRIPT_LOCATIONS, scriptLocation.getPath());
        configuration.setProperty(PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE, "true");

        databaseModule.init(configuration);
    }

    @Before
    @After
    public void dropTestTable() {
        executeUpdateQuietly("drop table test", dataSource);
    }


    @Test
    public void listenerCalledOnUpdateDatabase() throws Exception {
        TestDatabaseUpdateListener databaseUpdateListener = new TestDatabaseUpdateListener();
        databaseModule.registerDatabaseUpdateListener(databaseUpdateListener);

        databaseModule.updateDatabaseIfNeeded(null);
        assertEquals(1, databaseUpdateListener.callCount);
    }

    @Test
    public void listenerNotCalledWhenDatabaseIsUpToDate() throws Exception {
        databaseModule.updateDatabaseIfNeeded(null);

        TestDatabaseUpdateListener databaseUpdateListener = new TestDatabaseUpdateListener();
        databaseModule.registerDatabaseUpdateListener(databaseUpdateListener);

        databaseModule.updateDatabaseIfNeeded(null);
        assertEquals(0, databaseUpdateListener.callCount);
    }

    @Test
    public void unregisterListener() throws Exception {
        TestDatabaseUpdateListener databaseUpdateListener = new TestDatabaseUpdateListener();
        databaseModule.registerDatabaseUpdateListener(databaseUpdateListener);

        databaseModule.unregisterDatabaseUpdateListener(databaseUpdateListener);
        databaseModule.updateDatabaseIfNeeded(null);
        assertEquals(0, databaseUpdateListener.callCount);
    }


    private File getScriptLocation() throws URISyntaxException {
        URL script = getClass().getResource("/org/unitils/database/scripts/01_create-test-table.sql");
        return new File(script.toURI()).getParentFile();
    }


    private static class TestDatabaseUpdateListener implements DatabaseUpdateListener {

        public int callCount;

        public void databaseWasUpdated() {
            callCount++;
        }
    }
}
