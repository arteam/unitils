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
package org.unitils.database.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.TransactionProvider;
import org.unitils.database.transaction.TransactionProviderManager;

import javax.sql.DataSource;

import static org.apache.commons.lang.StringUtils.isBlank;
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

    protected TransactionProviderManager transactionProviderManager;

    protected PlatformTransactionManager platformTransactionManager;
    protected String transactionManagerName;
    protected boolean transactionStarted;
    protected DataSource dataSource;
    protected TransactionStatus transactionStatus;


    public TransactionManager(TransactionProviderManager transactionProviderManager) {
        this.transactionProviderManager = transactionProviderManager;
    }


    public void startTransaction(String transactionManagerName) {
        if (transactionManagerName == null) {
            transactionManagerName = "";
        }
        if (transactionStarted) {
            if (!this.transactionManagerName.equals(transactionManagerName)) {
                String newTransactionManagerName = isBlank(transactionManagerName) ? "the default transaction manager" : ("transaction manager with name '" + transactionManagerName + "'");
                String currentTransactionManagerName = isBlank(this.transactionManagerName) ? "the default transaction manager" : ("transaction manager with name '" + this.transactionManagerName + "'");
                throw new UnitilsException("Unable to start transaction for " + newTransactionManagerName + ". A transaction for " + currentTransactionManagerName + " is already active for this test.\n" +
                        "A transaction can only be started for 1 transaction manager at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context.");
            }
            // transaction already active, ignore
            return;
        }
        this.transactionManagerName = transactionManagerName;
        this.transactionStarted = true;

        if (!isTransactionActive() && dataSource != null) {
            doStartTransaction();
        }
    }

    public void registerDataSource(DataSource dataSource) {
        if (isTransactionActive()) {
            if (this.dataSource != dataSource) {
                throw new UnitilsException("Unable to register data source. A transaction for another data source is already active for this test.\n" +
                        "A transaction can only be started for 1 data source at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context.");
            }
            // transaction already active, ignore
            return;
        }
        this.dataSource = dataSource;
        if (isTransactionStarted()) {
            doStartTransaction();
        }
    }

    public boolean isTransactionStarted() {
        return transactionStarted;
    }

    public boolean isTransactionActive() {
        return transactionStatus != null && platformTransactionManager != null;
    }


    /**
     * Commits the current transaction.
     */
    public void commit(boolean endTransaction) {
        if (!isTransactionActive()) {
            throw new UnitilsException("Unable to commit. No transaction is currently active. Make sure to call startTransaction to start a transaction.");
        }
        logger.debug("Committing transaction.");
        platformTransactionManager.commit(transactionStatus);
        transactionStatus = null;

        if (endTransaction) {
            doEndTransaction();
        }
    }

    /**
     * Rolls back the current transaction.
     */
    public void rollback(boolean endTransaction) {
        if (!isTransactionActive()) {
            throw new UnitilsException("Unable to rollback. No transaction is currently active. Make sure to call startTransaction to start a transaction.");
        }
        logger.debug("Rolling back transaction.");
        platformTransactionManager.rollback(transactionStatus);
        transactionStatus = null;

        if (endTransaction) {
            doEndTransaction();
        }
    }


    protected void doStartTransaction() {
        logger.debug("Starting transaction.");
        TransactionProvider transactionProvider = transactionProviderManager.getTransactionProvider();
        TransactionDefinition transactionDefinition = createTransactionDefinition();

        platformTransactionManager = transactionProvider.getPlatformTransactionManager(transactionManagerName, dataSource);
        transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
    }

    protected void doEndTransaction() {
        transactionStarted = false;
        platformTransactionManager = null;
        transactionManagerName = null;
    }


    /**
     * Returns a @{link TransactionDefinition} to use when starting a transaction.
     * Defaults to a transaction definition with propagation required.
     *
     * @return The transaction definition, not null
     */
    protected TransactionDefinition createTransactionDefinition() {
        return new DefaultTransactionDefinition(PROPAGATION_REQUIRED);
    }
}
