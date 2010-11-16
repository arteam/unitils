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
package org.unitils.dbunit.structure.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.structure.DataSetStructureGenerator;
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
import static org.unitils.dataset.util.DataSetTestUtils.createDatabases;
import static org.unitils.dbunit.structure.impl.XsdDataSetStructureGenerator.PROPKEY_XSD_DIR_NAME;
import static org.unitils.testutil.TestUnitilsConfiguration.getUnitilsConfiguration;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.deleteDirectory;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * Test class for the {@link XsdDataSetStructureGenerator} using multiple schemas.
 * <p/>
 * Currently this is only implemented for HsqlDb.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XsdDataSetStructureGeneratorMultiSchemaTest {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(XsdDataSetStructureGeneratorMultiSchemaTest.class);

    /* Tested object */
    private DataSetStructureGenerator dataSetStructureGenerator = new XsdDataSetStructureGenerator();

    private File xsdDirectory;
    private DataSource dataSource;
    private boolean disabled;


    @Before
    public void initialize() throws Exception {
        Properties configuration = getUnitilsConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPERTY_DIALECT, configuration));
        if (disabled) {
            return;
        }

        xsdDirectory = new File(System.getProperty("java.io.tmpdir"), "XsdDataSetStructureGeneratorMultiSchemaTest");
        if (xsdDirectory.exists()) {
            deleteDirectory(xsdDirectory);
        }
        xsdDirectory.mkdirs();

        Database defaultDatabase = createDatabases("PUBLIC, SCHEMA_A").getDefaultDatabase();
        dataSource = defaultDatabase.getDataSource();

        configuration.setProperty(PROPKEY_XSD_DIR_NAME, xsdDirectory.getPath());
        dataSetStructureGenerator.init(configuration, defaultDatabase);

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
        dataSetStructureGenerator.generateDataSetStructure();

        // check content of general dataset xsd
        File dataSetXsd = new File(xsdDirectory, "dataset.xsd");
        assertFileContains("xmlns:dflt=\"PUBLIC\"", dataSetXsd);
        assertFileContains("<xsd:import namespace=\"PUBLIC\" schemaLocation=\"PUBLIC.xsd\" />", dataSetXsd);
        assertFileContains("<xsd:import namespace=\"SCHEMA_A\" schemaLocation=\"SCHEMA_A.xsd\" />", dataSetXsd);
        assertFileContains("<xsd:element name=\"TABLE_1\" type=\"dflt:TABLE_1__type\" />", dataSetXsd);
        assertFileContains("<xsd:element name=\"TABLE_2\" type=\"dflt:TABLE_2__type\" />", dataSetXsd);
        assertFileContains("<xsd:any namespace=\"PUBLIC\" />", dataSetXsd);

        // check content of PUBLIC schema dataset xsd
        File publicSchemaDataSetXsd = new File(xsdDirectory, "PUBLIC.xsd");
        assertFileContains("xmlns=\"PUBLIC\"", publicSchemaDataSetXsd);
        assertFileContains("targetNamespace=\"PUBLIC\"", publicSchemaDataSetXsd);
        assertFileContains("<xsd:element name=\"TABLE_1\" type=\"TABLE_1__type\" />", publicSchemaDataSetXsd);
        assertFileContains("<xsd:complexType name=\"TABLE_1__type\">", publicSchemaDataSetXsd);
        assertFileContains("<xsd:element name=\"TABLE_2\" type=\"TABLE_2__type\" />", publicSchemaDataSetXsd);
        assertFileContains("<xsd:complexType name=\"TABLE_2__type\">", publicSchemaDataSetXsd);

        // check content of PUBLIC schema dataset xsd
        File schemaADataSetXsd = new File(xsdDirectory, "SCHEMA_A.xsd");
        assertFileContains("xmlns=\"SCHEMA_A\"", schemaADataSetXsd);
        assertFileContains("targetNamespace=\"SCHEMA_A\"", schemaADataSetXsd);
        assertFileContains("<xsd:element name=\"TABLE_1\" type=\"TABLE_1__type\" />", schemaADataSetXsd);
        assertFileContains("<xsd:complexType name=\"TABLE_1__type\">", schemaADataSetXsd);
        assertFileContains("<xsd:element name=\"TABLE_4\" type=\"TABLE_4__type\" />", schemaADataSetXsd);
        assertFileContains("<xsd:complexType name=\"TABLE_4__type\">", schemaADataSetXsd);
        assertFileContains("<xsd:element name=\"TABLE_3\" type=\"TABLE_3__type\" />", schemaADataSetXsd);
        assertFileContains("<xsd:complexType name=\"TABLE_3__type\">", schemaADataSetXsd);
    }


    private void createTestTables() {
        // PUBLIC SCHEMA
        executeUpdate("create table TABLE_1(columnA int not null identity, columnB varchar(1) not null, columnC varchar(1))", dataSource);
        executeUpdate("create table TABLE_2(column1 varchar(1), column2 varchar(1))", dataSource);
        // SCHEMA_A
        executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA", dataSource);
        executeUpdate("create table SCHEMA_A.TABLE_1(columnA int not null identity, columnB varchar(1) not null, columnC varchar(1))", dataSource);
        executeUpdate("create table SCHEMA_A.TABLE_3(columnA int not null identity, columnB varchar(1) not null, columnC varchar(1))", dataSource);
        executeUpdate("create table SCHEMA_A.TABLE_4(column1 varchar(1), column2 varchar(1))", dataSource);
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table TABLE_1", dataSource);
        executeUpdateQuietly("drop table TABLE_2", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TABLE_1", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TABLE_3", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TABLE_4", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
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
