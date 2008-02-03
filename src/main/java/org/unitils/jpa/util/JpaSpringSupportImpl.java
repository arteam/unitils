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

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.unitils.core.Unitils;
import org.unitils.spring.SpringModule;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class JpaSpringSupportImpl implements JpaSpringSupport {

	
	public JpaSpringSupportImpl(JpaPersistenceProvider jpaPersistenceProvider) {
		// Make sure Spring is in the classpath
		AbstractEntityManagerFactoryBean.class.getName();
		// TODO Needed to verify if a JtaTransactionManager is used in spring first?
        getSpringModule().registerSpringResourceTransactionManagerTransactionalConnectionHandler(new JtaTransactionManagerTransactionalConnectionHandler(jpaPersistenceProvider));
	}
	
	
	public boolean isEntityManagerFactoryConfiguredInSpring(Object testObject) {
		return getEntityManagerFactoryBean(testObject) != null;
	}
	
	
	public EntityManagerFactory getEntityManagerFactory(Object testObject) {
		AbstractEntityManagerFactoryBean entityManagerFactoryBean = getEntityManagerFactoryBean(testObject);
        if (entityManagerFactoryBean == null) {
            return null;
        }
        
        return (EntityManagerFactory) entityManagerFactoryBean.getObject();
	}

	
	public Object getConfigurationObject(Object testObject) {
		throw new UnsupportedOperationException("Getting the configuration when JPA is configured using spring is not yet supported in unitils");
	}
	
	
	public EntityManager getActiveEntityManager(Object testObject) {
		EntityManagerFactory entityManagerFactory = getEntityManagerFactory(testObject);
    	if (entityManagerFactory == null) {
    		return null;
    	}
    	
    	EntityManagerHolder entityManagerHolder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(entityManagerFactory);
    	if (entityManagerHolder == null) {
    		return null;
    	}
    	return entityManagerHolder.getEntityManager();
	}

	
	/**
     * @param testObject
     * @return Instance of {@link LocalSessionFactoryBean} that wraps the configuration of hibernate in spring
     */
    protected AbstractEntityManagerFactoryBean getEntityManagerFactoryBean(Object testObject) {
        if (!getSpringModule().isApplicationContextConfiguredFor(testObject)) {
            return null;
        }
        Collection<?> entityManagerFactoryBeans = getSpringModule().getApplicationContext(testObject).getBeansOfType(AbstractEntityManagerFactoryBean.class).values();
        if (entityManagerFactoryBeans.size() == 0) {
            return null;
        }
        return (AbstractEntityManagerFactoryBean) entityManagerFactoryBeans.iterator().next();
    }
	
	
	/**
     * @return The Spring module, not null
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }

}
