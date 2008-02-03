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
package org.unitils.jpa.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.unitils.database.transaction.impl.SpringResourceTransactionManagerTransactionalConnectionHandler;

public class JtaTransactionManagerTransactionalConnectionHandler implements
		SpringResourceTransactionManagerTransactionalConnectionHandler {

	
	private JpaPersistenceProvider jpaPersistenceProvider;
	
	
	public JtaTransactionManagerTransactionalConnectionHandler(JpaPersistenceProvider jpaPersistenceProvider) {
		this.jpaPersistenceProvider = jpaPersistenceProvider;
	}

	public Class<?> getResourceFactoryType() {
		return EntityManagerFactory.class;
	}

	public Connection getTransactionalConnection(Object resourceFactory) {
		EntityManager entityManager = getSpringManagedTransactionalEntityManager((EntityManagerFactory) resourceFactory);
		if (entityManager == null) {
			return null;
		}
		return jpaPersistenceProvider.getJdbcConnection(entityManager);
	}
	

	public void releaseTransactionalConnection(Connection conn)
			throws SQLException {
	}
	
	
	private EntityManager getSpringManagedTransactionalEntityManager(EntityManagerFactory resourceFactory) {
		return EntityManagerFactoryUtils.getTransactionalEntityManager(resourceFactory);
	}

}
