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

import org.apache.commons.lang.StringUtils;
import org.dbmaintain.database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.unitils.dbunit.structure.DataSetStructureGenerator;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.DatabaseUnitils.getDefaultDatabase;
import static org.unitils.dbunit.structure.impl.DtdDataSetStructureGenerator.PROPKEY_DTD_FILENAME;
import static org.unitils.testutil.TestUnitilsConfiguration.getUnitilsConfiguration;
import static thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

/**
 * Test class for the FlatXmlDataSetDtdGenerator
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DtdDataSetStructureGeneratorTest {

    /* Tested object */
    private DataSetStructureGenerator dataSetStructureGenerator = new DtdDataSetStructureGenerator();

    private File dtdFile;
    private DataSource dataSource;


    /**
     * Initializes the test by creating following tables in the test database:
     * tableOne(columnA not null, columnB not null, columnC) and
     * tableTwo(column1, column2)
     */
    @Before
    public void setUp() throws Exception {
        dtdFile = File.createTempFile("testDTD", ".dtd");

        Properties configuration = getUnitilsConfiguration();
        configuration.setProperty(PROPKEY_DTD_FILENAME, dtdFile.getPath());

        Database defaultDatabase = getDefaultDatabase();
        dataSource = defaultDatabase.getDataSource();
        dataSetStructureGenerator.init(configuration, defaultDatabase);

        dropTestTables();
        createTestTables();
    }


    /**
     * Clean-up test database.
     */
    @After
    public void tearDown() throws Exception {
        dropTestTables();
    }


    /**
     * Tests the generation of the DTD file.
     */
    @Test
    public void testGenerateDtd() throws Exception {
        String expectedContent = "<!ELEMENT DATASET ( (TABLEONE | TABLETWO)*)> " +
                "<!ELEMENT TABLEONE EMPTY>" +
                "<!ATTLIST TABLEONE" +
                "   COLUMNA CDATA #IMPLIED" +
                "   COLUMNB CDATA #IMPLIED" +
                "   COLUMNC CDATA #IMPLIED>" +
                "<!ELEMENT TABLETWO EMPTY>" +
                "<!ATTLIST TABLETWO" +
                "   COLUMN1 CDATA #IMPLIED" +
                "   COLUMN2 CDATA #IMPLIED>";


        dataSetStructureGenerator.generateDataSetStructure();

        assertTrue(dtdFile.exists());
        String content = IOUtils.toString(new FileReader(dtdFile)).toUpperCase();
        assertEquals(StringUtils.deleteWhitespace(expectedContent), StringUtils.deleteWhitespace(content));
    }

    /**
     * Creates the test tables
     */
    private void createTestTables() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            statement = connection.createStatement();
            statement.execute("create table tableOne(columnA varchar(1) not null, columnB varchar(1) not null, columnC varchar(1))");
            statement.execute("create table tableTwo(column1 varchar(1), column2 varchar(1))");

        } finally {
            closeQuietly(connection, statement, null);
        }
    }


    /**
     * Removes the test database tables
     */
    private void dropTestTables() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            statement = connection.createStatement();
            try {
                statement.executeUpdate("drop table TABLEONE");
            } catch (SQLException e) {
                // Ignored
            }
            try {
                statement.executeUpdate("drop table TABLETWO");
            } catch (SQLException e) {
                // Ignored
            }
        } finally {
            closeQuietly(connection, statement, null);
        }
    }

}
