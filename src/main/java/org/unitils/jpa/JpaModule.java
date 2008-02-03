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
package org.unitils.jpa;

import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.PropertyUtils.getString;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;
import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.util.Flushable;
import org.unitils.hibernate.HibernateModule;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import org.unitils.hibernate.util.SessionInterceptingSessionFactory;
import org.unitils.jpa.annotation.JpaEntityManagerFactory;
import org.unitils.jpa.util.EntityManagerFactoryManager;
import org.unitils.jpa.util.EntityManagerInterceptingEntityManagerFactory;
import org.unitils.jpa.util.JpaPersistenceProvider;
import org.unitils.jpa.util.JpaSpringSupport;
import org.unitils.util.CollectionUtils;
import org.unitils.util.ConfigUtils;
import org.unitils.util.PropertyUtils;

public class JpaModule implements Module, Flushable {

	 /* Property key of the class name of the hibernate configuration */
    public static final String PROPKEY_PERSISTENCE_PROVIDER = "JpaModule.persistenceProvider";

    /* Key of property that indicates whether after each test, hibernate sessions that are still open
     * need to be closed */
    public static final String PROPKEY_AUTOCLOSEENTITYMANAGERSAFTERTEST_ENABLED = "JpaModule.autoCloseEntityManagersAfterTest.enabled";
	
    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(HibernateModule.class);
    
    protected JpaPersistenceProvider jpaPersistenceProvider;
    
    /**
     * Manager for storing and creating entity manager factories
     */
    protected EntityManagerFactoryManager entityManagerFactoryManager;

    /**
     * The jpa spring support, null if the spring module is not enabled
     */
    protected JpaSpringSupport jpaSpringSupport;
    
    /**
     * boolean indicating if after each test, entity managers that are still open need to be closed
     */
    protected boolean autoCloseEntityManagersAfterTest;
    
    
    /**
     * Initializes the module.
     *
     * @param configuration The Unitils configuration, not null
     */
    public void init(Properties configuration) {
        String persistenceProviderImplClassName = getString(PROPKEY_PERSISTENCE_PROVIDER, configuration);
        jpaPersistenceProvider = ConfigUtils.getConfiguredInstance(JpaPersistenceProvider.class, configuration, persistenceProviderImplClassName);

        entityManagerFactoryManager = ConfigUtils.getConfiguredInstance(EntityManagerFactoryManager.class, configuration, persistenceProviderImplClassName);
        
        autoCloseEntityManagersAfterTest = PropertyUtils.getBoolean(PROPKEY_AUTOCLOSEENTITYMANAGERSAFTERTEST_ENABLED, configuration);
    }
    

    /**
     * No after initialization needed for this module
     */
    public void afterInit() {
    	initJpaSpringSupport();
	}

	/**
     * Checks if the mapping of the managed objects with the database is still correct.
     *
     * @param testObject The test instance, not null
     */
    public void assertMappingWithDatabaseConsistent(Object testObject) {
    	// TODO check if configured via spring, give error message if so
        jpaPersistenceProvider.assertMappingWithDatabaseConsistent(testObject, entityManagerFactoryManager);
    }


    /**
     * Gets a configured hibernate <code>SessionFactory</code> for the given test object. This
     * <code>SessionFactory</code> can be either configured in a Spring
     * <code>ApplicationContext</code> or using {@link HibernateSessionFactory} annotations. An
     * exception is thrown if no <code>SessionFactory</code> could be returned. If possible, a
     * cached instance is returned that was created during a previous test.
     *
     * @param testObject The test instance, not null
     * @return The Hibernate <code>SessionFactory</code>, not null
     */
    public EntityManagerFactory getEntityManagerFactory(Object testObject) {
    	EntityManagerFactory springModuleConfigured = null;
        if (jpaSpringSupport != null) {
            springModuleConfigured = jpaSpringSupport.getEntityManagerFactory(testObject);
        }
        EntityManagerFactory hibernateModuleConfigured = getEntityManagerFactoryManager().getEntityManagerFactory(testObject);
        if (springModuleConfigured != null && hibernateModuleConfigured != null) {
            throw new UnitilsException("A EntityManagerFactory configuration was found in both the spring configuration and by use of a " + JpaEntityManagerFactory.class.getSimpleName() + " annotation. One of them should be removed");
        }
        if (springModuleConfigured == null && hibernateModuleConfigured == null) {
            throw new UnitilsException("No EntityManagerFactory configuration was found for class " + testObject.getClass().getSimpleName());
        }
        return springModuleConfigured == null ? hibernateModuleConfigured : springModuleConfigured;
    }
    

    /**
     * Indicates whether a hibernate <code>SessionFactory</code> has been configured in some way for the given testObject
     *
     * @param testObject The test instance, not null
     * @return true if a <code>SessionFactory</code> has been configured, false otherwise
     */
    public boolean isEntityManagerFactoryConfiguredFor(Object testObject) {
    	EntityManagerFactory springModuleConfigured = null;
        if (jpaSpringSupport != null) {
            springModuleConfigured = jpaSpringSupport.getEntityManagerFactory(testObject);
        }
        return springModuleConfigured != null || getEntityManagerFactoryManager().getEntityManagerFactory(testObject) != null;
    }


    /**
     * Gets the manager for session factories and hibernate configurations.
     *
     * @return The manager, not null
     */
    public EntityManagerFactoryManager getEntityManagerFactoryManager() {
        return entityManagerFactoryManager;
    }
    

    /**
     * Closes all open entity managers.
     *
     * @param testObject The test instance, not null
     */
    public void closeEntityManagers(Object testObject) {
    	Set<EntityManager> activeEntityManagers = getActiveEntityManagers(testObject);
        for (EntityManager activeEntityManager : activeEntityManagers) {
        	if (activeEntityManager.isOpen()) {
            	logger.info("Closing entity manager " + activeEntityManager);
            	activeEntityManager.close();
            }
        }
    }


    /**
     * Flushes all pending updates to the database. This method is useful when the effect
     * of updates needs to be checked directly on the database. For verifying updates using the
     * <code>EntityManager</code> used for testing, flushing is not needed.
     * 
     * @param testObject The test instance, not null
     */
    public void flushDatabaseUpdates(Object testObject) {
    	Set<EntityManager> activeEntityManagers = getActiveEntityManagers(testObject);
        for (EntityManager activeEntityManager : activeEntityManagers) {
        	if (activeEntityManager.isOpen()) {
            	logger.info("Flushing entity manager " + activeEntityManager);
            	activeEntityManager.flush();
            }
        }
    }
    
    
    /**
     * Removes all JPA enity manager that were intercepted during this unit test
     * 
     * @param testObject The test instance, not null
     */
    public void clearInterceptedEntityManagers(Object testObject) {
    	if (isEntityManagerFactoryConfiguredFor(testObject)) {
    		EntityManagerFactory entityManagerFactory = getEntityManagerFactory(testObject);
            if (entityManagerFactory instanceof EntityManagerInterceptingEntityManagerFactory) {
                ((EntityManagerInterceptingEntityManagerFactory)entityManagerFactory).clearInterceptedEntityManagers();
            }
        }
    }
    
    
    /**
     * Forces the reloading of the hibernate configurations the next time that it is requested. If
     * classes are given only hibernate configurations that are linked to those classes will be
     * reset. If no classes are given, all cached hibernate configurations will be reset.
     *
     * @param classes The classes for which to reset the configs
     */
    public void invalidateConfiguration(Class<?>... classes) {
    	getEntityManagerFactoryManager().invalidateEntityManagerFactory(classes);
    }


    /**
     * Injects the JPA <code>EntityManagerFactory</code> into all fields and methods that are
     * annotated with {@link JpaEntityManagerFactory}
     *
     * @param testObject The test object, not null
     */
    public void injectEntityManagerFactory(Object testObject) {
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), JpaEntityManagerFactory.class);
        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), JpaEntityManagerFactory.class);

        // filter out methods without entity manager factory argument
        Iterator<Method> iterator = methods.iterator();
        while (iterator.hasNext()) {
            Class<?>[] parameterTypes = iterator.next().getParameterTypes();
            if (parameterTypes.length == 0 || !EntityManagerFactory.class.isAssignableFrom(parameterTypes[0])) {
                iterator.remove();
            }
        }
        if (fields.isEmpty() && methods.isEmpty()) {
            // Nothing to do. Jump out to make sure that we don't try to instantiate the
            // EntityManagerFactory
            return;
        }

        EntityManagerFactory entityManagerFactory = getEntityManagerFactory(testObject);
        setFieldAndSetterValue(testObject, fields, methods, entityManagerFactory);
    }
    
    
    protected Set<EntityManager> getActiveEntityManagers(Object testObject) {
    	// If no sessionfactory was configured in unitils, there are no open sessions
    	if (!isEntityManagerFactoryConfiguredFor(testObject)) {
    		return CollectionUtils.asSet();
    	}

    	// If the SessionFactory was configured in unitils, all opened session were intercepted
    	EntityManagerFactory entityManagerFactory = getEntityManagerFactory(testObject);
		if (entityManagerFactory instanceof EntityManagerInterceptingEntityManagerFactory) {
			return ((EntityManagerInterceptingEntityManagerFactory) entityManagerFactory).getActiveEntityManagers();
		}
		
		// If we got here, this means the session factory was configured using spring
		EntityManager activeEntityManager = jpaSpringSupport.getActiveEntityManager(testObject);
		if (activeEntityManager == null) {
			return CollectionUtils.asSet();
		}
		return CollectionUtils.asSet(activeEntityManager);
    }
    
    
    /**
     * Creates an instance of {@link org.unitils.hibernate.util.HibernateSpringSupportImpl}, that
     * implements the dependency to the {@link org.unitils.spring.SpringModule}. If the
     * {@link org.unitils.spring.SpringModule} is not
     * active, or if a dependency of {@link org.unitils.hibernate.util.HibernateSpringSupportImpl}
     * could not be found in the classpath, the instance is not loaded and the
     * SpringHibernateSupport is not enabled.
     */
    @SuppressWarnings("unchecked")
	protected void initJpaSpringSupport() {
        if (!isSpringModuleEnabled()) {
            return;
        }
        try {
        	Class jpaSpringSupportImplClass = Class.forName("org.unitils.jpa.util.JpaSpringSupportImpl");
        	Constructor constr = jpaSpringSupportImplClass.getConstructor(JpaPersistenceProvider.class);
        	jpaSpringSupport = (JpaSpringSupport) constr.newInstance(jpaPersistenceProvider);
        } catch (Exception e) {
            logger.warn("The HibernateSpringSupportImpl could not be loaded, probably due to a missing dependency", e);
        }
    }


	/**
     * Verifies whether the SpringModule is enabled. If not, this means that either the property unitils.modules doesn't
     * include spring, or unitils.module.spring.enabled = false, or that the module could not be loaded because spring is not
     * in the classpath.
     *
     * @return true if the SpringModule is enabled, false otherwise
     */
	private boolean isSpringModuleEnabled() {
		return Unitils.getInstance().getModulesRepository().isModuleEnabled("org.unitils.spring.SpringModule");
	}


	/**
     * @return The TestListener associated with this module
     */
    public TestListener getTestListener() {
        return new JpaTestListener();
    }


    /**
     * The {@link TestListener} for this module
     */
    protected class JpaTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            injectEntityManagerFactory(testObject);
        }

        @Override
        public void afterTestTearDown(Object testObject, Method testMethod) {
        	if (autoCloseEntityManagersAfterTest) {
        		closeEntityManagers(testObject);
        	}
    		clearInterceptedEntityManagers(testObject);
        }
    }
}
