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
import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Tests for the DatabaseModule
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleTest extends UnitilsJUnit3 {

    /* Tested object */
    private TestDatabaseModule databaseModule;


    /**
     * Initializes the test fixture.
     */
    public void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(DatabaseModule.PROPKEY_UPDATEDATABASESCHEMA_ENABLED, "true");

        databaseModule = new TestDatabaseModule();
        databaseModule.init(configuration);
    }


    /**
     * Test the injection of the dataSource into a test object. This should also have triggered the
     * DbMaintainer.
     */
    public void testInjectDataSource() throws Exception {

        DbTest dbTest = new DbTest();
        databaseModule.injectDataSource(dbTest);

        assertNotNull(dbTest.dataSourceFromField);
        assertNotNull(dbTest.dataSourceFromMethod);
        assertSame(dbTest.dataSourceFromField, dbTest.dataSourceFromMethod);
        assertTrue(databaseModule.updateDataSchemaCalled);
    }


    /**
     * Object that plays the role of database test object in this class's tests.
     */
    public static class DbTest {

        private DataSource dataSourceFromMethod;

        @TestDataSource
        private DataSource dataSourceFromField = null;

        @TestDataSource
        public void setDataSource(DataSource dataSource) {
            this.dataSourceFromMethod = dataSource;
        }
    }


    /**
     * Database module that intercepts the updating of the database schema.
     */
    public class TestDatabaseModule extends DatabaseModule {

        private boolean updateDataSchemaCalled = false;

        public void updateDatabaseSchema() {
            updateDataSchemaCalled = true;
        }
    }

}
