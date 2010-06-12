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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

import static org.junit.Assert.assertTrue;
import static org.unitils.core.dbsupport.DbSupportFactory.PROPKEY_DATABASE_SCHEMA_NAMES;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.DataSetModule.PROPKEY_XSD_TARGETDIRNAME;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.deleteDirectory;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetXSDGeneratorTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DataSetXSDGeneratorTest.class);

    /* The target directory for the test xsd files */
    private File xsdDirectory;

    /* DataSource for the test database. */
    @TestDataSource
    private DataSource dataSource = null;

    /* True if current test is not for the current dialect */
    private boolean disabled;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPKEY_DATABASE_DIALECT, configuration));
        if (disabled) {
            return;
        }

        xsdDirectory = new File(System.getProperty("java.io.tmpdir"), "DataSetXSDGeneratorTest");
        if (xsdDirectory.exists()) {
            deleteDirectory(xsdDirectory);
        }

        dropTestTables();
        createTestTables();
    }


    /**
     * Clean-up test database.
     */
    @After
    public void tearDown() throws Exception {
        if (disabled) {
            return;
        }
        resetConfiguration();
        dropTestTables();
        try {
            deleteDirectory(xsdDirectory);
        } catch (Exception e) {
            // ignore
        }
    }


    @Test
    public void targetFolderSpecifiedAsArgument() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        DataSetXSDGenerator.generateDataSetXSDs(xsdDirectory);

        // check content of general dataset xsd
        File dataSetXsd = new File(xsdDirectory, "dataset.xsd");
        assertTrue(dataSetXsd.length() > 0);
    }

    @Test
    public void targetFolderConfiguredInConfiguration() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        setTargetDirInUnitilsConfiguration();
        DataSetXSDGenerator.generateDataSetXSDs();

        // check content of general dataset xsd
        File dataSetXsd = new File(xsdDirectory, "dataset.xsd");
        assertTrue(dataSetXsd.length() > 0);
    }

    @Test(expected = UnitilsException.class)
    public void targetDirNotFoundInConfiguration() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        DataSetXSDGenerator.generateDataSetXSDs();
    }


    private void setTargetDirInUnitilsConfiguration() {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPKEY_DATABASE_SCHEMA_NAMES, "PUBLIC, SCHEMA_A");
        configuration.setProperty(PROPKEY_XSD_TARGETDIRNAME, xsdDirectory.getPath());
        setUnitilsConfiguration(configuration);
    }

    private void resetConfiguration() {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        setUnitilsConfiguration(configuration);
    }

    private void setUnitilsConfiguration(Properties configuration) {
        DataSetModule dataSetModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DataSetModule.class);
        dataSetModule.init(configuration);
        dataSetModule.afterInit();
    }

    /**
     * Creates the test tables.
     */
    private void createTestTables() {
        executeUpdate("create table TABLE_1(columnA int not null identity, columnB varchar(1) not null, columnC varchar(1))", dataSource);
    }


    /**
     * Removes the test database tables
     */
    private void dropTestTables() {
        executeUpdateQuietly("drop table TABLE_1", dataSource);
    }
}