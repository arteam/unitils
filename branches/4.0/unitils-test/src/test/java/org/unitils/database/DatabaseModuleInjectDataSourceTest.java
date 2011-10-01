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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.unitils.database.DatabaseModule.PROPERTY_UPDATEDATABASESCHEMA_ENABLED;

/**
 * Tests for the DatabaseModule
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleInjectDataSourceTest extends UnitilsJUnit4 {

    /* Tested object */
    private DatabaseModule databaseModule;


    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, "true");

        databaseModule = new DatabaseModule();
        databaseModule.init(configuration);
    }


    @Test
    public void testInjectDataSource() throws Exception {
        TestTarget testTarget = new TestTarget();
        databaseModule.injectDataSources(testTarget, null);

        assertNotNull(testTarget.dataSourceFromField);
        assertNotNull(testTarget.dataSourceFromMethod);
        assertSame(testTarget.dataSourceFromField, testTarget.dataSourceFromMethod);
    }


    public static class TestTarget {

        private DataSource dataSourceFromMethod;

        @TestDataSource
        private DataSource dataSourceFromField;

        @TestDataSource
        public void setDataSource(DataSource dataSource) {
            this.dataSourceFromMethod = dataSource;
        }

        public void testMethod() {
        }
    }
}
