/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.database.dbmaintain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.database.DatabaseException;
import org.dbmaintain.database.SQLHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.database.transaction.impl.DefaultTransactionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.jdbc.datasource.DataSourceUtils.getConnection;
import static org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection;
import static org.springframework.jdbc.support.JdbcUtils.closeResultSet;
import static org.springframework.jdbc.support.JdbcUtils.closeStatement;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainSQLHandler implements SQLHandler {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DbMaintainSQLHandler.class);

    protected DefaultTransactionProvider defaultTransactionProvider;
    protected Map<DataSource, TransactionStatus> transactionStatuses = new IdentityHashMap<DataSource, TransactionStatus>(3);


    public DbMaintainSQLHandler(DefaultTransactionProvider defaultTransactionProvider) {
        this.defaultTransactionProvider = defaultTransactionProvider;
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
            throw new DatabaseException("Unable to perform database statement:\n" + sql, e);
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
            commitTransaction(dataSource);
            return nbChanges;

        } catch (Exception e) {
            throw new DatabaseException("Unable to perform database update:\n" + sql, e);
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
            throw new DatabaseException("Error while executing query:\n" + sql, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            releaseConnection(connection, dataSource);
        }
        // no value was found, throw an exception
        throw new DatabaseException("No value found for query:\n" + sql);
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
            throw new DatabaseException("Error while executing query:\n" + sql, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            releaseConnection(connection, dataSource);
        }
        // in case no value was found, throw an exception
        throw new DatabaseException("No value found for query:\n" + sql);
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
            throw new DatabaseException("Error while executing query:\n" + sql, e);
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
            throw new DatabaseException("Error while executing query:\n" + sql, e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            releaseConnection(connection, dataSource);
        }
    }


    public void startTransaction(DataSource dataSource) {
        PlatformTransactionManager platformTransactionManager = defaultTransactionProvider.getPlatformTransactionManager(null, dataSource);
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition(PROPAGATION_REQUIRED);
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        transactionStatuses.put(dataSource, transactionStatus);
    }

    public void endTransactionAndCommit(DataSource dataSource) {
        commitTransaction(dataSource);
        transactionStatuses.remove(dataSource);
    }

    public void endTransactionAndRollback(DataSource dataSource) {
        rollbackTransaction(dataSource);
        transactionStatuses.remove(dataSource);
    }

    public void closeAllConnections() {
    }


    protected void commitTransaction(DataSource dataSource) {
        TransactionStatus transactionStatus = transactionStatuses.get(dataSource);
        if (transactionStatus == null) {
            // nothing to commit
            return;
        }
        PlatformTransactionManager platformTransactionManager = defaultTransactionProvider.getPlatformTransactionManager(null, dataSource);
        platformTransactionManager.commit(transactionStatus);
    }

    protected void rollbackTransaction(DataSource dataSource) {
        TransactionStatus transactionStatus = transactionStatuses.get(dataSource);
        if (transactionStatus == null) {
            // nothing to rollback
            return;
        }
        PlatformTransactionManager platformTransactionManager = defaultTransactionProvider.getPlatformTransactionManager(null, dataSource);
        platformTransactionManager.rollback(transactionStatus);
    }
}
