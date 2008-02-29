/*
 * Copyright 2006-2007,  Unitils.org
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

import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModule;
import org.unitils.database.transaction.UnitilsTransactionManager;
import org.unitils.database.util.spring.DatabaseSpringSupport;
import org.unitils.spring.SpringModule;

/**
 * Implements transactions for unit tests, by delegating to a spring <code>PlatformTransactionManager</code>.
 * The concrete implementation of <code>PlatformTransactionManager</code> that is used depends on the test class. If a 
 * custom <code>PlatformTransactionManager</code> was configured in a spring <code>ApplicationContext</code>, this one is
 * used. If not, a suitable subclass of <code>PlatformTransactionManager</code> is created, depending on the 
 * configuration of a test. E.g. if some ORM persistence unit was configured on the test, a <code>PlatformTransactionManager</code>
 * that can offer transactional behavior for such a persistence unit is used. If no such configuration is found, a 
 * <code>DataSourceTransactionManager</code> is used.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultUnitilsTransactionManager implements UnitilsTransactionManager {

	/** The logger instance for this class */
	private static Log logger = LogFactory.getLog(DefaultUnitilsTransactionManager.class);

	/**
	 * ThreadLocal for holding the TransactionStatus that keeps track of the current test's transaction status
	 */
	protected ThreadLocal<TransactionStatus> transactionStatusHolder = new ThreadLocal<TransactionStatus>();
	
	/**
	 * ThreadLocal for holding the PlatformTransactionManager that is used by the current test
	 */
	protected ThreadLocal<PlatformTransactionManager> springPlatformTransactionManager = new ThreadLocal<PlatformTransactionManager>();
	
	/**
	 * Provides access to an optionally custom <code>PlatformTransactionManager</code>, configured in a spring <code>ApplicationContext</code>.
	 * Null if the spring module is not enabled
	 */
	protected DatabaseSpringSupport databaseSpringSupport;
	
	/**
	 * Set of possible providers of a spring <code>PlatformTransactionManager</code>, not null
	 */
	protected Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations;
	
	@Override
	public void init(Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations, DatabaseSpringSupport databaseSpringSupport) {
		this.transactionManagementConfigurations = transactionManagementConfigurations;
		this.databaseSpringSupport = databaseSpringSupport;
	}


	/**
	 * Returns the unitils datasource, wrapped in a spring <code>TransactionAwareDataSourceProxy</code>
	 */
	@Override
	public DataSource getTransactionalDataSource(Object testObject) {
		return new TransactionAwareDataSourceProxy(getDataSource());
	}


	/**
	 * @param testObject The test object, not null
	 * @return True if a custom <code>PlatformTransactionManager</code> has been configured in the spring <code>ApplicationContext</code>
	 * for this test class
	 */
	protected boolean isCustomSpringTransactionManagerConfigured(Object testObject) {
		return databaseSpringSupport != null && databaseSpringSupport.isTransactionManagerConfiguredInSpring(testObject);
	}


	/**
	 * Starts the transaction. Starts a transaction on the PlatformTransactionManager that is configured for the given testObject.
	 * 
	 * @param testObject The test object, not null
	 */
	public void startTransaction(Object testObject) {
		logger.debug("Starting transaction");
		springPlatformTransactionManager.set(getSpringPlatformTransactionManager(testObject));
		TransactionStatus transactionStatus = springPlatformTransactionManager.get().getTransaction(createTransactionDefinition(testObject));
		transactionStatusHolder.set(transactionStatus);
	}


	/**
	 * Commits the transaction. Will commit on the PlatformTransactionManager that is configured for the given testObject, for 
	 * the transaction associated with the current thread.
	 * 
	 * @param testObject The test object, not null
	 */
	public void commit(Object testObject) {
		TransactionStatus transactionStatus = transactionStatusHolder.get();
		if (transactionStatus == null) {
			throw new UnitilsException("Trying to commit, while no transaction is currently active");
		}
		logger.debug("Commiting transaction");
		springPlatformTransactionManager.get().commit(transactionStatus);
		transactionStatusHolder.remove();
		springPlatformTransactionManager.remove();
	}


	/**
	 * Rolls back the transaction. Will rollbackon the PlatformTransactionManager that is configured for the given testObject, for 
	 * the transaction associated with the current thread.
	 * 
	 * @param testObject The test object, not null
	 */
	public void rollback(Object testObject) {
		TransactionStatus transactionStatus = transactionStatusHolder.get();
		if (transactionStatus == null) {
			throw new UnitilsException("Trying to rollback, while no transaction is currently active");
		}
		logger.debug("Rolling back transaction");
		springPlatformTransactionManager.get().rollback(transactionStatus);
		transactionStatusHolder.remove();
		springPlatformTransactionManager.remove();
	}
	
	
	/**
	 * Returns a <code>TransactionDefinition</code> object containing the necessary transaction parameters. Simply
	 * returns a default <code>DefaultTransactionDefinition</code> object with the 'propagation required' attribute
	 * 
	 * @param testObject The test object, not null
	 * @return The default TransactionDefinition
	 */
	protected TransactionDefinition createTransactionDefinition(Object testObject) {
		return new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
	}


	/**
	 * @param testObject The test object, not null
	 * @return The <code>PlatformTransactionManager</code> that is configured in the spring application context
	 *         associated with the given testObject.
	 * @throws UnitilsException If no <code>PlatformTransactionManager</code> was configured for the given test object
	 */
	protected PlatformTransactionManager getSpringPlatformTransactionManager(Object testObject) {
		if (isCustomSpringTransactionManagerConfigured(testObject)) {
			return databaseSpringSupport.getPlatformTransactionManager(testObject);
		}
		return createPlatformTransactionManager(testObject);
	}


	/**
	 * A suitable implementation of <code>PlatformTransactionManager</code> is created, depending on the 
	 * configuration of a test. E.g. if some ORM persistence unit was configured on the test, a <code>PlatformTransactionManager</code>
	 * that can offer transactional behavior for such a persistence unit is used. If no such configuration is found, a 
	 * <code>DataSourceTransactionManager</code> is returned.
	 * 
	 * @param testObject The test object, not null
	 * @return A suitable implementation of <code>PlatformTransactionManager</code>
	 */
	protected PlatformTransactionManager createPlatformTransactionManager(Object testObject) {
		UnitilsTransactionManagementConfiguration applicableTransactionManagementConfiguration = null;
		for (UnitilsTransactionManagementConfiguration transactionManagementConfiguration : transactionManagementConfigurations) {
			if (transactionManagementConfiguration.isApplicableFor(testObject)) {
				if (applicableTransactionManagementConfiguration != null) {
					throw new UnitilsException("More than one transaction management configuration is applicable for " + testObject.getClass().getSimpleName() +
							". This means that, for example, you configured both a hibernate SessionFactory and a JPA EntityManagerFactory for this class. " +
							"This is not supported in unitils.");
				}
				applicableTransactionManagementConfiguration = transactionManagementConfiguration;
			}
		}
		if (applicableTransactionManagementConfiguration != null) {
			return applicableTransactionManagementConfiguration.getSpringPlatformTransactionManager(testObject);
		}
		
		return createDataSourceTransactionManager(testObject);
	}

	
	/**
	 * @param testObject The test object, not null
	 * @return An instance of <code>DataSourceTransactionManager</code> that implements transactions for the test datasource
	 */
	protected DataSourceTransactionManager createDataSourceTransactionManager(Object testObject) {
		DataSource dataSource = getDataSource();
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
		return dataSourceTransactionManager;
	}
	
	
	/**
	 * @return The test datasource
	 */
	protected DataSource getDataSource() {
		return getDatabaseModule().getDataSource();
	}
	
	
	/**
	 * @return The database module
	 */
	protected DatabaseModule getDatabaseModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
	}


	/**
	 * @return The Spring module
	 */
	protected SpringModule getSpringModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
	}
	
}
