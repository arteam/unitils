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
package org.unitils.dataset;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;
import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;

import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.DataSetUnitils.invalidateCachedDatabaseMetaData;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public abstract class MultiDbDataSetTestBase extends DataSetTestBase {

    @TestDataSource("unitils1")
    DataSource dataSource1;

    @TestDataSource("unitils2")
    DataSource dataSource2;

    @BeforeClass
    public static void initUnitilsMultiDb() {
        System.setProperty(ConfigurationLoader.PROPKEY_CUSTOM_CONFIGURATION, "unitils-multidb.properties");
        Unitils.getInstance().init();
    }

    @AfterClass
    public static void resetUnitils() {
        System.getProperties().remove(ConfigurationLoader.PROPKEY_CUSTOM_CONFIGURATION);
        Unitils.getInstance().init();
    }

    @Before
    public void createTestTables() {
        createTestTables(dataSource1);
        createTestTables(dataSource2);
    }

    @After
    public void dropTestTables() {
        dropTestTables(dataSource1);
        dropTestTables(dataSource2);
    }
}
