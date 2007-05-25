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
package org.unitils.database.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModule;
import org.unitils.database.util.BaseConnectionProxy;
import org.unitils.database.util.DynamicThreadLocalDataSourceProxy;
import org.unitils.dbmaintainer.util.BaseDataSourceProxy;

/**
 * Default, simple implementation of {@link TransactionManager}. Implements transactions by wrapping the Unitils
 * <code>DataSource</code>. This <code>DataSource</code> makes sure the same connection is returned for a thread
 * while a transaction is active, and that this connection is not closed while the transaction is active.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SimpleTransactionManager implements TransactionManager {

    /**
     * Wrapped instance of the DataSource
     */
    private SimpleTransactionalDataSourceProxy dataSourceProxy = new SimpleTransactionalDataSourceProxy();

    /**
     * Starts a transaction for this thread and testObject
     * 
     * @param testObject The test instance, not null
     */
    public void startTransaction(Object testObject) {
        dataSourceProxy.startTransaction();
        getDataSource().registerDataSourceProxy(dataSourceProxy);
    }

    /**
     * Commits the current transaction
     * 
     * @param testObject The test instance, not null
     */
    public void commit(Object testObject) {
        getDataSource().deRegisterDataSourceProxy();
        dataSourceProxy.commitTransaction();
    }

    /**
     * Rolls back the current transaction
     * 
     * @param testObject The test instance, not null
     */
    public void rollback(Object testObject) {
        getDataSource().deRegisterDataSourceProxy();
        dataSourceProxy.rollbackTransaction();
    }

    
    /**
     * @param testObject The test instance, not null 
     * 
     * @return true
     */
    public boolean isActive(Object testObject) {
        return true;
    }

    
    /**
     * @return The unitils DataSource
     */
    protected DynamicThreadLocalDataSourceProxy getDataSource() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).getDataSource();
    }

    /**
     * Proxy for a DataSource that makes the DataSource transactional. Makes sure that the same connection 
     * is returned for a thread while a transaction is active, and that this connection is not closed while 
     * the transaction is active.
     */
    protected class SimpleTransactionalDataSourceProxy extends BaseDataSourceProxy {

        /**
         * ThreadLocal for storing the Connection associated to the treads using this DataSource
         */
        private ThreadLocal<ConnectionHolder> connectionHolders = new ThreadLocal<ConnectionHolder>();


        /**
         * Creates a new instance without initializing the target <code>DataSource</code>. Make sure to call the method
         * {@link #setTargetDataSource(javax.sql.DataSource)} before using this object.
         */
        public SimpleTransactionalDataSourceProxy() {
            super();
        }

        /**
         * Creates a new instance that wraps the given <code>DataSource</code>
         *
         * @param wrappedDataSource the data source, not null
         */
        public SimpleTransactionalDataSourceProxy(DataSource wrappedDataSource) {
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
            } else {
                connectionHolders.set(new ConnectionHolder());
            }
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
            } else {
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
            } else {
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
        private Connection getConnection(GetConnectionMethod getConnectionMethod) throws SQLException {
            ConnectionHolder connectionHolder = connectionHolders.get();
            if (connectionHolder == null) {
                // No transaction has been initiated. Simply return a connection from the underlying DataSource
                Connection conn = getConnectionMethod.getConnection();
                // Switch to auto commit if necessary. This is very expensive in some JDBC drivers, so we don't want to do
                // it unnecessarily
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
                return conn;
            } else {
                CloseSuppressingConnectionProxy conn = connectionHolder.getConnection();
                if (conn == null) {
                    // First time that a Connection is requested during this transaction. Fetch a Connection and store it
                    // in the ConnectionHolder
                    conn = new CloseSuppressingConnectionProxy(getConnectionMethod.getConnection());
                    // Switch to manual commit if necessary. This is very expensive in some JDBC drivers, so we don't want
                    // to do it unnecessarily
                    if (conn.getAutoCommit()) {
                        conn.setAutoCommit(false);
                    }
                    connectionHolder.setConnection(conn);
                }
                return conn;
            }
        }

        /**
         * Holder class for a Connection. Used for storing the Connection that is used for a transaction.
         */
        protected class ConnectionHolder {

            /* The wrapped connection, which is a CloseSuppressingConnection */
            private CloseSuppressingConnectionProxy connection;

            /**
             * @return The connection
             */
            public CloseSuppressingConnectionProxy getConnection() {
                return connection;
            }

            /**
             * Sets the connection to the given one
             * @param connection
             */
            public void setConnection(CloseSuppressingConnectionProxy connection) {
                this.connection = connection;
            }
        }
    }

    /**
     * Connection proxy that intercepts the call to the close() method, to make sure
     * the connection is only closed when the transaction ends. 
     */
    protected class CloseSuppressingConnectionProxy extends BaseConnectionProxy {

        /**
         * Constructs a new instance that proxies the given connection
         * @param wrappedConnection
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
