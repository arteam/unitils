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
package org.unitils.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.unitils.core.UnitilsException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.unitils.database.util.DbUtils.closeQuietly;


/**
 * Utilities for executing statements and queries.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SQLUnitils {


    /* The logger instance for this class */
    private static final Log logger = LogFactory.getLog(SQLUnitils.class);


    /**
     * Executes the given update statement.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The nr of updates
     */
    public static int executeUpdate(String sql, DataSource dataSource) {
        logger.debug(sql);

        Connection connection = null;
        Statement statement = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, null, dataSource);
        }
    }


    /**
     * Executes the given statement ignoring all exceptions.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The nr of updates, -1 if not successful
     */
    public static int executeUpdateQuietly(String sql, DataSource dataSource) {
        try {
            return executeUpdate(sql, dataSource);
        } catch (UnitilsException e) {
            // Ignored
            return -1;
        }
    }


    /**
     * Returns the long extracted from the result of the given query. If no value is found, a {@link UnitilsException}
     * is thrown.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The long item value
     */
    public static long getItemAsLong(String sql, DataSource dataSource) {
        logger.debug(sql);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet, dataSource);
        }

        // in case no value was found, throw an exception
        throw new UnitilsException("No item value found: " + sql);
    }


    /**
     * Returns the value extracted from the result of the given query. If no value is found, a {@link UnitilsException}
     * is thrown.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The string item value
     */
    public static String getItemAsString(String sql, DataSource dataSource) {
        logger.debug(sql);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet, dataSource);
        }

        // in case no value was found, throw an exception
        throw new UnitilsException("No item value found: " + sql);
    }


    /**
     * Returns the items extracted from the result of the given query.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The items, not null
     */
    public static Set<String> getItemsAsStringSet(String sql, DataSource dataSource) {
        logger.debug(sql);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            Set<String> result = new HashSet<String>();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
            return result;

        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet, dataSource);
        }
    }


    /**
     * Utility method to check whether the given table is empty.
     *
     * @param tableName  The table, not null
     * @param dataSource The data source, not null
     * @return True if empty
     */
    public static boolean isEmpty(String tableName, DataSource dataSource) {
        return getItemAsLong("select count(1) from " + tableName, dataSource) == 0;
    }

}
