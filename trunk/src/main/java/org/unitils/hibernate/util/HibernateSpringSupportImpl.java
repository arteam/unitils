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

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.unitils.core.Unitils;
import org.unitils.hibernate.HibernateModule;
import org.unitils.spring.SpringModule;

/**
 * A support class containing Hibernate and {@link HibernateModule} related actions for the {@link SpringModule}.
 * <p/>
 * This support class is only loaded if both the {@link HibernateModule} and {@link SpringModule} are loaded.
 * By encapsulating these operations, we remove the strong dependency to spring and the {@link SpringModule} from
 * the {@link HibernateModule}. This way, the {@link HibernateModule} will still function if spring is not used.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateSpringSupportImpl implements HibernateSpringSupport {


    /**
     * Creates a new instance. It attempts to load a class of spring's hibernate support, to make
     * sure the necessary libraries are in the classpath
     */
    public HibernateSpringSupportImpl() {
        // Make sure Spring is in the classpath
        LocalSessionFactoryBean.class.getName();
        // TODO Needed to verify if a HibernateTransactionManager is used in spring first?
        getSpringModule().registerSpringResourceTransactionManagerTransactionalConnectionHandler(new HibernateTransactionManagerTransactionalConnectionHandler());
    }
    

    /**
     * @return true if a <code>LocalSessionFactoryBean</code> can be found in the applicationcontext
     * configured for the given testobject
     */
    public boolean isSessionFactoryConfiguredInSpring(Object testObject) {
        return getSessionFactoryBean(testObject) != null;
    }


    /**
     * Returns the hibernate <code>SessionFactory</code> that was configured in spring for the given testObject, if any
     *
     * @param testObject The test instance, not null
     * @return The <code>SessionFactory</code> configured in spring for the given testObject, null if no such
     *         <code>SessionFactory</code> was configured.
     */
    public SessionFactory getSessionFactory(Object testObject) {
        LocalSessionFactoryBean sessionFactoryBean = getSessionFactoryBean(testObject);
        if (sessionFactoryBean == null) {
            return null;
        }
        
        return (SessionFactory) sessionFactoryBean.getObject();
    }


    /**
     * Returns the hibernate <code>Configuration</code> that was configured in spring for the given testObject, if any
     *
     * @param testObject The test instance, not null
     * @return The <code>Configuration</code> configured in spring for the given testObject, null if no such
     *         <code>Configuration</code> was configured.
     */
    public Configuration getConfiguration(Object testObject) {
        LocalSessionFactoryBean sessionFactoryBean = getSessionFactoryBean(testObject);
        if (sessionFactoryBean == null) {
            return null;
        }
        
        return sessionFactoryBean.getConfiguration();
    }
    
    
    /**
	 * @param testObject The test instance, not null
     * @return The currently active hibernate session, managed by spring
     */
    public Session getActiveSession(Object testObject) {
    	SessionFactory sessionFactory = getSessionFactory(testObject);
    	if (sessionFactory == null) {
    		return null;
    	}
    	
    	SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
    	if (sessionHolder == null) {
    		return null;
    	}
    	// TODO Make sure we obtain all sessions currently active
    	return sessionHolder.getAnySession();
    }


    /**
     * Ensures that the spring application context is loaded. This could be not the case since the application context
     * is lazily loaded
     *
     * @param testObject The test instance, not null
     */
    protected ApplicationContext getApplicationContext(Object testObject) {
        return getSpringModule().getApplicationContext(testObject);
    }
    
    
    /**
     * @param testObject
     * @return Instance of {@link LocalSessionFactoryBean} that wraps the configuration of hibernate in spring
     */
    protected LocalSessionFactoryBean getSessionFactoryBean(Object testObject) {
        if (!getSpringModule().isApplicationContextConfiguredFor(testObject)) {
            return null;
        }
        Collection<?> sessionFactoryBeans = getSpringModule().getApplicationContext(testObject).getBeansOfType(LocalSessionFactoryBean.class).values();
        if (sessionFactoryBeans.size() == 0) {
            return null;
        }
        return (LocalSessionFactoryBean) sessionFactoryBeans.iterator().next();
    }


    /**
     * @return The Spring module, not null
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }
}
