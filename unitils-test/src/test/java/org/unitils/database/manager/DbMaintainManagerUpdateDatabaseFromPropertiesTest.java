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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.database.TestDataSourceFactory;
import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCRIPT_LOCATIONS;
import static org.junit.Assert.fail;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;

/**
 * Tests for the DatabaseModule
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbMaintainManagerUpdateDatabaseFromPropertiesTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainManager dbMaintainManager;

    @TestDataSource
    protected DataSource dataSource;
    private Properties configuration;


    @Before
    public void initialize() throws Exception {
        configuration = new ConfigurationLoader().loadConfiguration();

        File scriptLocation = getScriptLocation();
        configuration.setProperty(PROPERTY_SCRIPT_LOCATIONS, scriptLocation.getPath());
        configuration.setProperty(PROPERTY_AUTO_CREATE_DBMAINTAIN_SCRIPTS_TABLE, "true");
    }

    @Before
    public void createTestTable() {
        dropTestTables();
    }

    @After
    public void dropTestTables() {
        executeUpdateQuietly("drop table test", dataSource);
        executeUpdateQuietly("drop table dbmaintain_scripts", dataSource);
    }


    @Test
    public void updateDatabase() throws Exception {
        dbMaintainManager = new DbMaintainManager(configuration, true, new TestDataSourceFactory(), new UnitilsTransactionManager());

        dbMaintainManager.updateDatabaseIfNeeded(null);
        assertTestTableExists();
    }

    @Test
    public void updateDatabaseDisabled() throws Exception {
        dbMaintainManager = new DbMaintainManager(configuration, false, new TestDataSourceFactory(), new UnitilsTransactionManager());

        dbMaintainManager.updateDatabaseIfNeeded(null);
        assertTestTableNotExists();
    }


    private void assertTestTableExists() {
        executeUpdate("insert into test(val) values(1)", dataSource);
    }

    private void assertTestTableNotExists() {
        try {
            executeUpdate("insert into test(val) values(1)", dataSource);
            fail("Test table exists. DbMaintain should not have been called.");
        } catch (UnitilsException e) {
            // expected
        }
    }

    private File getScriptLocation() throws URISyntaxException {
        URL script = getClass().getResource("/org/unitils/database/scripts/01_create-test-table.sql");
        return new File(script.toURI()).getParentFile();
    }

}
