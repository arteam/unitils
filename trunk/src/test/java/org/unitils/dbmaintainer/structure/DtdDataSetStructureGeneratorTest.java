package org.unitils.dbmaintainer.structure;

import org.apache.commons.lang.StringUtils;
import org.dbmaintain.config.DbMaintainConfigurationLoader;
import org.dbmaintain.config.PropertiesDbMaintainConfigurer;
import org.dbmaintain.dbsupport.DbSupport;
import org.dbmaintain.launch.DbMaintain;
import org.dbmaintain.util.DbMaintainException;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.util.ConfigUtils;
import org.unitils.dbmaintainer.structure.impl.DtdDataSetStructureGenerator;
import static org.unitils.dbmaintainer.structure.impl.DtdDataSetStructureGenerator.PROPKEY_DTD_FILENAME;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Test class for the FlatXmlDataSetDtdGenerator
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DtdDataSetStructureGeneratorTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetStructureGenerator dataSetStructureGenerator;

    /* The file to which to write the DTD */
    private File dtdFile;

    DbSupport dbSupport;

    DbMaintain dbMaintain;

    /**
     * Initializes the test by creating following tables in the test database:
     * tableOne(columnA not null, columnB not null, columnC) and
     * tableTwo(column1, column2)
     */
    @Before
    public void setUp() throws Exception {
        dtdFile = File.createTempFile("testDTD", ".dtd");

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(DataSetStructureGenerator.class.getName() + ".implClassName", DtdDataSetStructureGenerator.class.getName());
        configuration.setProperty(PROPKEY_DTD_FILENAME, dtdFile.getPath());

        Properties dbMaintainProperties = new DbMaintainConfigurationLoader().loadDefaultConfiguration();
        dbMaintainProperties.putAll(configuration);
        PropertiesDbMaintainConfigurer propertiesDbMaintainConfigurer = new PropertiesDbMaintainConfigurer(dbMaintainProperties, new org.dbmaintain.dbsupport.impl.DefaultSQLHandler());
        dbSupport = propertiesDbMaintainConfigurer.getDefaultDbSupport();
        dbMaintain = new DbMaintain(propertiesDbMaintainConfigurer);

        dataSetStructureGenerator = ConfigUtils.getConfiguredInstanceOf(DataSetStructureGenerator.class, configuration);

        clearDatabase();
        createTestTables();
    }


    /**
     * Clean-up test database.
     */
    @After
    public void tearDown() throws Exception {
        clearDatabase();
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
            conn = dbSupport.getDataSource().getConnection();
            st = conn.createStatement();
            st.execute("create table tableOne(columnA varchar(1) not null, columnB varchar(1) not null, columnC varchar(1))");
            st.execute("create table tableTwo(column1 varchar(1), column2 varchar(1))");

        } finally {
            closeQuietly(conn, st, null);
        }
    }

    private void clearDatabase() {
        dbMaintain.clearDatabase();
        try {
            dbSupport.dropTable(dbSupport.getDefaultSchemaName(), "DBMAINTAIN_SCRIPTS");
        } catch (DbMaintainException e) {
            e.printStackTrace();
            // Ignored, thrown if DBMAINTAIN_SCRIPTS doesn't exists, which is not a problem
        }
    }


}
