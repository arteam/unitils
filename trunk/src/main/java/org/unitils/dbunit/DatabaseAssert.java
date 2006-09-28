package org.unitils.dbunit;

import org.unitils.core.TestContext;
import org.apache.commons.dbutils.DbUtils;
import org.dbunit.Assertion;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.dataset.filter.DefaultColumnFilter;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Filip Neven
 */
public class DatabaseAssert {

    public static void assertDBContentAsExpected() throws Exception {

        String currentMethodName = TestContext.getTestMethodName();
        if (currentMethodName == null) {
            throw new RuntimeException("This method is not supported when using a test framework that doesn't " +
                    "support resolving the name of the current test, such as JUnit4 or TestNG, use the version of " +
                    "this method that takes the method name as parameter");
        }
        assertDBContentAsExpected(TestContext.getTestObject(), currentMethodName);
    }

    public static void assertDBContentAsExpected(String testMethodName) throws Exception {

        assertDBContentAsExpected(TestContext.getTestMethodName(), testMethodName);
    }

    /**
     * Compares the contents of the expected DataSet with the contents of the database. Only the tables that occur in
     * the expected DataSet are compared with the database content.
     */
    private static void assertDBContentAsExpected(Object test, String testMethodName) throws Exception {
        IDatabaseConnection databaseConnection = null;
        try {
            databaseConnection = DatabaseTestModule.getConnection();

            IDataSet expectedDataSet = getExpectedDataSet(test, testMethodName);
            IDataSet actualDataSet = databaseConnection.createDataSet(expectedDataSet.getTableNames());
            ITableIterator tables = expectedDataSet.iterator();

            while (tables.next()) {
                ITable expectedTable = tables.getTable();
                ITableMetaData metaData = expectedTable.getTableMetaData();
                ITable actualTable = actualDataSet.getTable(expectedTable.getTableMetaData().getTableName());
                ITable filteredActualTable = DefaultColumnFilter.includedColumnsTable(actualTable, metaData.getColumns());

                Assertion.assertEquals(new SortedTable(expectedTable), new SortedTable(filteredActualTable,
                        expectedTable.getTableMetaData()));
            }

        } finally {
            if (databaseConnection != null) {
                DbUtils.closeQuietly(databaseConnection.getConnection());
            }
        }
    }

    /**
     * Gets the result dataset with a filename specified by {@link #getExpectedDataSetFileName(Object, String)}.
     * If the file does not exist, a file not found exception is thrown.
     *
     * @return the dataset, not null
     * @throws Exception if the dataset could not org loaded
     */
    private static IDataSet getExpectedDataSet(Object test, String methodName) throws Exception {
        String dataSetFileName = getExpectedDataSetFileName(test, methodName);
        IDataSet dataSet = loadDataSet(test, dataSetFileName);
        if (dataSet == null) {
            throw new FileNotFoundException("Unable to find test dataset with file name: " + dataSetFileName);
        }
        return dataSet;
    }

    /**
     * Loads a dataset from the file with the given name.
     * Filenames that start with '/' are treated absolute. Filenames that do not start with '/', are relative
     * to the current class.
     *
     * @param dataSetFilename the name, (start with '/' for absolute names)
     * @return the data set, or null if the file did not exist
     * @throws Exception if the dataset could not org loaded
     */
    private static IDataSet loadDataSet(Object test, String dataSetFilename) throws Exception {
        InputStream in = null;
        try {
            in = test.getClass().getResourceAsStream(dataSetFilename);
            if (in == null) {
                return null;
            }

            IDataSet dataSet = new GroupableFlatXmlDataSet(in);
            ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
            replacementDataSet.addReplacementObject("[null]", null);
            return replacementDataSet;

        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Gets the name of the result testdata file.
     * The name will org constructed as follows: 'classname without packagename'.'testname'-result.xml
     *
     * @return the result filename
     */
    private static String getExpectedDataSetFileName(Object test, String methodName) {
        String className = test.getClass().getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + methodName + "-result.xml";
    }

}
