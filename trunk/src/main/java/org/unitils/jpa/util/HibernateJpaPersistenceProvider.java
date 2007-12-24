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

import static org.apache.commons.lang.StringUtils.isEmpty;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.ejb.Ejb3Configuration;
import org.unitils.core.UnitilsException;
import org.unitils.hibernate.util.HibernateAssert;

public class HibernateJpaPersistenceProvider implements JpaPersistenceProvider {

	
	/**
     * Checks if the mapping of the JPA entities with the database is still correct.
     *
     * @param testObject The test instance, not null
     */
	public void assertMappingWithDatabaseConsistent(Object testObject, EntityManagerFactoryManager entityManagerFactoryManager) {
		HibernateEntityManagerFactoryManager hibernateEntityManagerFactoryManager = (HibernateEntityManagerFactoryManager) entityManagerFactoryManager;
		Ejb3Configuration configuration = hibernateEntityManagerFactoryManager.getConfiguration(testObject);
        EntityManager entityManager = hibernateEntityManagerFactoryManager.getEntityManagerFactory(testObject).createEntityManager();
        Dialect databaseDialect = getDatabaseDialect(configuration);

        HibernateAssert.assertMappingWithDatabaseConsistent(configuration.getHibernateConfiguration(), (Session) entityManager.getDelegate(), databaseDialect);
	}
	
	
	/**
     * Gets the database dialect from the Hibernate <code>Configuration</code.
     *
     * @param configuration The hibernate config, not null
     * @return the database Dialect, not null
     */
    protected Dialect getDatabaseDialect(Ejb3Configuration configuration) {
        String dialectClassName = configuration.getProperties().getProperty("hibernate.dialect");
        if (isEmpty(dialectClassName)) {
            throw new UnitilsException("Property hibernate.dialect not specified");
        }
        try {
            return (Dialect) Class.forName(dialectClassName).newInstance();
        } catch (Exception e) {
            throw new UnitilsException("Could not instantiate dialect class " + dialectClassName, e);
        }
    }
	
	
}
