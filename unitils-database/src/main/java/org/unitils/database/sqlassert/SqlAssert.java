/*
 * Copyright 2011,  Unitils.org
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

package org.unitils.database.sqlassert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModule;
import org.unitils.database.SQLUnitils;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

/**
 * Assertion class to verify content in the database, by specifying your own SQL and checking the result.
 *
 * todo td refactor
 *
 * @author Jeroen Horemans
 */
public abstract class SqlAssert {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(SqlAssert.class);


    /**
     * To be succesfull the result of the SQL should only return one row, this row should be identical to the given parameter. The sequence
     * of the values is not important.
     *
     * The datasource will be fetched from the database module of unitils ({@link DatabaseModule}).
     *
     * @param sql
     * @param row
     */
    public static void assertSingleRowSqlResult(String sql, String[] row) {
        assertSingleRowSqlResult(sql, getDefaultDataSourceFromUnitils(), row);
    }
    
    /**
     * To be succesfull the result of the SQL should only return one row, this row should be identical to the given parameter. The sequence
     * of the values is not important.
     *
     * The datasource will be fetched from the database module of unitils ({@link DatabaseModule}).
     *
     * @param sql
     * @param row
     * @param databaseName
     */
    public static void assertSingleRowSqlResult(String sql, String[] row, String databaseName) {
        assertSingleRowSqlResult(sql, getDataSourceFromUnitils(databaseName), row);
    }

    /**
     * To be succesfull the result of the SQL should return as many rows as the two dimensional arrey has, each row should be identical to
     * the given parameter. The sequence of the values is not important nor is the order of the rows.
     *
     * The datasource will be fetched from the database module of unitils ({@link DatabaseModule}).
     *
     * @param sql
     * @param rows
     */
    public static void assertMultipleRowSqlResult(String sql, String[]... rows) {
        assertMultipleRowSqlResult(sql, getDefaultDataSourceFromUnitils(), rows);
    }
    
    /**
     * To be succesfull the result of the SQL should return as many rows as the two dimensional arrey has, each row should be identical to
     * the given parameter. The sequence of the values is not important nor is the order of the rows.
     *
     * The datasource will be fetched from the database module of unitils ({@link DatabaseModule}).
     *
     * @param sql
     * @param databaseName
     * @param rows
     */
    public static void assertMultipleRowSqlResult(String sql, String databaseName, String[]... rows) {
        assertMultipleRowSqlResult(sql, getDataSourceFromUnitils(databaseName), rows);
    }

    /**
     * The SQL given should only return one row with one column, this column should be a number (preferred a count(*)). The result is
     * asserted with the countResult parameter.
     *
     * The datasource will be fetched from the database module of unitils ({@link DatabaseModule}).
     *
     * @param sql
     * @param countResult
     */
    public static void assertCountSqlResult(String sql, Long countResult) {
        assertCountSqlResult(sql, getDefaultDataSourceFromUnitils(), countResult);
    }
    
    /**
     * The SQL given should only return one row with one column, this column should be a number (preferred a count(*)). The result is
     * asserted with the countResult parameter.
     *
     * The datasource will be fetched from the database module of unitils ({@link DatabaseModule}).
     *
     * @param sql
     * @param countResult
     * @param databaseName
     */
    public static void assertCountSqlResult(String sql, Long countResult, String databaseName) {
        assertCountSqlResult(sql, getDataSourceFromUnitils(databaseName), countResult);
    }

    /**
     * To be successful the result of the SQL should only return one row, this row should be identical to the given parameter. The sequence
     * of the values is not important.
     *
     * @param sql
     * @param dataSource
     * @param row
     */
    public static void assertSingleRowSqlResult(String sql, DataSource dataSource, String[] row) {
        assertMultipleRowSqlResult(sql, dataSource, new String[][]{
                row
        });
    }

    /**
     * To be successful the result of the SQL should return as many rows as the two dimensional array has, each row should be identical to
     * the given parameter. The sequence of the values is not important nor the order of the rows.
     *
     * @param sql
     * @param dataSource
     * @param rows
     */
    public static void assertMultipleRowSqlResult(String sql, DataSource dataSource, String[]... rows) {
        String[][] itemAsString = getItemAsString(sql, dataSource, rows[0].length);
        ReflectionAssert.assertReflectionEquals(rows, itemAsString, ReflectionComparatorMode.LENIENT_ORDER);
    }

    /**
     * The SQL given should only return one row with one column, this column should be a number (preferred a count(*)). The result is
     * asserted with the countResult parameter.
     *
     * @param sql
     * @param dataSource
     * @param countResult
     */
    public static void assertCountSqlResult(String sql, DataSource dataSource, Long countResult) {
        Long itemAsLong = SQLUnitils.getItemAsLong(sql, dataSource);
        // we use the reflection assert because it would throw the same error ass the other functions of this class. When using the default
        // assertion from junit a assertion error from a different package is thrown
        ReflectionAssert.assertReflectionEquals(countResult, itemAsLong);
    }

    /**
     * Returns the value extracted from the result of the given query. If no value is found, a {@link UnitilsException} is thrown.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The string item value
     */
    protected static String[][] getItemAsString(String sql, DataSource dataSource, Integer columnCount) {
        logger.debug(sql);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            ArrayList<String[]> resultList = new ArrayList<String[]>();

            while (resultSet.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getString(i);
                }
                resultList.add(row);
            }
            return resultList.toArray(new String[][]{
                    {}
            });

        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }
    }

    /**
     * Returns the {@link DataSource} fetched from the unitils {@link DatabaseModule}
     *
     * @return DataSource
     */
    private static DataSource getDefaultDataSourceFromUnitils() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).getDefaultDataSourceWrapper().getDataSource();
    }

    /**
     * Returns the {@link DataSource} fetched from the unitils {@link DatabaseModule}
     *
     * @return DataSource
     */
    private static DataSource getDataSourceFromUnitils(String databaseName) {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).getDataSourceWrapper(databaseName).getDataSource();
    }
}
