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

import java.util.Properties;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.jdbc.JDBCContext;
import org.hibernate.transaction.TransactionFactory;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateTransactionFactory implements TransactionFactory {

	public boolean areCallbacksLocalToHibernateTransactions() {
		return true;
	}

	public void configure(Properties props) throws HibernateException {
	}

	public Transaction createTransaction(JDBCContext jdbcContext,
			Context context) throws HibernateException {
		return new HibernateTransaction();
	}

	public ConnectionReleaseMode getDefaultReleaseMode() {
		return ConnectionReleaseMode.AFTER_TRANSACTION;
	}

	public boolean isTransactionInProgress(JDBCContext jdbcContext,
			Context transactionContext, Transaction transaction) {
		return getDatabaseModule().getTransactionManager().isActive(getCurrentTestObject());
	}

	public boolean isTransactionManagerRequired() {
		return false;
	}
	
	protected Object getCurrentTestObject() {
		return Unitils.getInstance().getTestContext().getTestObject();
	}
	
	protected DatabaseModule getDatabaseModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
	}

}
