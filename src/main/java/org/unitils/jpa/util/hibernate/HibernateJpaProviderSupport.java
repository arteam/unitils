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
package org.unitils.jpa.util.hibernate;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.ejb.Ejb3Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.unitils.core.UnitilsException;
import org.unitils.hibernate.util.HibernateAssert;
import org.unitils.jpa.util.JpaProviderSupport;

/**
 * Implementation of {@link JpaProviderSupport} for hibernate JPA
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateJpaProviderSupport implements JpaProviderSupport {

	
	/**
     * Checks if the mapping of the JPA entities with the database is still correct.
     *
     * @param testObject The test instance, not null
     */
	@Override
	public void assertMappingWithDatabaseConsistent(EntityManager entityManager, Object configurationObject) {
		Ejb3Configuration configuration = (Ejb3Configuration) configurationObject;
        Dialect databaseDialect = getHibernateDatabaseDialect(configuration);

        HibernateAssert.assertMappingWithDatabaseConsistent(configuration.getHibernateConfiguration(), (Session) entityManager.getDelegate(), databaseDialect);
	}
	
	
	/**
     * Gets the database dialect from the Hibernate <code>Ejb3Configuration</code.
     *
     * @param configuration The hibernate config, not null
     * @return the database Dialect, not null
     */
    protected Dialect getHibernateDatabaseDialect(Ejb3Configuration configuration) {
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
    
    
    @Override
    @SuppressWarnings("deprecation")
	public Connection getJdbcConnection(EntityManager entityManager) {
		Session session = (Session) entityManager.getDelegate();
		return session.connection();
	}
    
    
    @Override
    public JpaVendorAdapter getSpringJpaVendorAdaptor() {
    	return new HibernateJpaVendorAdapter();
    }
    
    
    @Override
	public PersistenceProvider getPersistenceProvider() {
		return new UnitilsHibernatePersistenceProvider();
	}


	@Override
	public Object getProviderSpecificConfigurationObject(
			PersistenceProvider persistenceProvider) {
		if (!(persistenceProvider instanceof UnitilsHibernatePersistenceProvider)) {
			throw new UnitilsException("Make sure that the persistence provider that is used is an instance of " + UnitilsHibernatePersistenceProvider.class.getSimpleName());
		}
		UnitilsHibernatePersistenceProvider hibernatePersistenceProvider = (UnitilsHibernatePersistenceProvider) persistenceProvider;
		Ejb3Configuration configuration = hibernatePersistenceProvider.getHibernateConfiguration();
		return configuration;
	}
	
}
