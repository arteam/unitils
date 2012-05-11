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

package org.unitilsnew.database.dbmaintain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.database.DatabaseException;
import org.dbmaintain.database.SQLHandler;
import org.unitilsnew.database.core.TransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.jdbc.datasource.DataSourceUtils.getConnection;
import static org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection;
import static org.springframework.jdbc.support.JdbcUtils.closeResultSet;
import static org.springframework.jdbc.support.JdbcUtils.closeStatement;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainSQLHandler implements SQLHandler {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DbMaintainSQLHandler.class);

    protected TransactionManager transactionManager;


    // todo move methods out to some service
    // todo switch to spring


    public DbMaintainSQLHandler(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }


    public void execute(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        Connection connection = null;
        try {
            connection = getConnection(dataSource);
            statement = connection.createStatement();
            statement.execute(sql);

        } catch (Exception e) {
            throw new DatabaseException("Could not perform database statement: " + sql, e);
        } finally {
            closeStatement(statement);
            releaseConnection(connection, dataSource);
        }
    }

    public int executeUpdateAndCommit(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        Connection connection = null;
        try {
            connection = getConnection(dataSource);
            statement = connection.createStatement();
            int nbChanges = statement.executeUpdate(sql);
            if (!connection.getAutoCommit()) {
                transactionManager.commit();
            }
            return nbChanges;

        } catch (Exception e) {
            throw new DatabaseException("Error while performing database update:\n" + sql, e);
        } finally {
            closeStatement(statement);
            releaseConnection(connection, dataSource);
        }
    }

    public long getItemAsLong(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = getConnection(dataSource);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception e) {
            throw new DatabaseException("Error while executing statement: " + sql, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            releaseConnection(connection, dataSource);
        }

        // in case no value was found, throw an exception
        throw new DatabaseException("No item value found: " + sql);
    }

    public String getItemAsString(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = getConnection(dataSource);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (Exception e) {
            throw new DatabaseException("Error while executing statement: " + sql, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            releaseConnection(connection, dataSource);
        }
        // in case no value was found, throw an exception
        throw new DatabaseException("No item value found: " + sql);
    }

    public Set<String> getItemsAsStringSet(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = getConnection(dataSource);
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
            closeResultSet(resultSet);
            closeStatement(statement);
            releaseConnection(connection, dataSource);
        }
    }

    public boolean exists(String sql, DataSource dataSource) {
        logger.debug(sql);

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = getConnection(dataSource);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            return resultSet.next();

        } catch (Exception e) {
            throw new DatabaseException("Error while executing statement: " + sql, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            releaseConnection(connection, dataSource);
        }
    }

    public void startTransaction(DataSource dataSource) {
        transactionManager.startTransaction();
        transactionManager.registerDataSource(dataSource);
    }

    public void endTransactionAndCommit(DataSource dataSource) {
        transactionManager.commit();
    }

    public void endTransactionAndRollback(DataSource dataSource) {
        transactionManager.rollback();
    }

    public void closeAllConnections() {
    }
}
