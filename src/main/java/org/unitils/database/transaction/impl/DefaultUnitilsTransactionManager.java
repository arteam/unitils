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

import java.util.Properties;
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
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultUnitilsTransactionManager implements UnitilsTransactionManager {

	
	/* The logger instance for this class */
	private static Log logger = LogFactory.getLog(DefaultUnitilsTransactionManager.class);

	/**
	 * ThreadLocal for holding the TransactionStatus as used by spring's transaction management
	 */
	protected ThreadLocal<TransactionStatus> transactionStatusHolder = new ThreadLocal<TransactionStatus>();
	
	protected PlatformTransactionManager springTransactionManager;
	
	protected DatabaseSpringSupport databaseSpringSupport;
	
	protected Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations;
	
	public void init(Properties configuration, Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations, DatabaseSpringSupport databaseSpringSupport) {
		this.transactionManagementConfigurations = transactionManagementConfigurations;
		this.databaseSpringSupport = databaseSpringSupport;
	}


	/**
	 * Makes the given data source a transactional datasource. If no action needs to be performed, the given data source
	 * should be returned. <p/> This could for example be used to wrap the given data source for intercepting the
	 * creation of connections.
	 * 
	 * @param dataSource The original data source, not null
	 * @return The transactional data source, not null
	 */
	public DataSource createTransactionalDataSource(DataSource dataSource, Object testObject) {
		if (isCustomSpringTransactionManagerConfigured(testObject)) {
			return dataSource;
		}
		return new TransactionAwareDataSourceProxy(dataSource);
	}


	/**
	 * @param testObject 
	 * 
	 * @return
	 */
	protected boolean isCustomSpringTransactionManagerConfigured(Object testObject) {
		return databaseSpringSupport != null && databaseSpringSupport.isTransactionManagerConfiguredInSpring(testObject);
	}


	/**
	 * Starts the transaction. Will start a transaction on the PlatformTransactionManager that is configured in the
	 * spring application context associated with the given testObject.
	 * 
	 * @param testObject The test object, not null
	 */
	public void startTransaction(Object testObject) {
		try {
			logger.debug("Starting transaction");
			springTransactionManager = getSpringTransactionManager(testObject);
			TransactionStatus transactionStatus = springTransactionManager.getTransaction(createTransactionDefinition(testObject));
			transactionStatusHolder.set(transactionStatus);

		} catch (Throwable t) {
			throw new UnitilsException("Unable to start transaction. Could not retrieve PlatformTransactionManager from the Spring application context. " + "Make sure either to configure one, or use another Unitils transaction manager. (e.g. SimpleTransactionManager, by setting the " + "property 'transactionManager.type' to 'simple')", t);
		}
	}


	/**
	 * Commits the transaction. Will commit on the PlatformTransactionManager that is configured in the spring
	 * application context associated with the given testObject.
	 * 
	 * @param testObject The test object, not null
	 */
	public void commit(Object testObject) {
		TransactionStatus transactionStatus = transactionStatusHolder.get();
		if (transactionStatus == null) {
			throw new UnitilsException("Trying to commit, while no transaction is currently active");
		}
		logger.debug("Commiting transaction");
		springTransactionManager.commit(transactionStatus);
		transactionStatusHolder.remove();
	}


	/**
	 * Rolls back the transaction. Will rollback on the PlatformTransactionManager that is configured in the spring
	 * application context associated with the given testObject.
	 * 
	 * @param testObject The test object, not null
	 */
	public void rollback(Object testObject) {
		TransactionStatus transactionStatus = transactionStatusHolder.get();
		if (transactionStatus == null) {
			throw new UnitilsException("Trying to rollback, while no transaction is currently active");
		}
		logger.debug("Rolling back transaction");
		springTransactionManager.rollback(transactionStatus);
		transactionStatusHolder.remove();
	}
	
	
	public boolean isTransactionActive(Object testObject) {
		return transactionStatusHolder.get() != null;
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
	protected PlatformTransactionManager getSpringTransactionManager(Object testObject) {
		if (isCustomSpringTransactionManagerConfigured(testObject)) {
			return databaseSpringSupport.getPlatformTransactionManager(testObject);
		}
		return createPlatformTransactionManager(testObject);
	}


	public PlatformTransactionManager createPlatformTransactionManager(Object testObject) {
		for (UnitilsTransactionManagementConfiguration transactionManagementConfiguration : transactionManagementConfigurations) {
			if (transactionManagementConfiguration.isApplicableFor(testObject)) {
				return transactionManagementConfiguration.getSpringPlatformTransactionManager(testObject);
			}
		}
		
		return createDataSourceTransactionManager(testObject);
	}

	
	protected PlatformTransactionManager createDataSourceTransactionManager(Object testObject) {
		DataSource dataSource = getDataSource();
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
		return dataSourceTransactionManager;
	}
	
	
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
