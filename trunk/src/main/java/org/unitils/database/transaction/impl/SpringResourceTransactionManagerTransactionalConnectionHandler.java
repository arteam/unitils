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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Specifies contract for implementations that retrieve and release a <code>java.sql.Connection</code> that takes
 * part in the current transaction, given the fact that a spring <code>ResourceTransactionManager</code> is used 
 * for test transaction management.
 *  
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface SpringResourceTransactionManagerTransactionalConnectionHandler {

	/**
	 * @return The type of the resource factory that this implementation deals with
	 */
	Class<?> getResourceFactoryType();
	
	/**
	 * @param resourceFactory
	 * @return A connection that takes part in the current transaction
	 */
	Connection getTransactionalConnection(Object resourceFactory);
	
	/**
	 * Returns the given Connection to the connection pool.
	 * Precondition: The given Connection was obtained by invoking {@link #getTransactionalConnection(Object)} on
	 * the same instance.
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	void releaseTransactionalConnection(Connection conn) throws SQLException;
	
}
