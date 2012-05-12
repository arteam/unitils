/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.database;

import org.unitilsnew.core.Unitils;
import org.unitilsnew.database.core.DataSourceWrapper;
import org.unitilsnew.database.core.DataSourceWrapperManager;

import java.util.List;

import static junit.framework.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

/**
 * Assertion class to verify content in the database, by specifying your own SQL and checking the result.
 *
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 */
public class SqlAssert {

    protected static DataSourceWrapperManager dataSourceWrapperManager = Unitils.getInstanceOfType(DataSourceWrapperManager.class);

    // todo javadoc
    // todo unit test


    /**
     * To be successful the result of the SQL should return as many rows as the two dimensional array has, each row should be identical to
     * the given parameter. The sequence of the values is not important nor the order of the rows.
     */
    public static void assertRows(String sql, String[]... expectedRows) {
        assertRows(sql, null, expectedRows);
    }

    public static void assertRows(String sql, String databaseName, String[]... expectedRows) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        List<List<String>> actualRows = dataSourceWrapper.getRowsAsString(sql);
        assertReflectionEquals(expectedRows, actualRows, LENIENT_ORDER);
    }


    /**
     * The SQL given should only return one row with one column, this column should be a number (preferred a count(1)). The result is
     * asserted with the countResult parameter.
     */
    public static void assertTableCount(String tableName, long expectedCount) {
        assertTableCount(tableName, expectedCount, null);
    }

    public static void assertTableCount(String tableName, long expectedCount, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        long actualCount = dataSourceWrapper.getTableCount(tableName);
        assertReflectionEquals(expectedCount, actualCount);
    }


    public static void assertTableEmpty(String tableName) {
        assertTableEmpty(tableName, null);
    }

    public static void assertTableEmpty(String tableName, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        boolean empty = dataSourceWrapper.isTableEmpty(tableName);
        if (!empty) {
            fail("Table " + tableName + " is not empty.");
        }
    }


    public static void assertStringResult(String sql, String expectedValue) {
        assertStringResult(sql, expectedValue, null);
    }

    public static void assertStringResult(String sql, String expectedValue, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        String actualValue = dataSourceWrapper.getString(sql);
        assertReflectionEquals(expectedValue, actualValue);
    }

    public static void assertStringListResult(String sql, List<String> expectedValues) {
        assertStringListResult(sql, expectedValues, null);
    }

    public static void assertStringListResult(String sql, List<String> expectedValues, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        List<String> actualValues = dataSourceWrapper.getStringList(sql);
        assertReflectionEquals(expectedValues, actualValues);
    }


    public static void assertBooleanResult(String sql, boolean expectedValue) {
        assertBooleanResult(sql, expectedValue, null);
    }

    public static void assertBooleanResult(String sql, boolean expectedValue, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        boolean actualValue = dataSourceWrapper.getBoolean(sql);
        assertReflectionEquals(expectedValue, actualValue);
    }

    public static void assertBooleanListResult(String sql, List<Boolean> expectedValues) {
        assertBooleanListResult(sql, expectedValues, null);
    }

    public static void assertBooleanListResult(String sql, List<Boolean> expectedValues, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        List<Boolean> actualValues = dataSourceWrapper.getBooleanList(sql);
        assertReflectionEquals(expectedValues, actualValues);
    }


    public static void assertIntegerResult(String sql, long expectedValue) {
        assertIntegerResult(sql, expectedValue, null);
    }

    public static void assertIntegerResult(String sql, long expectedValue, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        int actualValue = dataSourceWrapper.getInteger(sql);
        assertReflectionEquals(expectedValue, actualValue);
    }

    public static void assertIntegerListResult(String sql, List<Long> expectedValues) {
        assertIntegerListResult(sql, expectedValues, null);
    }

    public static void assertIntegerListResult(String sql, List<Long> expectedValues, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        List<Integer> actualValues = dataSourceWrapper.getIntegerList(sql);
        assertReflectionEquals(expectedValues, actualValues, LENIENT_ORDER);
    }


    /**
     * The SQL given should only return one row with one column, this column should be a number (preferred a count(1)). The result is
     * asserted with the countResult parameter.
     */
    public static void assertLong(String sql, long expectedValue) {
        assertLong(sql, expectedValue, null);
    }

    public static void assertLong(String sql, long expectedValue, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        long actualValue = dataSourceWrapper.getLong(sql);
        assertReflectionEquals(expectedValue, actualValue);
    }

    public static void assertLongList(String sql, List<Long> expectedValues) {
        assertLongList(sql, expectedValues, null);
    }

    public static void assertLongList(String sql, List<Long> expectedValues, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        List<Long> actualValues = dataSourceWrapper.getLongList(sql);
        assertReflectionEquals(expectedValues, actualValues, LENIENT_ORDER);
    }


    public static <T> void assertObject(String sql, Class<T> type, List<T> expectedValue) {
        assertObject(sql, type, expectedValue, null);
    }

    public static <T> void assertObject(String sql, Class<T> type, List<T> expectedValue, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        T actualValue = dataSourceWrapper.getObject(sql, type);
        assertReflectionEquals(expectedValue, actualValue);
    }

    public static <T> void assertObjectList(String sql, Class<T> type, T expectedValues) {
        assertObjectList(sql, type, expectedValues, null);
    }

    public static <T> void assertObjectList(String sql, Class<T> type, T expectedValues, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        List<T> actualValues = dataSourceWrapper.getObjectList(sql, type);
        assertReflectionEquals(expectedValues, actualValues, LENIENT_ORDER);
    }

}
