package org.unitils.dbunit;

import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import static org.unitils.core.dbsupport.TestSQLUtils.executeUpdateQuietly;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import static org.unitils.core.util.SQLUtils.getItemAsString;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Test class for loading of data sets in mutliple database schemas.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbUnitModuleDataSetMultiSchemaTest extends UnitilsJUnit3 {

    /* Tested object */
    private DbUnitModule dbUnitModule;

    /* The dataSource */
    @TestDataSource
    private DataSource dataSource = null;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dbUnitModule = new DbUnitModule();
        dbUnitModule.init(configuration);

        dropTestTables();
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
     * Test for a data set containing multiple namespaces.
     */
    public void testDataSet_multiSchema() throws Exception {
        dbUnitModule.insertTestData(DataSetTest.class.getMethod("multiSchema"));

        assertLoadedDataSet("PUBLIC");
        assertLoadedDataSet("SCHEMA_A");
        assertLoadedDataSet("SCHEMA_B");
    }

    /**
     * Test for a data set containing multiple namespaces.
     */
    public void testDataSet_multiSchemaNoDefault() throws Exception {
        dbUnitModule.insertTestData(DataSetTest.class.getMethod("multiSchemaNoDefault"));

        assertLoadedDataSet("PUBLIC");
        assertLoadedDataSet("SCHEMA_A");
        assertLoadedDataSet("SCHEMA_B");
    }


    /**
     * Utility method to assert that the data set for the schema was loaded.
     *
     * @param schemaName the name of the schema, not null
     */
    private void assertLoadedDataSet(String schemaName) throws SQLException {
        String dataSet = getItemAsString("select dataset from " + schemaName + ".test", dataSource);
        assertEquals(schemaName, dataSet);
    }


    /**
     * Creates the test tables.
     */
    private void createTestTables() throws SQLException {
        // PUBLIC SCHEMA
        executeUpdate("create table TEST(dataset varchar(100))", dataSource);
        // SCHEMA_A
        executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA", dataSource);
        executeUpdate("create table SCHEMA_A.TEST(dataset varchar(100))", dataSource);
        // SCHEMA_B
        executeUpdate("create schema SCHEMA_B AUTHORIZATION DBA", dataSource);
        executeUpdate("create table SCHEMA_B.TEST(dataset varchar(100))", dataSource);
    }


    /**
     * Removes the test database tables
     */
    private void dropTestTables() {
        executeUpdateQuietly("drop table TEST", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
        executeUpdateQuietly("drop table SCHEMA_B.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
    }


    /**
     * Test class with a class level dataset
     */
    @DataSet
    public class DataSetTest {

        public void multiSchema() {
        }

        public void multiSchemaNoDefault() {
        }
    }

}
