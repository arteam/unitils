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

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.unitils.database.transaction.impl.SpringResourceTransactionManagerTransactionalConnectionHandler;

/**
 * Handles retrieval and release of <code>java.sql.Connection</code>s, given the fact that we
 * are dealing with a SessionFactory for resource management.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateTransactionManagerTransactionalConnectionHandler
		implements
		SpringResourceTransactionManagerTransactionalConnectionHandler {

	/**
	 * @return The resource factory type, i.e. SessionFactory
	 */
	public Class<?> getResourceFactoryType() {
		return SessionFactory.class;
	}

	/**
	 * @return The Connection that is in use by the current hibernate Session
	 */
	public Connection getTransactionalConnection(Object resourceFactory) {
		return SessionFactoryUtils.getSession((SessionFactory) resourceFactory, true).connection();
	}

	/**
	 * Releases the Connection used by the current hibernate Session.
	 */
	public void releaseTransactionalConnection(Connection conn) throws SQLException {
		conn.close();
	}

}
