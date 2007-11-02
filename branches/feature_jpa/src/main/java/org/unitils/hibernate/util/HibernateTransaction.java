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
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateTransaction implements Transaction {

	boolean committed;
	
	boolean rollbacked;
	
	public void begin() throws HibernateException {
		getUnitilsTransactionManager().startTransaction(getCurrentTestObject());
		committed = false;
		rollbacked = false;
	}

	public void commit() throws HibernateException {
		getUnitilsTransactionManager().commit(getCurrentTestObject());
		committed = true;
	}

	public boolean isActive() throws HibernateException {
		return getUnitilsTransactionManager().isActive(getCurrentTestObject());
	}

	public void registerSynchronization(Synchronization synchronization)
			throws HibernateException {
		
		// Not supported
	}

	public void rollback() throws HibernateException {
		getUnitilsTransactionManager().rollback(getCurrentTestObject());
		rollbacked = true;
	}

	public void setTimeout(int seconds) {
		
		// Not supported
	}

	public boolean wasCommitted() throws HibernateException {
		return committed;
	}

	public boolean wasRolledBack() throws HibernateException {
		return rollbacked;
	}
	
	protected TransactionManager getUnitilsTransactionManager() {
		return getDatabaseModule().getTransactionManager();
	}
	
	protected Object getCurrentTestObject() {
		return Unitils.getInstance().getTestContext().getTestObject();
	}
	
	protected DatabaseModule getDatabaseModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
	}

}
