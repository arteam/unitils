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
import org.unitils.core.ConfigurationLoader;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_DIALECT;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCHEMANAMES;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.DatabaseUnitils.getDefaultDatabase;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.DataSetModuleFactory.PROPKEY_XSD_TARGETDIRNAME;
import static org.unitils.testutil.TestUnitilsConfiguration.*;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.deleteDirectory;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetXSDGeneratorTest {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DataSetXSDGeneratorTest.class);

    private File xsdDirectory;
    private DataSource dataSource;
    private boolean disabled;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPERTY_DIALECT, configuration));
        if (disabled) {
            return;
        }

        xsdDirectory = new File(System.getProperty("java.io.tmpdir"), "DataSetXSDGeneratorTest");
        if (xsdDirectory.exists()) {
            deleteDirectory(xsdDirectory);
        }

        dataSource = getDefaultDatabase().getDataSource();

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
        resetUnitils();
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

    @Test
    public void targetDirNotFoundInConfiguration() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        DataSetXSDGenerator.generateDataSetXSDs();
        File dataSetXsd = new File(xsdDirectory, "dataset.xsd");
        assertFalse(dataSetXsd.exists());
    }


    private void setTargetDirInUnitilsConfiguration() {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPERTY_SCHEMANAMES, "PUBLIC, SCHEMA_A");
        configuration.setProperty(PROPKEY_XSD_TARGETDIRNAME, xsdDirectory.getPath());
        reinitializeUnitils(configuration);
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