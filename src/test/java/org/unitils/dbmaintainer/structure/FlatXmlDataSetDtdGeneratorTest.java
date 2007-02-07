package org.unitils.dbmaintainer.structure;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.script.StatementHandler;
import static org.unitils.dbmaintainer.structure.impl.FlatXmlDataSetDtdGenerator.PROPKEY_DTD_FILENAME;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test class for the FlatXmlDataSetDtdGenerator
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class FlatXmlDataSetDtdGeneratorTest extends UnitilsJUnit3 {

    /* Tested object */
    private DtdGenerator dtdGenerator;

    /* The file to which to write the DTD */
    private File dtdFile;

    /* DataSource for the test database. */
    @TestDataSource
    private DataSource dataSource = null;


    /**
     * Initializes the test by creating following tables in the test database:
     * tableOne(columnA not null, columnB not null, columnC) and
     * tableTwo(column1, column2)
     */
    protected void setUp() throws Exception {
        super.setUp();

        dtdFile = File.createTempFile("testDTD", ".dtd");

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadConfiguration();
        configuration.setProperty(PROPKEY_DTD_FILENAME, dtdFile.getPath());

        StatementHandler statementHandler = getConfiguredStatementHandlerInstance(configuration, dataSource);
        dtdGenerator = (DtdGenerator) getConfiguredDatabaseTaskInstance(DtdGenerator.class, configuration, dataSource, statementHandler);
        DBClearer dbClearer = (DBClearer) getConfiguredDatabaseTaskInstance(DBClearer.class, configuration, dataSource, statementHandler);

        dbClearer.clearDatabase();
        createTestTables();
    }


    /**
     * Clean-up test database.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        dropTestTables();
    }


    /**
     * Tests the generation of the DTD file.
     */
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


        dtdGenerator.generateDtd();

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
            DbUtils.closeQuietly(conn, st, null);
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
            DbUtils.closeQuietly(conn, st, null);
        }
    }

}
