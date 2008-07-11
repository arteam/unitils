package org.unitils.dbmaintainer.structure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.dbmaintainer.structure.impl.DtdDataSetStructureGenerator.PROPKEY_DTD_FILENAME;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.util.TestUtils;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

/**
 * Test class for the FlatXmlDataSetDtdGenerator
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DtdDataSetStructureGeneratorTest {

    /* Tested object */
    private DataSetStructureGenerator dataSetStructureGenerator;

    /* The file to which to write the DTD */
    private File dtdFile;

    private DbSupport dbSupport;
    
    /* DataSource for the test database */
    private DataSource dataSource;


    /**
     * Initializes the test by creating following tables in the test database:
     * tableOne(columnA not null, columnB not null, columnC) and
     * tableTwo(column1, column2)
     */
    @Before
    public void setUp() throws Exception {
        dtdFile = File.createTempFile("testDTD", ".dtd");

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPKEY_DTD_FILENAME, dtdFile.getPath());

        dbSupport = TestUtils.getDefaultDbSupport(configuration);
        dataSource = dbSupport.getDataSource();
        
        dataSetStructureGenerator = TestUtils.getDtdDataSetStructureGenerator(configuration, dbSupport);
        DBClearer dbClearer = TestUtils.getDefaultDBClearer(configuration, dbSupport);

        dbClearer.clearSchemas();
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
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create table tableOne(columnA varchar(1) not null, columnB varchar(1) not null, columnC varchar(1))");
            st.execute("create table tableTwo(column1 varchar(1), column2 varchar(1))");

        } finally {
            closeQuietly(conn, st, null);
        }
    }


    /**
     * Removes the test database tables
     */
    private void dropTestTables() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            try {
                st.executeUpdate("drop table TABLEONE");
            } catch (SQLException e) {
                // Ignored
            }
            try {
                st.executeUpdate("drop table TABLETWO");
            } catch (SQLException e) {
                // Ignored
            }
        } finally {
            closeQuietly(conn, st, null);
        }
    }

}
