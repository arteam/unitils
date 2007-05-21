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

import org.unitils.database.util.BaseConnectionDecorator;
import org.unitils.dbmaintainer.util.BaseDataSourceDecorator;
import org.unitils.core.UnitilsException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SimpleTransactionManager extends BaseTransactionManager<SimpleTransactionManager.SimpleTransactionalDataSource> {

    protected void doStartTransaction(Object testObject) {
        dataSource.startTransaction();
    }

    protected void doCommit(Object testObject) {
        dataSource.commitTransaction();
    }

    protected void doRollback(Object testObject) {
        dataSource.rollbackTransaction();
    }

    public SimpleTransactionalDataSource wrapDataSource(DataSource originalDataSource) {
        return new SimpleTransactionalDataSource(originalDataSource);
    }

    protected class SimpleTransactionalDataSource extends BaseDataSourceDecorator {

        private ThreadLocal<ConnectionHolder> connectionHolders = new ThreadLocal<ConnectionHolder>();

        /**
         * Creates a new instance that wraps the given <code>DataSource</code>
         *
         * @param wrappedDataSource the data source, not null
         */
        public SimpleTransactionalDataSource(DataSource wrappedDataSource) {
            super(wrappedDataSource);
        }

        public void startTransaction() {
            if (connectionHolders.get() != null) {
                throw new UnitilsException("A transaction was already associated with this thread");
            } else {
                connectionHolders.set(new ConnectionHolder());
            }
        }

        public void commitTransaction() {
            ConnectionHolder connectionHolder = connectionHolders.get();
            if (connectionHolder == null) {
                throw new UnitilsException("Trying to commit transaction, but no transaction has been intiated on this thread");
            } else {
                CloseSuppressingConnection conn = connectionHolder.getConnection();
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

        public void rollbackTransaction() {
            ConnectionHolder connectionHolder = connectionHolders.get();
            if (connectionHolder == null) {
                throw new UnitilsException("Trying to commit transaction, but no transaction has been intiated on this thread");
            } else {
                CloseSuppressingConnection conn = connectionHolder.getConnection();
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

        public Connection getConnection() throws SQLException {
            return getConnection(new GetConnectionMethod() {

                public Connection getConnection() throws SQLException {
                    return wrappedDataSource.getConnection();
                }
            });
        }

        public Connection getConnection(final String username, final String password) throws SQLException {
            return getConnection(new GetConnectionMethod() {

                public Connection getConnection() throws SQLException {
                    return wrappedDataSource.getConnection(username, password);
                }
            });
        }

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
                CloseSuppressingConnection conn = connectionHolder.getConnection();
                if (conn == null) {
                    // First time that a Connection is requested during this transaction. Fetch a Connection and store it
                    // in the ConnectionHolder
                    conn = new CloseSuppressingConnection(getConnectionMethod.getConnection());
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

        protected class ConnectionHolder {

            private CloseSuppressingConnection connection;

            public CloseSuppressingConnection getConnection() {
                return connection;
            }

            public void setConnection(CloseSuppressingConnection connection) {
                this.connection = connection;
            }
        }
    }

    protected class CloseSuppressingConnection extends BaseConnectionDecorator {

        public CloseSuppressingConnection(Connection wrappedConnection) {
            super(wrappedConnection);
        }

        public void close() throws SQLException {
            // Connection close is supressed
        }

        public void doClose() throws SQLException {
            super.close();
        }
    }

    protected interface GetConnectionMethod {

        public Connection getConnection() throws SQLException;
    }

}
