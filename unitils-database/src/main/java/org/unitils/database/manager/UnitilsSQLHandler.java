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
package org.unitils.database.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.database.DatabaseException;
import org.dbmaintain.database.SQLHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.unitils.database.util.DbUtils.closeQuietly;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsSQLHandler implements SQLHandler {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(UnitilsSQLHandler.class);

    private UnitilsTransactionManager unitilsTransactionManager;


    public UnitilsSQLHandler(UnitilsTransactionManager unitilsTransactionManager) {
        this.unitilsTransactionManager = unitilsTransactionManager;
    }


    @Override
    public void execute(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.execute(sql);

        } catch (Exception e) {
            throw new DatabaseException("Could not perform database statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, null);
        }
    }

    @Override
    public int executeUpdateAndCommit(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            int nbChanges = statement.executeUpdate(sql);
            if (!connection.getAutoCommit()) {
                unitilsTransactionManager.commit();
            }
            return nbChanges;

        } catch (Exception e) {
            throw new DatabaseException("Error while performing database update:\n" + sql, e);
        } finally {
            closeQuietly(connection, statement, null);
        }
    }

    @Override
    public long getItemAsLong(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception e) {
            throw new DatabaseException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }

        // in case no value was found, throw an exception
        throw new DatabaseException("No item value found: " + sql);
    }

    @Override
    public String getItemAsString(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (Exception e) {
            throw new DatabaseException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }

        // in case no value was found, throw an exception
        throw new DatabaseException("No item value found: " + sql);
    }

    @Override
    public Set<String> getItemsAsStringSet(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
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
            throw new DatabaseException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }
    }

    @Override
    public boolean exists(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            return resultSet.next();

        } catch (Exception e) {
            throw new DatabaseException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }
    }

    @Override
    public void startTransaction(DataSource dataSource) {
        unitilsTransactionManager.startTransactionForDataSource(dataSource);
    }

    @Override
    public void endTransactionAndCommit(DataSource dataSource) {
        unitilsTransactionManager.commit();
    }

    @Override
    public void endTransactionAndRollback(DataSource dataSource) {
        unitilsTransactionManager.rollback();
    }

    @Override
    public void closeAllConnections() {
    }
}
