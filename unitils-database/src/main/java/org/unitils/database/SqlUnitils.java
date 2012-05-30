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
package org.unitils.database;

import org.unitils.database.core.DataSourceService;
import org.unitils.database.core.DataSourceWrapper;
import org.unitilsnew.core.Unitils;

import java.util.List;


/**
 * Utilities for executing statements and queries.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SqlUnitils {

    protected static DataSourceService dataSourceService = Unitils.getInstanceOfType(DataSourceService.class);


    /**
     * Executes the given update statement on the default database.
     *
     * @param sql The sql string for retrieving the items
     * @return The nr of updates
     */
    public static int executeUpdate(String sql) {
        return executeUpdate(sql, null);

    }

    /**
     * Executes the given update statement on the database with the given name.
     *
     * @param sql          The sql string for retrieving the items
     * @param databaseName The database name, null for the default database
     * @return The nr of updates
     */
    public static int executeUpdate(String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.executeUpdate(sql);
    }


    /**
     * Executes the given statement ignoring all exceptions.
     *
     * @param sql The sql string for retrieving the items
     * @return The nr of updates, -1 if not successful
     */
    public static int executeUpdateQuietly(String sql) {
        return executeUpdateQuietly(sql, null);
    }

    /**
     * Executes the given statement ignoring all exceptions.
     *
     * @param sql          The sql string for retrieving the items
     * @param databaseName The database name, null for the default database
     * @return The nr of updates, -1 if not successful
     */
    public static int executeUpdateQuietly(String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.executeUpdateQuietly(sql);
    }


    /**
     * @param tableName The table, not null
     * @return The nr of rows in the given table for the default database
     */
    public static long getTableCount(String tableName) {
        return getTableCount(tableName, null);
    }

    /**
     * @param tableName    The table, not null
     * @param databaseName The database name, null for the default database
     * @return The nr of rows in the given table
     */
    public static long getTableCount(String tableName, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getTableCount(tableName);
    }

    /**
     * @param tableName The table, not null
     * @return True if the given table is empty for the default database
     */
    public static boolean isTableEmpty(String tableName) {
        return isTableEmpty(tableName, null);
    }

    /**
     * Utility method to check whether the given table is empty on the database with the given name.
     *
     * @param tableName    The table, not null
     * @param databaseName The database name, null for the default database
     * @return True if the given table is empty
     */
    public static boolean isTableEmpty(String tableName, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.isTableEmpty(tableName);
    }


    /**
     * Returns the string extracted from the result of the given query on the default database.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql The sql string for retrieving the items, not null
     * @return The string value, not null
     */
    public static String getString(String sql) {
        return getString(sql, null);
    }

    /**
     * Returns the string extracted from the result of the given query on the database with the given name.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql          The sql string for retrieving the items
     * @param databaseName The database name, null for the default database
     * @return The string value
     */
    public static String getString(String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getString(sql);
    }

    /**
     * Returns the strings extracted from the result of the given query on the default database.
     *
     * @param sql The sql string for retrieving the items, not null
     * @return The strings, not null
     */
    public static List<String> getStringList(String sql) {
        return getStringList(sql, null);
    }

    /**
     * Returns the value extracted from the result of the given query on the database with the given name.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql          The sql string for retrieving the items, not null
     * @param databaseName The database name, null for the default database
     * @return The string value, not null
     */
    public static List<String> getStringList(String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getStringList(sql);
    }


    /**
     * Returns the boolean extracted from the result of the given query on the default database.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql The sql string for retrieving the items, not null
     * @return The boolean value
     */
    public static boolean getBoolean(String sql) {
        return getBoolean(sql, null);
    }

    /**
     * Returns the boolean extracted from the result of the given query on the database with the given name.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql          The sql string for retrieving the items, not null
     * @param databaseName The database name, null for the default database
     * @return The boolean value
     */
    public static boolean getBoolean(String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getBoolean(sql);
    }

    /**
     * Returns the booleans extracted from the result of the given query on the default database.
     *
     * @param sql The sql string for retrieving the items, not null
     * @return The booleans, not null
     */
    public static List<Boolean> getBooleanList(String sql) {
        return getBooleanList(sql, null);
    }

    /**
     * Returns the booleans extracted from the result of the given query on the database with the given name.
     *
     * @param sql          The sql string for retrieving the items, not null
     * @param databaseName The database name, null for the default database
     * @return The booleans, not null
     */
    public static List<Boolean> getBooleanList(String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getBooleanList(sql);
    }


    /**
     * Returns the int extracted from the result of the given query on the default database.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql The sql string for retrieving the items, not null
     * @return The int value
     */
    public static int getInteger(String sql) {
        return getInteger(sql, null);
    }

    /**
     * Returns the int extracted from the result of the given query on the database with the given name.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql          The sql string for retrieving the items, not null
     * @param databaseName The database name, null for the default database
     * @return The int value
     */
    public static int getInteger(String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getInteger(sql);
    }

    /**
     * Returns the integers extracted from the result of the given query on the default database.
     *
     * @param sql The sql string for retrieving the items, not null
     * @return The integers, not null
     */
    public static List<Integer> getIntegerList(String sql) {
        return getIntegerList(sql, null);
    }

    /**
     * Returns the int extracted from the result of the given query on the database with the given name.
     *
     * @param sql          The sql string for retrieving the items, not null
     * @param databaseName The database name, null for the default database
     * @return The integers, not null
     */
    public static List<Integer> getIntegerList(String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getIntegerList(sql);
    }


    /**
     * Returns the long extracted from the result of the given query on the default database.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql The sql string for retrieving the items, not null
     * @return The long value
     */
    public static long getLong(String sql) {
        return getLong(sql, null);
    }

    /**
     * Returns the long extracted from the result of the given query on the database with the given name.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql          The sql string for retrieving the items, not null
     * @param databaseName The database name, null for the default database
     * @return The long value
     */
    public static long getLong(String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getLong(sql);
    }

    /**
     * Returns the longs extracted from the result of the given query on the default database.
     *
     * @param sql The sql string for retrieving the items, not null
     * @return The longs, not null
     */
    public static List<Long> getLongList(String sql) {
        return getLongList(sql, null);
    }

    /**
     * Returns the longs extracted from the result of the given query on the database with the given name.
     *
     * @param sql          The sql string for retrieving the items, not null
     * @param databaseName The database name, null for the default database
     * @return The longs, not null
     */
    public static List<Long> getLongList(String sql, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getLongList(sql);
    }


    /**
     * Returns the objects of the given type extracted from the result of the given query on the default database.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql  The sql string for retrieving the items, not null
     * @param type The result type, not null
     * @return The value, not null
     */
    public static <T> T getObject(String sql, Class<T> type) {
        return getObject(sql, type, null);
    }

    /**
     * Returns the objects of the given type extracted from the result of the given query on the database with the given name.
     * If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql          The sql string for retrieving the items, not null
     * @param type         The result type, not null
     * @param databaseName The database name, null for the default database
     * @return The value, not null
     */
    public static <T> T getObject(String sql, Class<T> type, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getObject(sql, type);
    }

    /**
     * Returns the objects of the given type extracted from the result of the given query on the default database.
     *
     * @param sql  The sql string for retrieving the items, not null
     * @param type The result type, not null
     * @return The values, not null
     */
    public static <T> List<T> getObjectList(String sql, Class<T> type) {
        return getObjectList(sql, type, null);
    }

    /**
     * Returns the objects of the given type extracted from the result of the given query on the database with the given name.
     *
     * @param sql          The sql string for retrieving the items, not null
     * @param type         The result type, not null
     * @param databaseName The database name, null for the default database
     * @return The values, not null
     */
    public static <T> List<T> getObjectList(String sql, Class<T> type, String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getObjectList(sql, type);
    }
}
