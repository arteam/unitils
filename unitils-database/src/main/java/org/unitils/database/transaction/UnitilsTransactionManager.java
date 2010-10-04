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
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.core.UnitilsException;

import javax.sql.DataSource;
import java.util.IdentityHashMap;
import java.util.Map;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;

/**
 * Implements transactions for unit tests, by delegating to a spring
 * <code>PlatformTransactionManager</code>. The concrete implementation of
 * <code>PlatformTransactionManager</code> that is used depends on the test
 * class. If a custom <code>PlatformTransactionManager</code> was configured
 * in a spring <code>ApplicationContext</code>, this one is used. If not, a
 * suitable subclass of <code>PlatformTransactionManager</code> is created,
 * depending on the configuration of a test. E.g. if some ORM persistence unit
 * was configured on the test, a <code>PlatformTransactionManager</code> that
 * can offer transactional behavior for such a persistence unit is used. If no
 * such configuration is found, a <code>DataSourceTransactionManager</code> is
 * used.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UnitilsTransactionManager {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(UnitilsTransactionManager.class);

    protected PlatformTransactionManager platformTransactionManager;
    protected Map<Object, TransactionStatus> testObjectTransactionStatusMap = new IdentityHashMap<Object, TransactionStatus>();


    /**
     * Starts the transaction on the PlatformTransactionManager for the given testObject.
     *
     * @param testObject         The test object, not null
     * @param applicationContext The spring application context, null if not defined
     * @param dataSource         The data source, not null
     */
    public void startTransaction(Object testObject, ApplicationContext applicationContext, DataSource dataSource) {
        if (platformTransactionManager == null) {
            platformTransactionManager = createPlatformTransactionManager(applicationContext, dataSource);
        }
        logger.debug("Starting transaction");

        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(createTransactionDefinition());
        testObjectTransactionStatusMap.put(testObject, transactionStatus);
    }

    /**
     * Commits the transaction. Uses the PlatformTransactionManager and transaction
     * that is associated with the given test object.
     *
     * @param testObject The test object, not null
     */
    public void commit(Object testObject) {
        TransactionStatus transactionStatus = testObjectTransactionStatusMap.get(testObject);
        if (transactionStatus == null) {
            throw new UnitilsException("Trying to commit, while no transaction is currently active");
        }
        platformTransactionManager.commit(transactionStatus);
        testObjectTransactionStatusMap.remove(testObject);
    }

    /**
     * Rolls back the transaction. Uses the PlatformTransactionManager and transaction
     * that is associated with the given test object.
     *
     * @param testObject The test object, not null
     */
    public void rollback(Object testObject) {
        TransactionStatus transactionStatus = testObjectTransactionStatusMap.get(testObject);
        if (transactionStatus == null) {
            throw new UnitilsException("Trying to rollback, while no transaction is currently active");
        }
        logger.debug("Rolling back transaction");
        platformTransactionManager.rollback(transactionStatus);
        testObjectTransactionStatusMap.remove(testObject);
    }


    /**
     * Returns a <code>TransactionDefinition</code> object containing the
     * necessary transaction parameters. Simply returns a default
     * <code>DefaultTransactionDefinition</code> object with the 'propagation
     * required' attribute
     *
     * @return The default TransactionDefinition
     */
    protected TransactionDefinition createTransactionDefinition() {
        // todo make configurable
        return new DefaultTransactionDefinition(PROPAGATION_REQUIRED);
    }

    protected PlatformTransactionManager createPlatformTransactionManager(ApplicationContext applicationContext, DataSource dataSource) {
        PlatformTransactionManager platformTransactionManager = getPlatformTransactionManagerFromApplicationContext(applicationContext);
        if (platformTransactionManager != null) {
            return platformTransactionManager;
        }
        return new DataSourceTransactionManager(dataSource);
    }

    protected PlatformTransactionManager getPlatformTransactionManagerFromApplicationContext(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return null;
        }
        try {
            return applicationContext.getBean(PlatformTransactionManager.class); // todo configurable
        } catch (Exception e) {
            // no spring application context defined
            return null;
        }
    }
}
