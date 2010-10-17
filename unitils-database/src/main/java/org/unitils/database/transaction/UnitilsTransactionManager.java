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
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

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

    protected Map<Object, List<TransactionManagerAndStatus>> transactionManagersAndStatus = new IdentityHashMap<Object, List<TransactionManagerAndStatus>>();


    public void startTransactionOnTransactionManagersInApplicationContext(Object testObject, List<String> transactionManagerBeanNames, ApplicationContext applicationContext) {
        List<TransactionManagerAndStatus> transactionManagersAndStatusForTestObject = getTransactionManagersAndStatusForTestObject(testObject);
        if (!transactionManagersAndStatusForTestObject.isEmpty()) {
            throw new UnitilsException("Unable to start transaction. A transaction was already started for this test object.");
        }

        if (transactionManagerBeanNames.isEmpty()) {
            PlatformTransactionManager platformTransactionManager = getDefaultPlatformTransactionManagerFromApplicationContext(applicationContext);
            if (platformTransactionManager == null) {
                return;
            }
            logger.debug("Starting transaction on configured Spring PlatformTransactionManager bean.");
            TransactionManagerAndStatus transactionManagerAndStatus = startTransaction(platformTransactionManager);
            transactionManagersAndStatusForTestObject.add(transactionManagerAndStatus);
            return;
        }
        for (String transactionManagerBeanName : transactionManagerBeanNames) {
            logger.debug("Starting transaction on configured Spring PlatformTransactionManager bean with name " + transactionManagerBeanName + ".");
            PlatformTransactionManager platformTransactionManager = getPlatformTransactionManagerFromApplicationContext(transactionManagerBeanName, applicationContext);
            TransactionManagerAndStatus transactionManagerAndStatus = startTransaction(platformTransactionManager);
            transactionManagersAndStatusForTestObject.add(transactionManagerAndStatus);
        }
    }

    /**
     * Starts the transaction on the PlatformTransactionManager for the given testObject.
     *
     * @param testObject The test object, not null
     * @param dataSource The data source, not null
     */
    public void startTransactionForDataSource(Object testObject, DataSource dataSource, boolean ignoreIfAlreadyActive) {
        List<TransactionManagerAndStatus> transactionManagersAndStatusForTestObject = getTransactionManagersAndStatusForTestObject(testObject);
        if (!transactionManagersAndStatusForTestObject.isEmpty()) {
            if (ignoreIfAlreadyActive) {
                return;
            }
            throw new UnitilsException("Unable to start transaction. A transaction was already started for this test object.");
        }

        logger.debug("Starting transaction using default transaction manager.");
        PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(dataSource);
        TransactionManagerAndStatus transactionManagerAndStatus = startTransaction(platformTransactionManager);
        transactionManagersAndStatusForTestObject.add(transactionManagerAndStatus);
    }


    /**
     * Commits the transaction. Uses the PlatformTransactionManager and transaction
     * that is associated with the given test object.
     *
     * @param testObject The test object, not null
     */
    public void commit(Object testObject) {
        List<TransactionManagerAndStatus> transactionManagersAndStatusForTestObject = getTransactionManagersAndStatusForTestObject(testObject);
        if (transactionManagersAndStatusForTestObject.isEmpty()) {
            throw new UnitilsException("Trying to commit while no transaction is currently active. Make sure to call startTransaction to start a transaction.");
        }
        logger.debug("Committing transaction(s).");
        for (TransactionManagerAndStatus transactionManagerAndStatus : transactionManagersAndStatusForTestObject) {
            PlatformTransactionManager platformTransactionManager = transactionManagerAndStatus.getPlatformTransactionManager();
            TransactionStatus transactionStatus = transactionManagerAndStatus.getTransactionStatus();
            platformTransactionManager.commit(transactionStatus);
        }
        transactionManagersAndStatus.remove(testObject);
    }

    /**
     * Rolls back the transaction. Uses the PlatformTransactionManager and transaction
     * that is associated with the given test object.
     *
     * @param testObject The test object, not null
     */
    public void rollback(Object testObject) {
        List<TransactionManagerAndStatus> transactionManagersAndStatusForTestObject = getTransactionManagersAndStatusForTestObject(testObject);
        if (transactionManagersAndStatusForTestObject.isEmpty()) {
            throw new UnitilsException("Trying to rollback while no transaction is currently active. Make sure to call startTransaction to start a transaction.");
        }
        logger.debug("Rolling back transaction(s).");
        for (TransactionManagerAndStatus transactionManagerAndStatus : transactionManagersAndStatusForTestObject) {
            PlatformTransactionManager platformTransactionManager = transactionManagerAndStatus.getPlatformTransactionManager();
            TransactionStatus transactionStatus = transactionManagerAndStatus.getTransactionStatus();
            platformTransactionManager.rollback(transactionStatus);
        }
        transactionManagersAndStatus.remove(testObject);
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
        return new DefaultTransactionDefinition(PROPAGATION_REQUIRED);
    }


    protected List<TransactionManagerAndStatus> getTransactionManagersAndStatusForTestObject(Object testObject) {
        List<TransactionManagerAndStatus> transactionManagersAndStatusForTestObject = transactionManagersAndStatus.get(testObject);
        if (transactionManagersAndStatusForTestObject == null) {
            transactionManagersAndStatusForTestObject = new ArrayList<TransactionManagerAndStatus>();
            transactionManagersAndStatus.put(testObject, transactionManagersAndStatusForTestObject);
        }
        return transactionManagersAndStatusForTestObject;
    }

    protected TransactionManagerAndStatus startTransaction(PlatformTransactionManager platformTransactionManager) {
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(createTransactionDefinition());
        return new TransactionManagerAndStatus(platformTransactionManager, transactionStatus);
    }


    protected PlatformTransactionManager getDefaultPlatformTransactionManagerFromApplicationContext(ApplicationContext applicationContext) {
        Map<String, PlatformTransactionManager> platformTransactionManagers = applicationContext.getBeansOfType(PlatformTransactionManager.class);
        if (platformTransactionManagers.isEmpty()) {
            return null;
        }
        if (platformTransactionManagers.size() > 1) {
            throw new UnitilsException("Unable to get default transaction manager from test application context. More than one bean of type PlatformTransactionManager found. Please specify the bean name explicitly in the @Transactional annotation.");
        }
        return platformTransactionManagers.values().iterator().next();
    }

    protected PlatformTransactionManager getPlatformTransactionManagerFromApplicationContext(String transactionManagerBeanName, ApplicationContext applicationContext) {
        try {
            return applicationContext.getBean(transactionManagerBeanName, PlatformTransactionManager.class);
        } catch (Exception e) {
            throw new UnitilsException("Unable to get transaction manager for name " + transactionManagerBeanName + " from test application context: " + e.getMessage(), e);
        }
    }


    protected static class TransactionManagerAndStatus {

        private PlatformTransactionManager platformTransactionManager;
        private TransactionStatus transactionStatus;

        public TransactionManagerAndStatus(PlatformTransactionManager platformTransactionManager, TransactionStatus transactionStatus) {
            this.platformTransactionManager = platformTransactionManager;
            this.transactionStatus = transactionStatus;
        }

        public PlatformTransactionManager getPlatformTransactionManager() {
            return platformTransactionManager;
        }

        public TransactionStatus getTransactionStatus() {
            return transactionStatus;
        }
    }
}
