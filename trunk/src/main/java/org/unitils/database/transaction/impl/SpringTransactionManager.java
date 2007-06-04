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

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.TransactionManager;
import org.unitils.database.transaction.TransactionalDataSource;
import org.unitils.database.util.BaseConnectionProxy;
import org.unitils.dbmaintainer.util.BaseDataSourceProxy;
import org.unitils.spring.SpringModule;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction manager that relies on Spring transaction management. When starting a Transaction, this transaction
 * manager tries to locate a configured <code>org.springframework.transaction.PlatformTransactionManager</code> bean
 * instance in the spring ApplicationContext configured in the {@link SpringModule} for this testObject. If no such
 * bean was configured for this test, a <code>org.springframework.jdbc.datasource.DataSourceTransactionManager</code>
 * instance is created.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SpringTransactionManager implements TransactionManager {

    /**
     * ThreadLocal for holding the TransactionStatus as used by spring's transaction management
     */
    private ThreadLocal<TransactionStatus> transactionStatusHolder = new ThreadLocal<TransactionStatus>();


    /**
     * Makes the given data source a transactional datasource.
     * If no action needs to be performed, the given data source should be returned.
     * <p/>
     * This could for example be used to wrap the given data source for intercepting the creation of connections.
     *
     * @param dataSource The original data source, not null
     * @return The transactional data source, not null
     */
    public TransactionalDataSource createTransactionalDataSource(DataSource dataSource) {
        return new SpringTransactionalDataSource(dataSource);
    }


    /**
     * Starts the transaction. Will start a transaction on the PlatformTransactionManager that is configured
     * in the spring application context associated with the given testObject.
     *
     * @param testObject The test object, not null
     */
    public void startTransaction(Object testObject) {
        PlatformTransactionManager springTransactionManager = getSpringTransactionManager(testObject);
        TransactionStatus transactionStatus = springTransactionManager.getTransaction(createTransactionDefinition(testObject));
        transactionStatusHolder.set(transactionStatus);
    }


    /**
     * Commits the transaction. Will commit on the PlatformTransactionManager that is configured
     * in the spring application context associated with the given testObject.
     *
     * @param testObject The test object, not null
     */
    public void commit(Object testObject) {
        TransactionStatus transactionStatus = transactionStatusHolder.get();
        if (transactionStatus == null) {
            throw new UnitilsException("Trying to commit, while no transaction is currently active");
        }
        getSpringTransactionManager(testObject).commit(transactionStatus);
        transactionStatusHolder.remove();
    }


    /**
     * Rolls back the transaction. Will rollback on the PlatformTransactionManager that is configured
     * in the spring application context associated with the given testObject.
     *
     * @param testObject The test object, not null
     */
    public void rollback(Object testObject) {
        TransactionStatus transactionStatus = transactionStatusHolder.get();
        if (transactionStatus == null) {
            throw new UnitilsException("Trying to rollback, while no transaction is currently active");
        }
        getSpringTransactionManager(testObject).rollback(transactionStatus);
        transactionStatusHolder.remove();
    }


    /**
     * Returns a <code>TransactionDefinition</code> object containing the necessary transaction parameters. Simply
     * returns a default <code>DefaultTransactionDefinition</code> object without specifying any custom properties on
     * it.
     *
     * @param testObject The test object, not null
     * @return The default TransactionDefinition
     */
    protected TransactionDefinition createTransactionDefinition(Object testObject) {
        return new DefaultTransactionDefinition();
    }


    /**
     * @param testObject The test object, not null
     * @return The <code>PlatformTransactionManager</code> that is configured in the spring application context associated with the given testObject.
     * @throws UnitilsException If no <code>PlatformTransactionManager</code> was configured for the given test object
     */
    protected PlatformTransactionManager getSpringTransactionManager(Object testObject) {
        return (PlatformTransactionManager) getSpringModule().getSpringBeanByType(testObject, PlatformTransactionManager.class);
    }


    /**
     * @return The Spring module
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }


    /**
     * todo javadoc
     */
    protected static class SpringTransactionalDataSource extends BaseDataSourceProxy implements TransactionalDataSource {


        /**
         * Creates a new instance without initializing the target <code>DataSource</code>. Make sure to call the method
         * {@link #setTargetDataSource(javax.sql.DataSource)} before using this object.
         */
        public SpringTransactionalDataSource() {
            super();
        }


        /**
         * Creates a new instance that wraps the given <code>DataSource</code>
         *
         * @param wrappedDataSource the data source, not null
         */
        public SpringTransactionalDataSource(DataSource wrappedDataSource) {
            super(wrappedDataSource);
        }


        //todo javadoc
        public Connection getTransactionalConnection() throws SQLException {
            return new CloseSuppressingConnectionProxy(DataSourceUtils.getConnection(this));
        }


        public Connection getConnection() throws SQLException {
            return new CloseSuppressingConnectionProxy(super.getConnection());
        }


        /**
         * Connection proxy that intercepts the call to the close() method, to make sure
         * the connection is only closed when the transaction ends.
         */
        protected class CloseSuppressingConnectionProxy extends BaseConnectionProxy {

            /**
             * Constructs a new instance that proxies the given connection
             *
             * @param wrappedConnection The connection to wrap, not null
             */
            public CloseSuppressingConnectionProxy(Connection wrappedConnection) {
                super(wrappedConnection);
            }

            /**
             * todo javdoc
             * Supresses the call to the close method, to make sure the connection is only closed
             * when the transaction ends.
             *
             * @see java.sql.Connection#close()
             */
            @Override
            public void close() throws SQLException {
                // Let spring close the connection
                // only close when not in a transaction
                DataSourceUtils.doReleaseConnection(getTargetConnection(), SpringTransactionalDataSource.this);
            }

        }
    }


}
