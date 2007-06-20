/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilities for executing statements and queries.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SQLUtils {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(SQLUtils.class);


    /**
     * Executes the given statement.
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
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            return statement.executeUpdate(sql);

        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            DbUtils.closeQuietly(connection, statement, null);
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
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
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
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
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
            connection = dataSource.getConnection();
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
            closeQuietly(connection, statement, resultSet);
        }
    }


    /**
     * Drops the test tables
     *
     * @param dbSupport  The db support, not null
     * @param tableNames The tables to drop
     */
    public static void dropTestTables(DbSupport dbSupport, String... tableNames) {
        for (String tableName : tableNames) {
            try {
                String correctCaseTableName = dbSupport.toCorrectCaseIdentifier(tableName);
                dbSupport.dropTable(correctCaseTableName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test views
     *
     * @param dbSupport The db support, not null
     * @param viewNames The views to drop
     */
    public static void dropTestViews(DbSupport dbSupport, String... viewNames) {
        for (String viewName : viewNames) {
            try {
                String correctCaseViewName = dbSupport.toCorrectCaseIdentifier(viewName);
                dbSupport.dropView(correctCaseViewName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test synonyms
     *
     * @param dbSupport    The db support, not null
     * @param synonymNames The views to drop
     */
    public static void dropTestSynonyms(DbSupport dbSupport, String... synonymNames) {
        for (String synonymName : synonymNames) {
            try {
                String correctCaseSynonymName = dbSupport.toCorrectCaseIdentifier(synonymName);
                dbSupport.dropSynonym(correctCaseSynonymName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test sequence
     *
     * @param dbSupport     The db support, not null
     * @param sequenceNames The sequences to drop
     */
    public static void dropTestSequences(DbSupport dbSupport, String... sequenceNames) {
        for (String sequenceName : sequenceNames) {
            try {
                String correctCaseSequenceName = dbSupport.toCorrectCaseIdentifier(sequenceName);
                dbSupport.dropSequence(correctCaseSequenceName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test triggers
     *
     * @param dbSupport    The db support, not null
     * @param triggerNames The triggers to drop
     */
    public static void dropTestTriggers(DbSupport dbSupport, String... triggerNames) {
        for (String triggerName : triggerNames) {
            try {
                String correctCaseTriggerName = dbSupport.toCorrectCaseIdentifier(triggerName);
                dbSupport.dropTrigger(correctCaseTriggerName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test types
     *
     * @param dbSupport The db support, not null
     * @param typeNames The types to drop
     */
    public static void dropTestTypes(DbSupport dbSupport, String... typeNames) {
        for (String typeName : typeNames) {
            try {
                String correctCaseTypeName = dbSupport.toCorrectCaseIdentifier(typeName);
                dbSupport.dropType(correctCaseTypeName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Executes the given statement ignoring all exceptions.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The nr of updates, -1 if not succesful
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
     * Utility method to check whether the given table is empty.
     *
     * @param tableName The table, not null
     * @param dbSupport The db support, not null
     * @return True if empty
     */
    public static boolean isEmpty(String tableName, DbSupport dbSupport) throws SQLException {
        String correctCaseTableName = dbSupport.toCorrectCaseIdentifier(tableName);
        return dbSupport.getSQLHandler().getItemsAsStringSet("select * from " + tableName).isEmpty();
    }

}
