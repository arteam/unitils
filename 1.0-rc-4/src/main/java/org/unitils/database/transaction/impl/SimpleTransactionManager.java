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
package org.unitils.database.transaction.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.TransactionManager;
import org.unitils.database.transaction.TransactionalDataSource;
import org.unitils.database.util.BaseConnectionProxy;
import org.unitils.database.util.BaseDataSourceProxy;
 
/**
 * Default, simple implementation of {@link TransactionManager}. Implements transactions by wrapping the Unitils
 * <code>DataSource</code>. This <code>DataSource</code> makes sure the same connection is returned for a thread
 * while a transaction is active, and that this connection is not closed while the transaction is active.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SimpleTransactionManager implements TransactionManager {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(SimpleTransactionManager.class);

    /**
     * Wrapped instance of the DataSource
     */
    protected SimpleTransactionalDataSource transactionalDataSource;
    
    /**
     * ThreadLocal used to remember for which thread a transaction has been started. If the DataSource is initialized after
     * a transaction was started, a transaction is started immediately afterwards on this DataSource
     */
    protected ThreadLocal<Boolean> transactionActiveFor = new ThreadLocal<Boolean>(); 


    /**
     * Makes sure the given data source is a transactional datasource.
     * <p/>
     * This will wrap the given data source, to make sure the transaction manager can manage the creation/destruction 
     * of connections. The wrapped data source is also installed in the database module.
     *
     * @param dataSource The original data source, not null
     * @return The transactional data source, not null
     */
    public TransactionalDataSource createTransactionalDataSource(DataSource dataSource) {
        if (transactionalDataSource != null) {
            throw new UnitilsException("A DataSource has already been registered with this transaction manager");
        }
        transactionalDataSource = new SimpleTransactionalDataSource(dataSource);
        
        // If startTransaction has been called before on this thread, a transaction is active. Since no DataSource
        // was available at the time of calling startTransaction, the transaction has not been initiated yet
        // on the DataSource. Therefore, we now call the startTransaction method on the transactional DataSource.
        if (transactionActiveFor.get() != null) {
            transactionalDataSource.startTransaction();
        }
        
        return transactionalDataSource;
    }


    /**
     * Starts a transaction for this thread and testObject
     *
     * @param testObject The test instance, not null
     */
    public void startTransaction(Object testObject) {
        if (transactionActiveFor.get() != null) {
            throw new UnitilsException("A transaction was already associated with this thread");
        }
        transactionActiveFor.set(Boolean.TRUE);
        
        if (transactionalDataSource == null) {
            // nothing to do
            return;
        }
        logger.debug("Starting transaction");
        transactionalDataSource.startTransaction();
    }


    /**
     * Commits the current transaction
     *
     * @param testObject The test instance, not null
     */
    public void commit(Object testObject) {
        if (transactionActiveFor.get() == null) {
            throw new UnitilsException("Trying to commit transaction, but no transaction has been intiated on this thread");
        }
        transactionActiveFor.remove();
        
        if (transactionalDataSource == null) {
            // nothing to do
            return;
        }
        logger.debug("Commiting transaction");
        transactionalDataSource.commitTransaction();
    }


    /**
     * Rolls back the current transaction
     *
     * @param testObject The test instance, not null
     */
    public void rollback(Object testObject) {
        if (transactionActiveFor.get() == null) {
            throw new UnitilsException("Trying to rollback transaction, but no transaction has been intiated on this thread");
        }
        transactionActiveFor.remove();
        
        if (transactionalDataSource == null) {
            // nothing to do
            return;
        }
        logger.debug("Rolling back transaction");
        transactionalDataSource.rollbackTransaction();
    }


    /**
     * Proxy for a DataSource that makes the DataSource transactional. Makes sure that the same connection
     * is returned for a thread while a transaction is active, and that this connection is not closed while
     * the transaction is active.
     */
    protected static class SimpleTransactionalDataSource extends BaseDataSourceProxy implements TransactionalDataSource {

        /**
         * ThreadLocal for storing the Connection associated to the treads using this DataSource
         */
        protected ThreadLocal<ConnectionHolder> connectionHolders = new ThreadLocal<ConnectionHolder>();


        /**
         * Creates a new instance without initializing the target <code>DataSource</code>. Make sure to call the method
         * {@link #setTargetDataSource(javax.sql.DataSource)} before using this object.
         */
        public SimpleTransactionalDataSource() {
            super();
        }


        /**
         * Creates a new instance that wraps the given <code>DataSource</code>
         *
         * @param wrappedDataSource the data source, not null
         */
        public SimpleTransactionalDataSource(DataSource wrappedDataSource) {
            super(wrappedDataSource);
        }


        /**
         * Starts a transaction for this current thread.
         *
         * @throws UnitilsException If a transaction was already associated with this thread
         */
        public void startTransaction() {
            if (connectionHolders.get() != null) {
                throw new UnitilsException("A transaction was already associated with this thread");
            }
            connectionHolders.set(new ConnectionHolder());
        }


        /**
         * Commits the transaction. This means the connection associated with this thread is committed
         * and closed. If no Connection has been requested since the transaction was started, nothing
         * happens. At the end, the connection is disassociated from the thread.
         *
         * @throws UnitilsException If no transaction was associated with this thread
         */
        public void commitTransaction() {
            ConnectionHolder connectionHolder = connectionHolders.get();
            if (connectionHolder == null) {
                throw new UnitilsException("Trying to commit transaction, but no transaction has been intiated on this thread");
            }
            CloseSuppressingConnectionProxy conn = connectionHolder.getConnection();
            if (conn != null) {
                try {
                    conn.commit();
                    conn.doClose();
                } catch (SQLException e) {
                    throw new UnitilsException("Error while closing Connection", e);
                }
            }
            connectionHolders.remove();
        }


        /**
         * Rolls back the transaction. This means the connection associated with this thread is rollbacked
         * and closed. If no Connection has been requested since the transaction was started, nothing
         * happens. At the end, the connection is disassociated from the thread.
         *
         * @throws UnitilsException If no transaction was associated with this thread
         */
        public void rollbackTransaction() {
            ConnectionHolder connectionHolder = connectionHolders.get();
            if (connectionHolder == null) {
                throw new UnitilsException("Trying to rollback transaction, but no transaction has been intiated on this thread");
            }
            CloseSuppressingConnectionProxy conn = connectionHolder.getConnection();
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.doClose();
                } catch (SQLException e) {
                    throw new UnitilsException("Error while closing Connection", e);
                }
            }
            connectionHolders.remove();
        }


        /**
         * Returns the connection that has been associated with this thread. If no connection was associated yet,
         * a connection is retrieved from the underlying DataSource and associated with the thread.
         */
        @Override
        public Connection getConnection() throws SQLException {
            return getConnection(new GetConnectionMethod() {

                public Connection getConnection() throws SQLException {
                    return getTargetDataSource().getConnection();
                }
            });
        }


        /**
         * Retrieves a connection that can participate in a transaction.
         * No special connection needed, same as {@link #getConnection()}
         *
         * @return The connection, not null
         */
        public Connection getTransactionalConnection() throws SQLException {
            return getConnection();
        }


        /**
         * Returns the connection that has been associated with this thread. If no connection was associated yet,
         * a connection is retrieved from the underlying DataSource and associated with the thread.
         */
        @Override
        public Connection getConnection(final String username, final String password) throws SQLException {
            return getConnection(new GetConnectionMethod() {

                public Connection getConnection() throws SQLException {
                    return getTargetDataSource().getConnection(username, password);
                }
            });
        }


        /**
         * Returns the connection that has been associated with this thread. If no connection was associated yet,
         * a connection is retrieved from the underlying DataSource and associated with the thread.
         *
         * @param getConnectionMethod The method that needs to be used for getting a Connection from the underlying
         *                            DataSource
         * @return a Connection
         * @throws SQLException If a problem occurs retrieving the connection from the underlying DataSource
         */
        protected Connection getConnection(GetConnectionMethod getConnectionMethod) throws SQLException {
            ConnectionHolder connectionHolder = connectionHolders.get();
            if (connectionHolder == null) {
                // No transaction has been initiated. Simply return a connection from the underlying DataSource
                Connection connection = getConnectionMethod.getConnection();
                // Switch to auto commit if necessary. This is very expensive in some JDBC drivers, so we don't want to do
                // it unnecessarily
                if (!connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                }
                return connection;
            }

            CloseSuppressingConnectionProxy connectionProxy = connectionHolder.getConnection();
            if (connectionProxy == null) {
                // First time that a Connection is requested during this transaction. Fetch a Connection and store it
                // in the ConnectionHolder
                connectionProxy = new CloseSuppressingConnectionProxy(getConnectionMethod.getConnection());
                // Switch to manual commit if necessary. This is very expensive in some JDBC drivers, so we don't want
                // to do it unnecessarily
                if (connectionProxy.getAutoCommit()) {
                    connectionProxy.setAutoCommit(false);
                }
                connectionHolder.setConnection(connectionProxy);
            }
            return connectionProxy;
        }

    }


    /**
     * Holder class for a Connection. Used for storing the Connection that is used for a transaction.
     */
    protected static class ConnectionHolder {

        /* The wrapped connection, which is a CloseSuppressingConnection */
        protected CloseSuppressingConnectionProxy connection;


        /**
         * @return The connection
         */
        public CloseSuppressingConnectionProxy getConnection() {
            return connection;
        }


        /**
         * Sets the connection to the given one
         *
         * @param connection The connection, not null
         */
        public void setConnection(CloseSuppressingConnectionProxy connection) {
            this.connection = connection;
        }
    }


    /**
     * Connection proxy that intercepts the call to the close() method, to make sure
     * the connection is only closed when the transaction ends.
     */
    protected static class CloseSuppressingConnectionProxy extends BaseConnectionProxy {


        /**
         * Constructs a new instance that proxies the given connection
         *
         * @param wrappedConnection The connection to wrap, not null
         */
        public CloseSuppressingConnectionProxy(Connection wrappedConnection) {
            super(wrappedConnection);
        }


        /**
         * Supresses the call to the close method, to make sure the connection is only closed
         * when the transaction ends.
         *
         * @see java.sql.Connection#close()
         */
        @Override
        public void close() throws SQLException {
            // Connection close is supressed
        }


        /**
         * Actually closes the connection.
         *
         * @throws SQLException If a problem occurs while closing the connection
         */
        public void doClose() throws SQLException {
            super.close();
        }
    }


    /**
     * Method used for retrieving a Connection from a DataSource
     */
    protected interface GetConnectionMethod {

        /**
         * @return A Connection from a DataSource
         * @throws SQLException If a problem occurs while retrieving the connection
         */
        public Connection getConnection() throws SQLException;
    }

}
