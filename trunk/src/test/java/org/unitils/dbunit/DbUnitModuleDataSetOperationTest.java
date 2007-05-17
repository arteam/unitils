package org.unitils.dbunit;

import org.unitils.UnitilsJUnit3;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.datasetoperation.DataSetOperation;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;
import org.unitils.core.ConfigurationLoader;
import static org.unitils.core.dbsupport.TestSQLUtils.executeUpdateQuietly;
import static org.unitils.core.util.SQLUtils.getItemAsString;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import org.dbunit.dataset.IDataSet;

import javax.sql.DataSource;
import java.util.Properties;
import java.sql.SQLException;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbUnitModuleDataSetOperationTest extends UnitilsJUnit3 {

    private DbUnitModule dbUnitModule;

    @TestDataSource
    private DataSource dataSource = null;

    public void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dbUnitModule = new DbUnitModule();
        dbUnitModule.init(configuration);

        dropTestTables();
        createTestTables();

        MockDataSetOperation.operationExecuted = false;
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        dropTestTables();
    }

    public void testLoadDataSet_defaultDataSetOperation() throws Exception {
        dbUnitModule.insertTestData(DataSetTest.class.getMethod("testMethod1"));
        assertLoadedDataSet("DbUnitModuleDataSetOperationTest$DataSetTest.xml");
    }

    public void testLoadDataSet_customDataSetOperation() throws Exception {
       dbUnitModule.insertTestData(DataSetTest.class.getMethod("testMethodCustomDataSetOperation"));
       assertTrue(MockDataSetOperation.operationExecuted);
    }

    /**
     * Utility method to assert that the correct data set was loaded.
     *
     * @param expectedDataSetName the name of the data set, not null
     */
    private void assertLoadedDataSet(String expectedDataSetName) throws SQLException {
        String dataSet = getItemAsString("select dataset from test", dataSource);
        assertEquals(expectedDataSetName, dataSet);
    }

    /**
     * Creates the test tables.
     */
    private void createTestTables() throws SQLException {
        // PUBLIC SCHEMA
        executeUpdate("create table TEST(dataset varchar(100))", dataSource);
    }


    /**
     * Removes the test database tables
     */
    private void dropTestTables() {
        executeUpdateQuietly("drop table TEST", dataSource);
    }

     /**
     * Test class with a class level dataset
     */
    @DataSet
    public class DataSetTest {

        public void testMethod1() {
        }

        @DataSet(operation = MockDataSetOperation.class)
        public void testMethodCustomDataSetOperation() {
        }
    }

    public static class MockDataSetOperation implements DataSetOperation {

        private static boolean operationExecuted;

        public void execute(DbUnitDatabaseConnection dbUnitDatabaseConnection, IDataSet dataSet) {
            operationExecuted = true;
        }
    }
}
