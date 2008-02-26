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

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.springframework.orm.jpa.JpaVendorAdapter;
import org.unitils.jpa.JpaModule;

/**
 * Defines the contract for implementations that implement any provider specific operations that unitils needs 
 * to implement the {@link JpaModule}s functionality.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface JpaProviderSupport {
	
	
	/**
	 * Checks if the mapping of the JPA entities with the database is still correct for the given 
	 * <code>EntityManager</code> and provider specific configuration object
	 * 
	 * @param entityManager Currently active <code>EntityManager</code>, not null
	 * @param configurationObject Provider specific configuration object, not null
	 */
	void assertMappingWithDatabaseConsistent(EntityManager entityManager, Object configurationObject);

	
	/**
	 * @param entityManager Currently active <code>EntityManager</code>, not null
	 * @return The JDBC connection that is used by the currently active <code>EntityManager</code>. Actions
	 * performed on this connection are performed in the same transaction as the one to which the given
	 * <code>EntityManager</code> is associated, not null
	 */
	Connection getJdbcConnection(EntityManager entityManager);
	
	
	/**
	 * @return Implementation of spring's <code>JpaVendorAdapter</code> interface for this persistence provider, not null
	 */
	JpaVendorAdapter getSpringJpaVendorAdaptor();
	
	
	/**
	 * @return An instance of the JPA <code>PersistenceProvider</code> for this persistence provider, not null
	 */
	PersistenceProvider getPersistenceProvider();

	
	/**
	 * @param persistenceProvider The JPA <code>PersistenceProvider</code> that was used for creating the 
	 * <code>EntityManagerFactory</code>, not null
	 * 
	 * @return The provider specific configuration object that was used for configuring this 
	 * <code>EntityManagerFactory</code>, not null
	 */
	Object getProviderSpecificConfigurationObject(PersistenceProvider persistenceProvider);
}
