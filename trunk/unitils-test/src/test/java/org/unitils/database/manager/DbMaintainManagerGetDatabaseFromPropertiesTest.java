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
package org.unitils.database.manager;

import org.dbmaintain.database.Database;
import org.dbmaintain.database.DatabaseException;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.TestDataSourceFactory;

import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbMaintainManagerGetDatabaseFromPropertiesTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainManager dbMaintainManager;

    @Before
    public void initialize() {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPERTY_DRIVERCLASSNAME, "org.hsqldb.jdbcDriver");
        configuration.setProperty(PROPERTY_URL, "jdbc:hsqldb:mem:unitils");
        configuration.setProperty(PROPERTY_USERNAME, "sa");
        configuration.setProperty(PROPERTY_PASSWORD, "");
        configuration.setProperty(PROPERTY_SCHEMANAMES, "public");
        configuration.setProperty(PROPERTY_DATABASE_NAMES, "database1, database2");

        dbMaintainManager = new DbMaintainManager(configuration, false, new TestDataSourceFactory(), new UnitilsTransactionManager());
    }


    @Test
    public void getDatabaseWithName() {
        Database database = dbMaintainManager.getDatabase("database2");
        assertEquals("database2", database.getDatabaseName());
    }

    @Test
    public void getDefaultDatabase() {
        Database database = dbMaintainManager.getDatabase(null);
        assertEquals("database1", database.getDatabaseName());
    }

    @Test(expected = DatabaseException.class)
    public void unknownDatabaseName() {
        dbMaintainManager.getDatabase("xxxx");
    }

    @Test
    public void sameInstanceReturned() {
        Database database1 = dbMaintainManager.getDatabase(null);
        Database database2 = dbMaintainManager.getDatabase(null);
        assertSame(database1, database2);
    }
}
