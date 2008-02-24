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
package org.unitils.hibernate.util;

import javax.transaction.Synchronization;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.database.transaction.TransactionManager;

/**
 * Implementation of a hibernate <code>org.hibernate.Transaction</code>. Couples calls to the 
 * hibernate transaction API to the transaction system used in Unitils.
 * <p>
 * This means that, if a transaction is started using Hibernate's transaction API, a transaction
 * is started on the Unitils transaction manager under the hoods, and that Hibernate obtains 
 * Connections that are unit test transaction scoped.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateTransaction implements Transaction {

	/**
	 * Is the current transaction was committed?
	 */
	boolean committed;
	
	/**
	 * Is the current transaction was rollbacked?
	 */
	boolean rollbacked;
	
	
	/**
	 * Starts a Unitils transaction
	 */
	public void begin() throws HibernateException {
		getUnitilsTransactionManager().startTransaction(getCurrentTestObject());
		committed = false;
		rollbacked = false;
	}

	
	/**
	 * Commits a Unitils transaction
	 */
	public void commit() throws HibernateException {
		getUnitilsTransactionManager().commit(getCurrentTestObject());
		committed = true;
	}

	
	/**
	 * Verifies whether a transaction is currently active
	 */
	public boolean isActive() throws HibernateException {
		return getUnitilsTransactionManager().isTransactionActive(getCurrentTestObject());
	}

	
	/**
	 * Has no effect in Unitils, not supported
	 */
	public void registerSynchronization(Synchronization synchronization)
			throws HibernateException {
		
		// Not supported
	}

	
	/**
	 * Rollbacks the current Unitils transaction
	 */
	public void rollback() throws HibernateException {
		getUnitilsTransactionManager().rollback(getCurrentTestObject());
		rollbacked = true;
	}

	
	/**
	 * Has no effect in Unitils, not supported
	 */
	public void setTimeout(int seconds) {
		
		// Not supported
	}

	
	/**
	 * @return If the current transaction was committed
	 */
	public boolean wasCommitted() throws HibernateException {
		return committed;
	}

	
	/**
	 * @return @return If the current transaction was rollbacked
	 */
	public boolean wasRolledBack() throws HibernateException {
		return rollbacked;
	}
	
	
	/**
	 * @return The underlying Unitils transaction manager
	 */
	protected TransactionManager getUnitilsTransactionManager() {
		return getDatabaseModule().getTransactionManager();
	}
	
	
	/**
	 * @return The current test objects
	 */
	protected Object getCurrentTestObject() {
		return Unitils.getInstance().getTestContext().getTestObject();
	}
	
	
	/**
	 * @return The {@link DatabaseModule}
	 */
	protected DatabaseModule getDatabaseModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
	}

}
