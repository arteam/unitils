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
import org.unitilsnew.database.core.DataSourceService;
import org.unitilsnew.database.core.DataSourceWrapper;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

/**
 * Assertion class to verify content in the database, by specifying your own SQL and checking the result.
 *
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 */
public class SqlAssert {

    protected static DataSourceService dataSourceService = Unitils.getInstanceOfType(DataSourceService.class);


    /**
     * To be successful the result of the SQL should return as many rows as the two dimensional array has, each row should be identical to
     * the given parameter. The sequence of the values is not important nor the order of the rows.
     */
    public static void assertRows(String sql, String[]... expectedRows) {
        assertRows(sql, null, expectedRows);
    }

    public static void assertRows(String sql, String databaseName, String[]... expectedRows) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        List<List<String>> actualRows = dataSourceWrapper.getRowsAsString(sql);
        assertReflectionEquals(expectedRows, actualRows, LENIENT_ORDER);
    }


    /**
     * The SQL given should only return one row with one column, this column should be a number (preferred a count(1)). The result is
     * asserted with the countResult parameter.
     */
    public static void assertTableCount(long expectedCount, String tableName) {
        assertTableCount(expectedCount, tableName, null);
    }

    public static void assertTableCount(long expectedCount, String tableName, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        long actualCount = dataSourceWrapper.getTableCount(tableName);
        assertEquals("Different table count for table '" + tableName + "':", expectedCount, actualCount);
    }


    public static void assertTableEmpty(String tableName) {
        assertTableEmpty(tableName, null);
    }

    public static void assertTableEmpty(String tableName, String databaseName) {
        assertTableCount(0, tableName, databaseName);
    }


    public static void assertString(String expectedValue, String sql) {
        assertString(expectedValue, sql, null);
    }

    public static void assertString(String expectedValue, String sql, String databaseName) {
        assertObject(expectedValue, String.class, sql, databaseName);
    }

    public static void assertStringList(List<String> expectedValues, String sql) {
        assertStringList(expectedValues, sql, null);
    }

    public static void assertStringList(List<String> expectedValues, String sql, String databaseName) {
        assertObjectList(expectedValues, String.class, sql, databaseName);
    }


    public static void assertBoolean(boolean expectedValue, String sql) {
        assertBoolean(expectedValue, sql, null);
    }

    public static void assertBoolean(boolean expectedValue, String sql, String databaseName) {
        assertObject(expectedValue, Boolean.class, sql, databaseName);
    }

    public static void assertBooleanList(List<Boolean> expectedValues, String sql) {
        assertBooleanList(expectedValues, sql, null);
    }

    public static void assertBooleanList(List<Boolean> expectedValues, String sql, String databaseName) {
        assertObjectList(expectedValues, Boolean.class, sql, databaseName);
    }


    public static void assertInteger(int expectedValue, String sql) {
        assertInteger(expectedValue, sql, null);
    }

    public static void assertInteger(int expectedValue, String sql, String databaseName) {
        assertObject(expectedValue, Integer.class, sql, databaseName);
    }

    public static void assertIntegerList(List<Integer> expectedValues, String sql) {
        assertIntegerList(expectedValues, sql, null);
    }

    public static void assertIntegerList(List<Integer> expectedValues, String sql, String databaseName) {
        assertObjectList(expectedValues, Integer.class, sql, databaseName);
    }


    /**
     * The SQL given should only return one row with one column, this column should be a number (preferred a count(1)). The result is
     * asserted with the countResult parameter.
     */
    public static void assertLong(long expectedValue, String sql) {
        assertLong(expectedValue, sql, null);
    }

    public static void assertLong(long expectedValue, String sql, String databaseName) {
        assertObject(expectedValue, Long.class, sql, databaseName);
    }

    public static void assertLongList(List<Long> expectedValues, String sql) {
        assertLongList(expectedValues, sql, null);
    }

    public static void assertLongList(List<Long> expectedValues, String sql, String databaseName) {
        assertObjectList(expectedValues, Long.class, sql, databaseName);
    }


    public static <T> void assertObject(T expectedValue, Class<T> type, String sql) {
        assertObject(expectedValue, type, sql, null);
    }

    public static <T> void assertObject(T expectedValue, Class<T> type, String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        T actualValue = dataSourceWrapper.getObject(sql, type);
        assertEquals("Different result found for query '" + sql + "':", expectedValue, actualValue);
    }

    public static <T> void assertObjectList(List<T> expectedValues, Class<T> type, String sql) {
        assertObjectList(expectedValues, type, sql, null);
    }

    public static <T> void assertObjectList(List<T> expectedValues, Class<T> type, String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        List<T> actualValues = dataSourceWrapper.getObjectList(sql, type);
        assertReflectionEquals("Different result found for query '" + sql + "':", expectedValues, actualValues, LENIENT_ORDER);
    }

}
