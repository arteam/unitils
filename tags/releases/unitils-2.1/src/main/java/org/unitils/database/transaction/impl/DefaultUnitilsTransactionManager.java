/*
 * Copyright 2008,  Unitils.org
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.UnitilsTransactionManager;
import org.unitils.spring.SpringModule;

import javax.sql.DataSource;
import java.util.*;

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
public class DefaultUnitilsTransactionManager implements UnitilsTransactionManager {

    /**
     * The logger instance for this class
     */
    private static Log logger = LogFactory.getLog(DefaultUnitilsTransactionManager.class);

    protected Map<Object, Boolean> testObjectTransactionActiveMap = new HashMap<Object, Boolean>();

    /**
     * ThreadLocal for holding the TransactionStatus that keeps track of the
     * current test's transaction status
     */
    protected Map<Object, TransactionStatus> testObjectTransactionStatusMap = new HashMap<Object, TransactionStatus>();

    /**
     * ThreadLocal for holding the PlatformTransactionManager that is used by
     * the current test
     */
    protected Map<Object, PlatformTransactionManager> testObjectPlatformTransactionManagerMap = new HashMap<Object, PlatformTransactionManager>();


    /**
     * Set of possible providers of a spring
     * <code>PlatformTransactionManager</code>, not null
     */
    protected List<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations;

    public void init(Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations) {
        setTransactionManagementConfigurations(transactionManagementConfigurations);
    }


    /**
     * Returns the given datasource, wrapped in a spring
     * <code>TransactionAwareDataSourceProxy</code>
     */
    public DataSource getTransactionalDataSource(DataSource dataSource) {
        return new TransactionAwareDataSourceProxy(dataSource);
    }


    /**
     * Starts the transaction. Starts a transaction on the
     * PlatformTransactionManager that is configured for the given testObject.
     *
     * @param testObject The test object, not null
     */
    public void startTransaction(Object testObject) {
        UnitilsTransactionManagementConfiguration transactionManagementConfiguration = getTransactionManagementConfiguration(testObject);
        if (transactionManagementConfiguration.isTransactionalResourceAvailable(testObject)) {
            testObjectTransactionActiveMap.put(testObject, Boolean.TRUE);
            doStartTransaction(testObject, transactionManagementConfiguration);
        } else {
            testObjectTransactionActiveMap.put(testObject, Boolean.FALSE);
        }
    }


    public void activateTransactionIfNeeded(Object testObject) {
        if (testObjectTransactionActiveMap.containsKey(testObject) && !testObjectTransactionActiveMap.get(testObject)) {
            testObjectTransactionActiveMap.put(testObject, Boolean.TRUE);
            UnitilsTransactionManagementConfiguration transactionManagementConfiguration = getTransactionManagementConfiguration(testObject);
            doStartTransaction(testObject, transactionManagementConfiguration);
        }
    }


    protected void doStartTransaction(Object testObject, UnitilsTransactionManagementConfiguration transactionManagementConfiguration) {
        logger.debug("Starting transaction");
        PlatformTransactionManager platformTransactionManager = transactionManagementConfiguration.getSpringPlatformTransactionManager(testObject);
        testObjectPlatformTransactionManagerMap.put(testObject, platformTransactionManager);
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(createTransactionDefinition(testObject));
        testObjectTransactionStatusMap.put(testObject, transactionStatus);
    }


    /**
     * Commits the transaction. Uses the PlatformTransactionManager and transaction
     * that is associated with the given test object.
     *
     * @param testObject The test object, not null
     */
    public void commit(Object testObject) {
        if (!testObjectTransactionActiveMap.containsKey(testObject)) {
            throw new UnitilsException("Trying to commit, while no transaction is currently active");
        }
        TransactionStatus transactionStatus = testObjectTransactionStatusMap.get(testObject);
        if (testObjectTransactionActiveMap.get(testObject)) {
            logger.debug("Committing transaction");
            testObjectPlatformTransactionManagerMap.get(testObject).commit(transactionStatus);
        }
        testObjectTransactionActiveMap.remove(testObject);
        testObjectTransactionStatusMap.remove(testObject);
        testObjectPlatformTransactionManagerMap.remove(testObject);
    }

    /**
     * Rolls back the transaction. Uses the PlatformTransactionManager and transaction
     * that is associated with the given test object.
     *
     * @param testObject The test object, not null
     */
    public void rollback(Object testObject) {
        if (!testObjectTransactionActiveMap.containsKey(testObject)) {
            throw new UnitilsException("Trying to rollback, while no transaction is currently active");
        }
        TransactionStatus transactionStatus = testObjectTransactionStatusMap.get(testObject);
        if (testObjectTransactionActiveMap.get(testObject)) {
            logger.debug("Rolling back transaction");
            testObjectPlatformTransactionManagerMap.get(testObject).rollback(transactionStatus);
        }
        testObjectTransactionActiveMap.remove(testObject);
        testObjectTransactionStatusMap.remove(testObject);
        testObjectPlatformTransactionManagerMap.remove(testObject);
    }

    /**
     * Returns a <code>TransactionDefinition</code> object containing the
     * necessary transaction parameters. Simply returns a default
     * <code>DefaultTransactionDefinition</code> object with the 'propagation
     * required' attribute
     *
     * @param testObject The test object, not null
     * @return The default TransactionDefinition
     */
    protected TransactionDefinition createTransactionDefinition(Object testObject) {
        return new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
    }


    protected UnitilsTransactionManagementConfiguration getTransactionManagementConfiguration(Object testObject) {
        for (UnitilsTransactionManagementConfiguration transactionManagementConfiguration : transactionManagementConfigurations) {
            if (transactionManagementConfiguration.isApplicableFor(testObject)) {
                return transactionManagementConfiguration;
            }
        }
        throw new UnitilsException("No applicable transaction management configuration found for test " + testObject.getClass());
    }


    protected void setTransactionManagementConfigurations(Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurationsSet) {
        List<UnitilsTransactionManagementConfiguration> configurations = new ArrayList<UnitilsTransactionManagementConfiguration>();
        configurations.addAll(transactionManagementConfigurationsSet);
        Collections.sort(configurations, new Comparator<UnitilsTransactionManagementConfiguration>() {

            public int compare(UnitilsTransactionManagementConfiguration o1, UnitilsTransactionManagementConfiguration o2) {
                return o2.getPreference().compareTo(o1.getPreference());
            }

        });
        this.transactionManagementConfigurations = configurations;
    }

    /**
     * @return The Spring module
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }

}
