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
package org.unitils.dbmaintainer.structure;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.SQLHandler;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import static org.unitils.core.util.SQLUtils.executeUpdateQuietly;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.structure.impl.XsdDataSetStructureGenerator;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.deleteDirectory;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

/**
 * Test class for the {@link XsdDataSetStructureGenerator} for a single schema.
 * <p/>
 * Currently this is only implemented for HsqlDb.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XsdDataSetStructureGeneratorTest extends UnitilsJUnit3 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(XsdDataSetStructureGeneratorTest.class);


    private static final String DATASET_XSD_CONTENT =
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                    "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" xmlns:dflt=\"PUBLIC\">\n" +
                    "   <xsd:import namespace=\"PUBLIC\" schemaLocation=\"PUBLIC.xsd\" />\n" +
                    "   <xsd:element name=\"dataset\">\n" +
                    "       <xsd:complexType>\n" +
                    "           <xsd:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
                    "               <xsd:element name=\"TABLE_1\" type=\"dflt:TABLE_1__type\" />\n" +
                    "               <xsd:element name=\"TABLE_2\" type=\"dflt:TABLE_2__type\" />\n" +
                    "               <xsd:any namespace=\"PUBLIC\" />\n" +
                    "           </xsd:choice>\n" +
                    "       </xsd:complexType>\n" +
                    "   </xsd:element>\n" +
                    "</xsd:schema>";

    private static final String PUBLIC_SCHEMA_XSD_CONTENT =
            "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                    "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" xmlns=\"PUBLIC\" targetNamespace=\"PUBLIC\">\n" +
                    "   <xsd:element name=\"TABLE_1\" type=\"TABLE_1__type\" />\n" +
                    "   <xsd:element name=\"TABLE_2\" type=\"TABLE_2__type\" />\n" +
                    "   <xsd:complexType name=\"TABLE_1__type\">\n" +
                    "       <xsd:attribute name=\"COLUMNC\" use=\"optional\" />\n" +
                    "       <xsd:attribute name=\"COLUMNA\" use=\"optional\" />\n" +
                    "       <xsd:attribute name=\"COLUMNB\" use=\"optional\" />\n" +
                    "   </xsd:complexType>\n" +
                    "   <xsd:complexType name=\"TABLE_2__type\">\n" +
                    "       <xsd:attribute name=\"COLUMN2\" use=\"optional\" />\n" +
                    "       <xsd:attribute name=\"COLUMN1\" use=\"optional\" />\n" +
                    "   </xsd:complexType>\n" +
                    "</xsd:schema>";


    /* Tested object */
    private DataSetStructureGenerator dataSetStructureGenerator;

    /* The target directory for the test xsd files */
    private File xsdDirectory;

    /* DataSource for the test database. */
    @TestDataSource
    private DataSource dataSource = null;

    /* True if current test is not for the current dialect */
    private boolean disabled;


    /**
     * Initializes the test by creating following tables in the test database:
     * tableOne(columnA not null, columnB not null, columnC) and
     * tableTwo(column1, column2)
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPKEY_DATABASE_DIALECT, configuration));
        if (disabled) {
            return;
        }

        xsdDirectory = new File(System.getProperty("java.io.tmpdir"), "XmlSchemaDatabaseStructureGeneratorTest");
        if (xsdDirectory.exists()) {
            deleteDirectory(xsdDirectory);
        }
        xsdDirectory.mkdirs();

        configuration.setProperty(DataSetStructureGenerator.class.getName() + ".implClassName", XsdDataSetStructureGenerator.class.getName());
        configuration.setProperty(XsdDataSetStructureGenerator.PROPKEY_XSD_DIR_NAME, xsdDirectory.getPath());

        SQLHandler sqlHandler = new SQLHandler(dataSource);
        dataSetStructureGenerator = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DataSetStructureGenerator.class, configuration, sqlHandler);
        DBClearer dbClearer = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DBClearer.class, configuration, sqlHandler);

        dbClearer.clearSchemas();
        createTestTables();
    }


    /**
     * Clean-up test database.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
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


    /**
     * Tests the generation of the xsd files for 1 database schema.
     */
    public void testGenerateDataSetStructure() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        dataSetStructureGenerator.generateDataSetStructure();
        assertFileContent(DATASET_XSD_CONTENT, new File(xsdDirectory, "dataset.xsd"));
        assertFileContent(PUBLIC_SCHEMA_XSD_CONTENT, new File(xsdDirectory, "PUBLIC.xsd"));
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
     * Asserts that the contents of the given file equals the given string.
     *
     * @param expectedContent The string, not null
     * @param file            The file, not null
     */
    private void assertFileContent(String expectedContent, File file) throws Exception {
        Reader reader = null;
        try {
            assertTrue("Expected file does not exist. File name: " + file.getPath(), file.exists());

            reader = new BufferedReader(new FileReader(file));
            String content = IOUtils.toString(reader);
            assertEquals(StringUtils.deleteWhitespace(expectedContent), StringUtils.deleteWhitespace(content));

        } finally {
            closeQuietly(reader);
        }
    }
}
