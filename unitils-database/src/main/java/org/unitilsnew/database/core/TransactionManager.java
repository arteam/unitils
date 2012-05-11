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
package org.unitilsnew.database.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.core.UnitilsException;

import javax.sql.DataSource;
import java.util.IdentityHashMap;
import java.util.Map;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;

/**
 * Implements transactions for unit tests, by delegating to a spring <code>PlatformTransactionManager</code>.
 * The concrete implementation of <code>PlatformTransactionManager</code> that is used depends on the test
 * class. If a custom <code>PlatformTransactionManager</code> was configured in a spring <code>ApplicationContext</code>,
 * this one is used. If not, a <code>DataSourceTransactionManager</code> is created,
 * depending on the configuration of a test.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TransactionManager {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(TransactionManager.class);

    protected boolean startTransactionWhenDataSourceIsRegistered;
    protected DataSource dataSource;
    protected TransactionStatus transactionStatus;

    protected static Map<DataSource, DataSourceTransactionManager> cachedDataSourceTransactionManagers = new IdentityHashMap<DataSource, DataSourceTransactionManager>(3);


    public void startTransaction() {
        if (transactionStatus != null) {
            throw new UnitilsException("Unable to start transaction. A transaction is already active. Make sure to call commit or rollback to end the transaction.");
        }
        if (dataSource == null) {
            startTransactionWhenDataSourceIsRegistered = true;
            return;
        }
        startTransaction(dataSource);
    }

    public void registerDataSource(DataSource dataSource) {
        if (startTransactionWhenDataSourceIsRegistered) {
            startTransaction(dataSource);
        }
        this.dataSource = dataSource;
    }

    protected void startTransaction(DataSource dataSource) {
        startTransactionWhenDataSourceIsRegistered = false;

        if (transactionStatus != null) {
            if (this.dataSource != dataSource) {
                throw new UnitilsException("Unable to start transaction: a transaction for another data source is already active for this test.\n" +
                        "A transaction can only be started for 1 data source at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context. See the tutorial for more info.");
            }
            // transaction already active, ignore
            return;
        }

        logger.debug("Starting transaction.");
        DataSourceTransactionManager dataSourceTransactionManager = getDataSourceTransactionManager(dataSource);
        TransactionDefinition transactionDefinition = createTransactionDefinition();
        transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
    }

    /**
     * Commits the current transaction.
     * Nothing happens if no transaction is currently active.
     */
    public void commit() {
        if (transactionStatus == null) {
            throw new UnitilsException("Trying to commit while no transaction is currently active. Make sure to call startTransaction to start a transaction.");
        }
        logger.debug("Committing transaction.");
        DataSourceTransactionManager dataSourceTransactionManager = getDataSourceTransactionManager(dataSource);
        dataSourceTransactionManager.commit(transactionStatus);
        transactionStatus = null;
    }

    /**
     * Rolls back the current transaction.
     * Nothing happens if no transaction is currently active.
     */
    public void rollback() {
        if (transactionStatus == null) {
            throw new UnitilsException("Trying to rollback while no transaction is currently active. Make sure to call startTransaction to start a transaction.");
        }
        logger.debug("Rolling back transaction.");
        DataSourceTransactionManager dataSourceTransactionManager = getDataSourceTransactionManager(dataSource);
        dataSourceTransactionManager.rollback(transactionStatus);
        transactionStatus = null;
    }


    protected synchronized DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = cachedDataSourceTransactionManagers.get(dataSource);
        if (dataSourceTransactionManager == null) {
            dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
            cachedDataSourceTransactionManagers.put(dataSource, dataSourceTransactionManager);
        }
        return dataSourceTransactionManager;
    }

    /**
     * Returns a @{link TransactionDefinition} to use when starting a transaction.
     * Defaults to a transaction definition with propagation required.
     *
     * @return The transaction definition, not null
     */
    protected TransactionDefinition createTransactionDefinition() {
        return new DefaultTransactionDefinition(PROPAGATION_REQUIRED); // todo make configurable?
    }
}
