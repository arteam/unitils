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
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.util.DataSetTestUtils.createDataSourceWrapper;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.deleteDirectory;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * Test class for the {@link XsdDataSetStructureGenerator} for a single schema.
 * <p/>
 * Currently this is only implemented for HsqlDb.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XsdDataSetStructureGeneratorTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(XsdDataSetStructureGeneratorTest.class);

    /* Tested object */
    private XsdDataSetStructureGenerator xsdDataSetStructureGenerator = new XsdDataSetStructureGenerator();

    /* The target directory for the test xsd files */
    private File xsdDirectory;
    @TestDataSource
    private DataSource dataSource = null;
    private boolean disabled;


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

        DataSourceWrapper dataSourceWrapper = createDataSourceWrapper();
        xsdDataSetStructureGenerator.init(dataSourceWrapper, null);

        dropTestTables();
        createTestTables();
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
    }


    @Test
    public void generateDataSetStructure() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        xsdDataSetStructureGenerator.generateDataSetStructure(xsdDirectory);

        // check content of general dataset xsd
        File dataSetXsd = new File(xsdDirectory, "dataset.xsd");
        assertFileContains("targetNamespace=\"unitils-dataset\"", dataSetXsd);
        assertFileContains("<xsd:import namespace=\"PUBLIC\" schemaLocation=\"PUBLIC.xsd\"/>", dataSetXsd);
        assertFileContains("<xsd:element name=\"notExists\" type=\"notExists__type\"/>", dataSetXsd);
        assertFileContains("<xsd:any namespace=\"PUBLIC\"/>", dataSetXsd);
        assertFileContains("<xsd:attribute name=\"caseSensitive\" use=\"optional\" type=\"xsd:boolean\"/>", dataSetXsd);
        assertFileContains("<xsd:attribute name=\"literalToken\" use=\"optional\" type=\"xsd:string\"/>", dataSetXsd);
        assertFileContains("<xsd:attribute name=\"variableToken\" use=\"optional\" type=\"xsd:string\"/>", dataSetXsd);
        assertFileContains("<xsd:complexType name=\"notExists__type\">", dataSetXsd);

        // check content of PUBLIC schema dataset xsd
        File publicSchemaDataSetXsd = new File(xsdDirectory, "PUBLIC.xsd");
        assertFileContains("xmlns=\"PUBLIC\" targetNamespace=\"PUBLIC\"", publicSchemaDataSetXsd);

        assertFileContains("<xsd:element name=\"TABLE_1\" type=\"TABLE_1__type\"/>", publicSchemaDataSetXsd);
        assertFileContains("<xsd:complexType name=\"TABLE_1__type\">", publicSchemaDataSetXsd);
        assertFileContains("<xsd:any namespace=\"PUBLIC\"/>", publicSchemaDataSetXsd);
        assertFileContains("<xsd:attribute name=\"COLUMNC\" use=\"optional\"/>", publicSchemaDataSetXsd);
        assertFileContains("<xsd:attribute name=\"COLUMNA\" use=\"optional\"/>", publicSchemaDataSetXsd);
        assertFileContains("<xsd:attribute name=\"COLUMNB\" use=\"optional\"/>", publicSchemaDataSetXsd);

        assertFileContains("<xsd:element name=\"TABLE_2\" type=\"TABLE_2__type\"/>", publicSchemaDataSetXsd);
        assertFileContains("<xsd:complexType name=\"TABLE_2__type\">", publicSchemaDataSetXsd);
        assertFileContains("<xsd:any namespace=\"PUBLIC\"/>", publicSchemaDataSetXsd);
        assertFileContains("<xsd:attribute name=\"COLUMN2\" use=\"optional\"/>", publicSchemaDataSetXsd);
        assertFileContains("<xsd:attribute name=\"COLUMN1\" use=\"optional\"/>", publicSchemaDataSetXsd);
    }


    private void createTestTables() {
        executeUpdate("create table TABLE_1(columnA int not null identity, columnB varchar(1) not null, columnC varchar(1))", dataSource);
        executeUpdate("create table TABLE_2(column1 varchar(1), column2 varchar(1))", dataSource);
    }

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