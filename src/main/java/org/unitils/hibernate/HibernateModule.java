/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.hibernate;

import static org.apache.commons.lang.StringUtils.isEmpty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModule;
import org.unitils.database.util.Flushable;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import org.unitils.hibernate.util.HibernateAssert;
import org.unitils.hibernate.util.HibernateConnectionProvider;
import org.unitils.hibernate.util.HibernateSpringSupport;
import org.unitils.hibernate.util.SessionFactoryManager;
import org.unitils.hibernate.util.SessionInterceptingSessionFactory;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.PropertyUtils.getString;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;
import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * todo javadoc
 * <p/>
 * Module providing support for unit tests for code that uses Hibernate. It offers an easy way of loading hibernate
 * configuration files, creating Sessions and SessionFactories and injecting them in the test. It also offers a test
 * to check whether the hibernate mappings are consistent with the structure of the database.
 * <p/>
 * A Hibernate <code>Session</code> is created when requested and can be injected into the test by annotating a field
 * or setter method with the HibernateSession annotation. The same is true for the <code>SessionFactory</code>,
 * it will lazily be created and can be injected by annotating a field or setter method with the {@link HibernateSessionFactory}
 * annotation.
 * <p/>
 * All created session will be tracked during a test and automatically closed after the test's teardown.
 * The {@link #PROPKEY_CURRENTSESSIONCONTEXT_CLASS_NAME} property determines which <code>CurrentSessionContext</code> is
 * used for the session factory. If this is null no CurrentSessionContext is installed. If this is set to a valid value, the
 * current session will also be available during your test using {@link SessionFactory#getCurrentSession()}.
 * <p/>
 * This module also manages the hibernate configurations that need to be loaded for the tests. Configurations will be reused
 * when possible by caching them on class level. If a superclass loads a configuration and a test-subclass does not define
 * its own, the cached configuration of the superclass will be used. The {@link #invalidateConfiguration}
 * method can be used to force a reloading of a configuration if needed. What and how a hibernate configuration is loaded,
 * is specified by using {@link HibernateSessionFactory} annotations. See the annotation javadoc for more information.
 * <p/>
 * Once a configuration is loaded, the property 'hibernate.connection.provider_class' will be overwritten so that Hibernate
 * will load the {@link HibernateConnectionProvider} as provider. This way we can make sure that Hibernate will use
 * the unitils datasource and thus connect to the unit test database.
 * <p/>
 * It is highly recommended to write a unit test that invokes {@link HibernateUnitils#assertMappingWithDatabaseConsistent()},
 * This is a very powerful test that verifies whether the mapping of all your Hibernate mapped objects still corresponds
 * to the actual state of the database.
 * <p/>
 * This module depends on the {@link DatabaseModule}, for managing connections to a unit test database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateModule implements Module, Flushable {

    /* Property key of the class name of the hibernate configuration */
    public static final String PROPKEY_CONFIGURATION_CLASS_NAME = "HibernateModule.configuration.implClassName";

    /* Property key of the class name of the CurrentSessionContext to use, null for no context */
    public static final String PROPKEY_CURRENTSESSIONCONTEXT_CLASS_NAME = "HibernateModule.currentsessioncontext.implClassName";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(HibernateModule.class);

    /**
     * Manager for storing and creating hibernate configurations
     */
    protected SessionFactoryManager sessionFactoryManager;

    /**
     * The spring hibernate support, null if spring is not available
     */
    protected HibernateSpringSupport hibernateSpringSupport;


    /**
     * Initializes the module.
     *
     * @param configuration The Unitils configuration, not null
     */
    public void init(Properties configuration) {
        String configurationImplClassName = getString(PROPKEY_CONFIGURATION_CLASS_NAME, configuration);

        String currentSessionContextImplClassName = getString(PROPKEY_CURRENTSESSIONCONTEXT_CLASS_NAME, configuration);
        this.sessionFactoryManager = new SessionFactoryManager(configurationImplClassName, currentSessionContextImplClassName);
    }


    /**
     * Checks if the mapping of the Hibernate managed objects with the database is still correct.
     *
     * @param testObject The test instance, not null
     */
    public void assertMappingWithDatabaseConsistent(Object testObject) {
        Configuration configuration = getHibernateConfiguration(testObject);
        Session session = getSessionFactory(testObject).openSession();
        Dialect databaseDialect = getDatabaseDialect(configuration);

        HibernateAssert.assertMappingWithDatabaseConsistent(configuration, session, databaseDialect);
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
    public SessionInterceptingSessionFactory getSessionFactory(Object testObject) {
        SessionInterceptingSessionFactory springModuleConfigured = null;
        if (hibernateSpringSupport != null) {
            springModuleConfigured = hibernateSpringSupport.getSessionFactory(testObject);
        }
        SessionInterceptingSessionFactory hibernateModuleConfigured = getSessionFactoryManager().getSessionFactory(testObject);
        if (springModuleConfigured != null && hibernateModuleConfigured != null) {
            throw new UnitilsException("A SessionFactory configuration was found in both the spring configuration and by use of a " + HibernateSessionFactory.class.getSimpleName() + " annotation. One of them should be removed");
        }
        if (springModuleConfigured == null && hibernateModuleConfigured == null) {
            throw new UnitilsException("No SessionFactory configuration was found for class " + testObject.getClass().getSimpleName());
        }
        return springModuleConfigured == null ? hibernateModuleConfigured : springModuleConfigured;
    }


    /**
     * Gets a configured hibernate <code>Configuration</code> for the given test object. This
     * <code>Configuration</code> can be either configured in a Spring
     * <code>ApplicationContext</code> or using {@link HibernateSessionFactory} annotations. An
     * exception is thrown if no <code>Configuration</code> could be returned. If possible, a
     * cached instance is returned that was created during a previous test.
     *
     * @param testObject The test instance, not null
     * @return The configuration, not null
     */
    public Configuration getHibernateConfiguration(Object testObject) {
        Configuration springModuleConfigured = null;
        if (hibernateSpringSupport != null) {
            springModuleConfigured = hibernateSpringSupport.getConfiguration(testObject);
        }
        Configuration hibernateModuleConfigured = getSessionFactoryManager().getConfiguration(testObject);
        if (springModuleConfigured != null && hibernateModuleConfigured != null) {
            throw new UnitilsException("A SessionFactory configuration was found in both the spring configuration and by use of a " + HibernateSessionFactory.class.getSimpleName() + " annotation. One of them should be removed");
        }
        if (springModuleConfigured == null && hibernateModuleConfigured == null) {
            throw new UnitilsException("No SessionFactory configuration was found for class " + testObject.getClass().getSimpleName());
        }
        return springModuleConfigured == null ? hibernateModuleConfigured : springModuleConfigured;
    }


    /**
     * Indicates whether a hibernate <code>SessionFactory</code> has been configured in some way for the given testObject
     *
     * @param testObject The test instance, not null
     * @return true if a <code>SessionFactory</code> has been configured, false otherwise
     */
    public boolean isSessionFactoryConfiguredFor(Object testObject) {
        SessionInterceptingSessionFactory springModuleConfigured = null;
        if (hibernateSpringSupport != null) {
            springModuleConfigured = hibernateSpringSupport.getSessionFactory(testObject);
        }
        return springModuleConfigured != null || getSessionFactoryManager().getSessionFactory(testObject) != null;
    }


    /**
     * Gets the manager for session factories and hibernate configurations.
     *
     * @return The manager, not null
     */
    public SessionFactoryManager getSessionFactoryManager() {
        return sessionFactoryManager;
    }


    /**
     * Closes all open Hibernate session.
     *
     * @param testObject The test instance, not null
     */
    public void closeSessions(Object testObject) {
        if (isSessionFactoryConfiguredFor(testObject)) {
            SessionInterceptingSessionFactory sessionFactory = getSessionFactory(testObject);
            // close all open sessions
            sessionFactory.closeOpenSessions();
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
        getSessionFactoryManager().invalidateSessionFactory(classes);
    }


    /**
     * Flushes all pending Hibernate updates to the database. This method is useful when the effect
     * of updates needs to be checked directly on the database. For verifying updates using the
     * Hibernate <code>Session</code> provided by the method #getCurrentSession, flushing is not
     * needed.
     */
    public void flushDatabaseUpdates() {
        // get all open session factories
        List<SessionInterceptingSessionFactory> sessionFactories = getSessionFactoryManager().getSessionFactories();
        for (SessionInterceptingSessionFactory sessionFactory : sessionFactories) {
            // flush all open sessions
            sessionFactory.flushOpenSessions();
        }
    }


    /**
     * Injects the Hibernate <code>SessionFactory</code> into all fields and methods that are
     * annotated with {@link HibernateSessionFactory}
     *
     * @param testObject The test object, not null
     */
    public void injectSessionFactory(Object testObject) {
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), HibernateSessionFactory.class);
        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), HibernateSessionFactory.class);

        // filter out methods with session factory argument
        Iterator<Method> iterator = methods.iterator();
        while (iterator.hasNext()) {
            Class<?>[] parameterTypes = iterator.next().getParameterTypes();
            if (parameterTypes.length == 0 || !SessionFactory.class.isAssignableFrom(parameterTypes[0])) {
                iterator.remove();
            }
        }
        if (fields.isEmpty() && methods.isEmpty()) {
            // Nothing to do. Jump out to make sure that we don't try to instantiate the
            // SessionFactory
            return;
        }

        SessionFactory sessionFactory = getSessionFactory(testObject);
        setFieldAndSetterValue(testObject, fields, methods, sessionFactory);
    }


    /**
     * Gets the database dialect from the Hibernate <code>Configuration</code.
     *
     * @param configuration The hibernate config, not null
     * @return the databazse Dialect, not null
     */
    protected Dialect getDatabaseDialect(Configuration configuration) {
        String dialectClassName = configuration.getProperty("hibernate.dialect");
        if (isEmpty(dialectClassName)) {
            throw new UnitilsException("Property hibernate.dialect not specified");
        }
        try {
            return (Dialect) Class.forName(dialectClassName).newInstance();
        } catch (Exception e) {
            throw new UnitilsException("Could not instantiate dialect class " + dialectClassName, e);
        }
    }


    /**
     * Creates an instance of {@link org.unitils.hibernate.util.HibernateSpringSupportImpl}, that
     * implements the dependency to the {@link org.unitils.spring.SpringModule}. If the
     * {@link org.unitils.spring.SpringModule} is not
     * active, or if a dependency of {@link org.unitils.hibernate.util.HibernateSpringSupportImpl}
     * could not be found in the classpath, the instance is not loaded and the
     * SpringHibernateSupport is not enabled.
     */
    protected void createSpringHibernateSupport() {
        if (!isSpringModuleEnabled()) {
            return;
        }
        try {
            hibernateSpringSupport = createInstanceOfType("org.unitils.hibernate.util.HibernateSpringSupportImpl");
        } catch (UnitilsException e) {
            logger.warn("The HibernateSpringSupportImpl could not be loaded, probably due to a missing dependency", e);
        }
    }


    /**
     * Verifies whether the SpringModule is enabled. If not, this means that either the property unitils.modules doesn't
     * include spring, or unitils.module.spring.enabled, or that the module could not be loaded because spring is not
     * in the classpath.
     * @return true if the SpringModule is enabled, false otherwise
     */
    protected boolean isSpringModuleEnabled() {
        return Unitils.getInstance().getModulesRepository().isModuleEnabled("org.unitils.spring.SpringModule");
    }


    /**
     * @return The TestListener associated with this module
     */
    public TestListener createTestListener() {
        return new HibernateTestListener();
    }


    /**
     * The {@link TestListener} for this module
     */
    protected class HibernateTestListener extends TestListener {

        @Override
        public void beforeAll() {
            createSpringHibernateSupport();
        }

        @Override
        public void beforeTestSetUp(Object testObject) {
            injectSessionFactory(testObject);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod) {
            closeSessions(testObject);
        }
    }
}
