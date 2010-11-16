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
package org.unitils.database.transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.core.UnitilsException;

import javax.sql.DataSource;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;

/**
 * Implements transactions for unit tests, by delegating to a spring <code>PlatformTransactionManager</code>.
 * The concrete implementation of <code>PlatformTransactionManager</code> that is used depends on the test
 * class. If a custom <code>PlatformTransactionManager</code> was configured in a spring <code>ApplicationContext</code>,
 * this one is used. If not, a <code>DataSourceTransactionManager</code> is created,
 * depending on the configuration of a test.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UnitilsTransactionManager {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(UnitilsTransactionManager.class);

    /* The current transaction instance, null if no transaction is active */
    protected TransactionInstance transactionInstance;


    /**
     * Starts a transaction for the give data source using a {@link DataSourceTransactionManager}.
     * If a transaction is already started for the given data source, the start will be ignored.
     * If a transaction is already started for another data source, an expception will be raised.
     *
     * @param dataSource The data source, not null
     */
    public void startTransactionForDataSource(DataSource dataSource) {
        if (transactionInstance != null) {
            if (transactionInstance.getDataSource() == dataSource) {
                // transaction already active, ignore
                return;
            }
            throw new UnitilsException("Unable to start transaction: a transaction for another data source is already active for this test.\n" +
                    "A transaction can only be started for 1 data source at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context. See the tutorial for more info.");
        }

        logger.debug("Starting transaction.");
        PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(dataSource);
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(createTransactionDefinition());
        transactionInstance = new TransactionInstance(platformTransactionManager, transactionStatus, dataSource);
    }


    /**
     * Commits the current transaction.
     * An exception will be raised if no transaction is currently active.
     */
    public void commit() {
        if (transactionInstance == null) {
            throw new UnitilsException("Trying to commit while no transaction is currently active. Make sure to call startTransaction to start a transaction.");
        }
        logger.debug("Committing transaction.");
        PlatformTransactionManager platformTransactionManager = transactionInstance.getPlatformTransactionManager();
        TransactionStatus transactionStatus = transactionInstance.getTransactionStatus();
        platformTransactionManager.commit(transactionStatus);
        transactionInstance = null;
    }

    /**
     * Rolls back the current transaction.
     * An exception will be raised if no transaction is currently active.
     */
    public void rollback() {
        if (transactionInstance == null) {
            throw new UnitilsException("Trying to rollback while no transaction is currently active. Make sure to call startTransaction to start a transaction.");
        }
        logger.debug("Rolling back transaction.");
        PlatformTransactionManager platformTransactionManager = transactionInstance.getPlatformTransactionManager();
        TransactionStatus transactionStatus = transactionInstance.getTransactionStatus();
        platformTransactionManager.rollback(transactionStatus);
        transactionInstance = null;
    }


    /**
     * Returns a @{link TransactionDefinition} to use when startin a transaction.
     * Defaults to a transaction definition with propagation required.
     *
     * @return The transaction definition, not null
     */
    protected TransactionDefinition createTransactionDefinition() {
        return new DefaultTransactionDefinition(PROPAGATION_REQUIRED);
    }


    protected static class TransactionInstance {

        private PlatformTransactionManager platformTransactionManager;
        private TransactionStatus transactionStatus;
        private DataSource dataSource;

        public TransactionInstance(PlatformTransactionManager platformTransactionManager, TransactionStatus transactionStatus, DataSource dataSource) {
            this.platformTransactionManager = platformTransactionManager;
            this.transactionStatus = transactionStatus;
            this.dataSource = dataSource;
        }

        public PlatformTransactionManager getPlatformTransactionManager() {
            return platformTransactionManager;
        }

        public TransactionStatus getTransactionStatus() {
            return transactionStatus;
        }

        public DataSource getDataSource() {
            return dataSource;
        }
    }
}
