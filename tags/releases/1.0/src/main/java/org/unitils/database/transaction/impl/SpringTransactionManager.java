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

import static java.lang.reflect.Proxy.newProxyInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.TransactionManager;
import org.unitils.database.transaction.TransactionalDataSource;
import org.unitils.spring.SpringModule;

/**
 * Transaction manager that relies on Spring transaction management. When starting a Transaction, this transaction
 * manager tries to locate a configured <code>org.springframework.transaction.PlatformTransactionManager</code> bean
 * instance in the spring ApplicationContext configured in the {@link SpringModule} for this testObject. If no such bean
 * was configured for this test, a <code>org.springframework.jdbc.datasource.DataSourceTransactionManager</code>
 * instance is created.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SpringTransactionManager implements TransactionManager {

	/* The logger instance for this class */
	private static Log logger = LogFactory.getLog(SpringTransactionManager.class);

	/**
	 * ThreadLocal for holding the TransactionStatus as used by spring's transaction management
	 */
	protected ThreadLocal<TransactionStatus> transactionStatusHolder = new ThreadLocal<TransactionStatus>();


	/**
	 * Makes the given data source a transactional datasource. If no action needs to be performed, the given data source
	 * should be returned. <p/> This could for example be used to wrap the given data source for intercepting the
	 * creation of connections.
	 * 
	 * @param dataSource The original data source, not null
	 * @return The transactional data source, not null
	 */
	public TransactionalDataSource createTransactionalDataSource(DataSource dataSource) {
		SpringTransactionalDataSourceProxyInvocationHandler invocationHandler = new SpringTransactionalDataSourceProxyInvocationHandler(dataSource);
		TransactionalDataSource transactionalDataSource = (TransactionalDataSource) newProxyInstance(getClass().getClassLoader(), 
				new Class[] { TransactionalDataSource.class }, invocationHandler);
		invocationHandler.setProxiedDataSource(transactionalDataSource);
		return transactionalDataSource;
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
			PlatformTransactionManager springTransactionManager = getSpringTransactionManager(testObject);
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
		getSpringTransactionManager(testObject).commit(transactionStatus);
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
		getSpringTransactionManager(testObject).rollback(transactionStatus);
		transactionStatusHolder.remove();
	}
	
	
	public boolean isTransactionActive(Object testObject) {
		return transactionStatusHolder.get() != null;
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
	 * @return The <code>PlatformTransactionManager</code> that is configured in the spring application context
	 *         associated with the given testObject.
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
	 * Proxy for a DataSource that makes the DataSource Spring transactional. Makes sure that connection are retrieved
	 * and closed using Springs <code>DataSourceUtils</code>
	 */
	protected class SpringTransactionalDataSourceProxyInvocationHandler implements InvocationHandler {

		private DataSource wrappedDataSource;

		private DataSource proxiedDataSource;

		/**
		 * Creates a new instance that wraps the given <code>DataSource</code>
		 * 
		 * @param wrappedDataSource the data source, not null
		 */
		public SpringTransactionalDataSourceProxyInvocationHandler(DataSource wrappedDataSource) {
			this.wrappedDataSource = wrappedDataSource;
		}


		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getTransactionalConnection")) {
				return getTransactionalConnection();
			}
			try {
				return method.invoke(wrappedDataSource, args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}


		/**
		 * Retrieves a connection that can participate in a transaction.
		 * 
		 * @return The connection, not null
		 */
		protected Connection getTransactionalConnection() throws SQLException {
			Connection connection = doGetConnection();
			return (Connection) newProxyInstance(getClass().getClassLoader(), new Class[] { Connection.class }, new SpringConnectionProxyInvocationHandler(connection));
		}
		
		/**
		 * Sets the proxied DataSource, which is the dynamic proxy which makes use of this invocationhandler
		 * 
		 * @param proxiedDataSource The proxied DataSource. This DataSource must be the proxy that makes use of this invocationhandler!
		 */
		protected void setProxiedDataSource(DataSource proxiedDataSource) {
			this.proxiedDataSource = proxiedDataSource;
		}
		
		protected Connection doGetConnection() {
			if (isTransactionActive(getTestObject())) {
				PlatformTransactionManager springTransactionManager = getSpringTransactionManager(getTestObject());
				if (springTransactionManager instanceof ResourceTransactionManager) {
					Object resourceFactory = ((ResourceTransactionManager) springTransactionManager).getResourceFactory();
					for (SpringResourceTransactionManagerTransactionalConnectionHandler transactionalConnectionHandler : getSpringModule().getTransactionalConnectionHandlers()) {
						if (transactionalConnectionHandler.getResourceFactoryType().isAssignableFrom(resourceFactory.getClass())) {
							return transactionalConnectionHandler.getTransactionalConnection(resourceFactory);
						}
					}
					if (!(resourceFactory instanceof DataSource)) {
						throw new UnitilsException("Unitils doesn't support a spring PlatformTransactionManager of type " + springTransactionManager.getClass().getName());
					}
				}
			}
			return DataSourceUtils.getConnection(proxiedDataSource);
		}
		
		protected void doReleaseConnection(Connection connection) throws SQLException {
			if (isTransactionActive(getTestObject())) {
				PlatformTransactionManager springTransactionManager = getSpringTransactionManager(getTestObject());
				if (springTransactionManager instanceof ResourceTransactionManager) {
					Object resourceFactory = ((ResourceTransactionManager) springTransactionManager).getResourceFactory();
					for (SpringResourceTransactionManagerTransactionalConnectionHandler transactionalConnectionHandler : getSpringModule().getTransactionalConnectionHandlers()) {
						if (transactionalConnectionHandler.getResourceFactoryType().isAssignableFrom(resourceFactory.getClass())) {
							transactionalConnectionHandler.releaseTransactionalConnection(connection);
							return;
						}
					}
					// TODO Externalize to hibernate JPA module, hibernate specific
					if (resourceFactory instanceof EntityManagerFactory) {
						connection.close();
						return;
					}
					if (!(resourceFactory instanceof DataSource)) {
						throw new UnitilsException("Unitils doesn't support a spring PlatformTransactionManager of type " + springTransactionManager.getClass().getName());
					}
				}
			}
			DataSourceUtils.releaseConnection(connection, proxiedDataSource);
		}
		
		protected Object getTestObject() {
			return Unitils.getInstance().getTestContext().getTestObject();
		}

		/**
		 * Invocation handler that can be used to create Connection proxy that intercepts the call to the close()
		 * method, and makes sure {@link DataSourceUtils#doReleaseConnection(Connection, DataSource)} is called instead
		 * of targetConnection.close().
		 */
		private class SpringConnectionProxyInvocationHandler implements InvocationHandler {

			private Connection wrappedConnection;


			/**
			 * Constructs a new instance that delegates to the given connection
			 * 
			 * @param wrappedConnection The connection to wrap, not null
			 */
			protected SpringConnectionProxyInvocationHandler(Connection wrappedConnection) {
				this.wrappedConnection = wrappedConnection;
			}


			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.getName().equals("close")) {
					doReleaseConnection(wrappedConnection);
					return null;
				}
				try {
					return method.invoke(wrappedConnection, args);
				} catch (InvocationTargetException e) {
					throw e.getTargetException();
				}
			}

		}
	}


}
