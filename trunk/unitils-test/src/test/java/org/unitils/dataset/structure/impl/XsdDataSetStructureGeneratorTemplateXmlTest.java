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
import org.unitils.dataset.database.DataSourceWrapper;
import org.unitils.dataset.database.DataSourceWrapperFactory;
import org.unitils.mock.Mock;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.deleteWhitespace;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_DIALECT;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCHEMANAMES;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.util.DataSetTestUtils.createDataSourceWrapper;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.deleteDirectory;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * Test class for the {@link XsdDataSetStructureGenerator} for the generation of the sample data set template xml.
 * <p/>
 * Currently this is only implemented for HsqlDb.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XsdDataSetStructureGeneratorTemplateXmlTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(XsdDataSetStructureGeneratorTemplateXmlTest.class);

    /* Tested object */
    private XsdDataSetStructureGenerator xsdDataSetStructureGenerator = new XsdDataSetStructureGenerator();

    /* The target directory for the test xsd files */
    private File xsdDirectory;

    @TestDataSource
    protected DataSource dataSource;
    protected Mock<DataSourceWrapperFactory> dataSourceWrapperFactory;

    /* True if current test is not for the current dialect */
    private boolean disabled;


    /**
     * Initializes the test by creating following tables in the test database:
     * tableOne(columnA not null, columnB not null, columnC) and
     * tableTwo(column1, column2)
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPERTY_DIALECT, configuration));
        if (disabled) {
            return;
        }
        configuration.put(PROPERTY_SCHEMANAMES, "public, schema_a");

        xsdDirectory = new File(System.getProperty("java.io.tmpdir"), "XsdDataSetStructureGeneratorTemplateXmlTest");
        if (xsdDirectory.exists()) {
            deleteDirectory(xsdDirectory);
        }

        DataSourceWrapper dataSourceWrapper = createDataSourceWrapper("public", "schema_a");
        dataSourceWrapperFactory.returns(dataSourceWrapper).getDataSourceWrapper(null);

        xsdDataSetStructureGenerator.init(dataSourceWrapperFactory.getMock(), null);

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
        dropTestTables();
        try {
            deleteDirectory(xsdDirectory);
        } catch (Exception e) {
            // ignore
        }
    }


    @Test
    public void generateDataSetTemplateXml() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        xsdDataSetStructureGenerator.generateDataSetTemplateXmlFile(null, xsdDirectory);

        // check content of general dataset xsd
        File dataSetTemplateXml = new File(xsdDirectory, "dataset-template.xml");
        assertFileContains("<uni:dataset xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", dataSetTemplateXml);
        assertFileContains("xmlns=\"PUBLIC\"", dataSetTemplateXml);
        assertFileContains("xmlns:PUBLIC=\"PUBLIC\"", dataSetTemplateXml);
        assertFileContains("xmlns:SCHEMA_A=\"SCHEMA_A\"", dataSetTemplateXml);
        assertFileContains("xmlns:uni=\"unitils-dataset\"", dataSetTemplateXml);
        assertFileContains("xsi:schemaLocation=", dataSetTemplateXml);
        assertFileContains("PUBLIC PUBLIC.xsd", dataSetTemplateXml);
        assertFileContains("SCHEMA_A SCHEMA_A.xsd", dataSetTemplateXml);
        assertFileContains("unitils-dataset dataset.xsd\"", dataSetTemplateXml);
        assertFileContains("</uni:dataset>", dataSetTemplateXml);
    }


    /**
     * Creates the test tables.
     */
    private void createTestTables() {
        executeUpdate("create table TABLE_1(columnA int not null identity, columnB varchar(1) not null, columnC varchar(1))", dataSource);
        executeUpdate("create table TABLE_2(column1 varchar(1), column2 varchar(1))", dataSource);
    }


    /**
     * Removes the test database tables
     */
    private void dropTestTables() {
        executeUpdateQuietly("drop table TABLE_1", dataSource);
        executeUpdateQuietly("drop table TABLE_2", dataSource);
    }


    /**
     * Asserts that the contents of the given file contains the given string.
     *
     * @param expectedContent The string, not null
     * @param file            The file, not null
     */
    private void assertFileContains(String expectedContent, File file) throws Exception {
        Reader reader = null;
        try {
            assertTrue("Expected file does not exist. File name: " + file.getPath(), file.exists());

            reader = new BufferedReader(new FileReader(file));
            String content = IOUtils.toString(reader);
            assertTrue(content + "\ndid not contain\n" + expectedContent, deleteWhitespace(content).contains(deleteWhitespace(expectedContent)));

        } finally {
            closeQuietly(reader);
        }
    }
}