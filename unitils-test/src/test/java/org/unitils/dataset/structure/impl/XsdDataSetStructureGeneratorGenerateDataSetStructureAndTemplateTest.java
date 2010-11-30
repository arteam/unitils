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
package org.unitils.dataset.structure.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dataset.database.DataSourceWrapperFactory;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_DIALECT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.DataSetUnitils.invalidateCachedDatabaseMetaData;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.deleteDirectory;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XsdDataSetStructureGeneratorGenerateDataSetStructureAndTemplateTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(XsdDataSetStructureGeneratorGenerateDataSetStructureAndTemplateTest.class);

    /* Tested object */
    private XsdDataSetStructureGenerator xsdDataSetStructureGenerator = new XsdDataSetStructureGenerator();

    protected File xsdDirectory;
    @TestDataSource
    protected DataSource dataSource;
    protected DataSourceWrapperFactory dataSourceWrapperFactory;
    protected boolean disabled;


    @Before
    public void initialize() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPERTY_DIALECT, configuration));
        if (disabled) {
            return;
        }

        xsdDirectory = new File(System.getProperty("java.io.tmpdir"), "XmlSchemaDatabaseStructureGeneratorTest");
        if (xsdDirectory.exists()) {
            deleteDirectory(xsdDirectory);
        }

        dataSourceWrapperFactory = new DataSourceWrapperFactory(configuration);

        dropTestTables();
        createTestTables();
        invalidateCachedDatabaseMetaData();
    }

    @After
    public void cleanUp() throws Exception {
        if (disabled) {
            return;
        }
        dropTestTables();
        try {
            deleteDirectory(xsdDirectory);
        } catch (Exception e) {
            // ignore
        }
        invalidateCachedDatabaseMetaData();
    }


    @Test
    public void targetFolderSpecifiedAsArgument() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        xsdDataSetStructureGenerator.init(dataSourceWrapperFactory, null);
        xsdDataSetStructureGenerator.generateDataSetStructureAndTemplate(null, xsdDirectory);

        File dataSetXsd = new File(xsdDirectory, "dataset.xsd");
        assertTrue(dataSetXsd.length() > 0);
    }

    @Test
    public void defaultTargetFolder() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        xsdDataSetStructureGenerator.init(dataSourceWrapperFactory, xsdDirectory.getPath());
        xsdDataSetStructureGenerator.generateDataSetStructureAndTemplate(null);

        File dataSetXsd = new File(xsdDirectory, "dataset.xsd");
        assertTrue(dataSetXsd.length() > 0);
    }

    @Test
    public void noDefaultTargetFolderDefined() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        xsdDataSetStructureGenerator.init(dataSourceWrapperFactory, null);
        xsdDataSetStructureGenerator.generateDataSetStructureAndTemplate(null);

        File dataSetXsd = new File(xsdDirectory, "dataset.xsd");
        assertFalse(dataSetXsd.exists());
    }


    private void createTestTables() {
        executeUpdate("create table TABLE_1(columnA int not null identity, columnB varchar(1) not null, columnC varchar(1))", dataSource);
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table TABLE_1", dataSource);
    }
}