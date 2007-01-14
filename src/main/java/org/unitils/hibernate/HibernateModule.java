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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModule;
import org.unitils.database.util.Flushable;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import org.unitils.hibernate.annotation.HibernateSession;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import org.unitils.hibernate.annotation.HibernateTest;
import org.unitils.hibernate.util.HibernateConnectionProvider;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

/**
 * Module providing support for unit tests for code that uses Hibernate. This involves unit tests for queries to the
 * database and the Hibernate mapping test implemented by {@link HibernateAssert}
 * <p/>
 * This module depends on the {@link DatabaseModule}, for managing connections to a unit test database.
 * <p/>
 * The configuration of Hibernate is performed by executing the method annotated with {@link HibernateConfiguration}.
 * This method should take no parameters and return a Hibernate <code>Configuration</code> object. The property
 * 'hibernate.connection.provider_class' is overwritten by Unitils, it is set to {@link HibernateConnectionProvider}.
 * This makes sure that Hibernate connects with the unit test database configured by the {@link DatabaseModule}.
 * <p/>
 * A Hibernate <code>Session</code> is created before each test setup, and closed after each test's teardown.
 * This session will connect to the unit test database as configured by the {@link DatabaseModule}. The session will be
 * injected to fields or methods annotated with {@link HibernateSession} before each test setup. This way, the Hibernate
 * session can easily be injected into the tested environment.
 * <p/>
 * It is highly recommended to write a unit test that invokes {@link HibernateAssert#assertMappingToDatabase()},
 * This is a very powerful test that verifies if the mapping of all your Hibernate mapped objects with the database is
 * correct.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateModule implements Module, Flushable {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(HibernateModule.class);

    /* The Hibernate configuration */
    private Configuration hibernateConfiguration;

    /* The Hibernate SessionFactory */
    private SessionFactory hibernateSessionFactory;

    /* The Hibernate Session that is used in unit tests */
    private Session currentHibernateSession;


    /**
     * Initializes the module.
     *
     * @param configuration The Unitils configuration, not null
     */
    public void init(org.apache.commons.configuration.Configuration configuration) {
    }


    /**
     * Checks whether the given test instance is a hibernate test, i.e. is annotated with the {@link HibernateTest} annotation.
     *
     * @param testClass the test class, not null
     * @return true if the test class is a hibernate test, false otherwise
     */
    public boolean isHibernateTest(Class<?> testClass) {
        if (testClass == null) {
            return false;
        }
        return testClass.getAnnotation(HibernateTest.class) != null;
    }


    /**
     * Configurates Hibernate, i.e. calls that method that should create the Hibernate configuration object, and
     * instantiates the Hibernate <code>SessionFactory</code>. The class-level JavaDoc explains how Hibernate is to be
     * configured.
     *
     * @param testObject The test object, not null
     */
    public void configureHibernate(Object testObject) {
        if (hibernateConfiguration == null) {
            hibernateConfiguration = createHibernateConfiguration(testObject);
            createHibernateSessionFactory();
        }
    }


    /**
     * Creates completely configured Hibernate <code>Configuration</code> object. Executes the method in the test class
     * annotated with {@link HibernateConfiguration} to retrieve a <code>Configuration</code> object.
     * Unitils' own implementation of the Hibernate <code>ConnectionProvider</code>, {@link HibernateConnectionProvider},
     * is set as connection provider. This object makes sure that Hibernate uses connections of Unitils' own
     * <code>DataSource</code>.
     *
     * @param testObject The test object, not null
     * @return The Hibernate configuration
     */
    private Configuration createHibernateConfiguration(Object testObject) {
        logger.info("Configuring Hibernate for test " + testObject);
        Configuration hbnConfiguration = getUserHibernateConfiguration(testObject);
        if (hbnConfiguration.getProperty(Environment.CONNECTION_PROVIDER) != null) {
            logger.warn("The property " + Environment.CONNECTION_PROVIDER + " is present in your Hibernate configuration. " +
                    "This property will be overwritten with Unitils own ConnectionProvider implementation!");
        }

        Properties connectionProviderProperty = new Properties();
        connectionProviderProperty.setProperty(Environment.CONNECTION_PROVIDER, HibernateConnectionProvider.class.getName());
        hbnConfiguration.addProperties(connectionProviderProperty);
        return hbnConfiguration;
    }


    /**
     * Injects the Hibernate <code>SessionFactory</code> into all fields and methods that are annotated with
     * {@link HibernateSessionFactory}
     *
     * @param testObject The test object, not null
     */
    public void injectHibernateSessionFactory(Object testObject) {
        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), HibernateSessionFactory.class);
        for (Field field : fields) {
            try {
                ReflectionUtils.setFieldValue(testObject, field, getHibernateSessionFactory());

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the hibernate Session to field annotated with @" + HibernateSessionFactory.class.getSimpleName() +
                        "Ensure that this field is of type " + SessionFactory.class.getName(), e);
            }
        }

        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), HibernateSessionFactory.class);
        for (Method method : methods) {
            try {
                ReflectionUtils.invokeMethod(testObject, method, getHibernateSessionFactory());

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to invoke method annotated with @" + HibernateSessionFactory.class.getSimpleName() +
                        ". Ensure that this method has following signature: void myMethod(" + SessionFactory.class.getName() + " sessionFactory)", e);
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Method " + testObject.getClass().getSimpleName() + "." + methods.get(0).getName() +
                        " (annotated with " + HibernateSessionFactory.class.getSimpleName() + ") has thrown an exception", e.getCause());
            }
        }
    }


    /**
     * Injects the Hibernate <code>Session</code> into all fields and methods that are annotated with {@link HibernateSession}
     *
     * @param testObject The test object, not null
     */
    public void injectHibernateSession(Object testObject) {
        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), HibernateSession.class);
        for (Field field : fields) {
            try {
                ReflectionUtils.setFieldValue(testObject, field, getCurrentSession());

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the hibernate Session to field annotated with @" +
                        HibernateSession.class.getSimpleName() + "Ensure that this field is of type " + Session.class.getName(), e);
            }
        }

        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), HibernateSession.class);
        for (Method method : methods) {
            try {
                ReflectionUtils.invokeMethod(testObject, method, getCurrentSession());

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to invoke method annotated with @" + HibernateSession.class.getSimpleName() +
                        ". Ensure that this method has following signature: void myMethod(" + Session.class.getName() + " session)", e);
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Method " + testObject.getClass().getSimpleName() + "." + methods.get(0).getName() +
                        " (annotated with " + HibernateSession.class.getSimpleName() + ") has thrown an exception", e.getCause());
            }
        }
    }


    /**
     * @return The Hibernate Configuration object
     */
    public Configuration getHibernateConfiguration() {
        return hibernateConfiguration;
    }


    /**
     * Retrieves the current Hibernate <code>Session</code>. If there is no session yet, or if the current session is
     * closed, a new one is created. If the current session is disconnected, it is reconnected with a
     * <code>Connection</code> from the pool.
     *
     * @return An open and connected Hibernate <code>Session</code>
     */
    public Session getCurrentSession() {
        if (currentHibernateSession == null || !currentHibernateSession.isOpen()) {
            logger.debug("No Hibernate Session available. Creating a new one");
            currentHibernateSession = getHibernateSessionFactory().openSession();
        }
        return currentHibernateSession;
    }


    /**
     * Closes the current Hibernate session.
     */
    public void closeHibernateSession() {
        if (currentHibernateSession != null && currentHibernateSession.isOpen()) {
            logger.debug("Closing Hibernate Session");
            currentHibernateSession.close();
        }
    }


    /**
     * Flushes all pending Hibernate updates to the database. This method is useful when the effect of updates needs to
     * be checked directly on the database. For verifying updates using the Hibernate <code>Session</code> provided by
     * the method #getCurrentSession, flushing is not needed.
     */
    public void flushDatabaseUpdates() {
        if (currentHibernateSession != null) {
            currentHibernateSession.flush();
        }
    }


    /**
     * @return The TestListener associated with this module
     */
    public TestListener createTestListener() {
        return new HibernateTestListener();
    }


    /**
     * Creates the Hibernate <code>SessionFactory</code>
     */
    private void createHibernateSessionFactory() {
        logger.debug("Creating Hibernate SessionFactory");
        hibernateSessionFactory = hibernateConfiguration.buildSessionFactory();
    }


    /**
     * Calls all methods annotated with {@link HibernateConfiguration}, so that they can perform extra Hibernate
     * configuration before the <code>SessionFactory</code> is created.
     *
     * @param testObject The test object, not null
     * @return The Hibernate configuration
     */
    private Configuration getUserHibernateConfiguration(Object testObject) {
        Configuration configuration;
        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), HibernateConfiguration.class);
        if (methods.size() > 1) {
            throw new UnitilsException(methods.size() + " methods found in " + testObject.getClass().getSimpleName() +
                    " that are annotated with " + HibernateConfiguration.class.getSimpleName() + ". Only one such method should exist");
        } else if (methods.size() == 0) {
            throw new UnitilsException("No method found in " + testObject.getClass().getSimpleName() +
                    " that is annotated with " + HibernateConfiguration.class.getSimpleName());
        } else {
            try {
                configuration = ReflectionUtils.invokeMethod(testObject, methods.get(0));
            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to invoke method " + testObject.getClass().getSimpleName() + "." +
                        methods.get(0).getName() + " (annotated with @" + HibernateConfiguration.class.getSimpleName() +
                        "). Ensure that this method has following signature: " + Configuration.class.getName() + " myMethod()", e);
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Method " + testObject.getClass().getSimpleName() + "." + methods.get(0).getName() +
                        " (annotated with @" + HibernateConfiguration.class.getSimpleName() + ") has thrown an exception", e.getCause());
            }
        }
        if (configuration == null) {
            throw new UnitilsException("The configuration object returned by " + testObject.getClass().getSimpleName() + "." + methods.get(0).getName() +
                    " (annotated with " + HibernateConfiguration.class.getSimpleName() + ") should not return a null value");
        }
        return configuration;
    }


    /**
     * @return The Hibernate SessionFactory
     */
    private SessionFactory getHibernateSessionFactory() {
        if (hibernateSessionFactory == null) {
            Configuration hibernateConfiguration = getHibernateConfiguration();
            logger.debug("Creating Hibernate SessionFactory");

            //todo check for null hibernateConfiguration
            hibernateSessionFactory = hibernateConfiguration.buildSessionFactory();
        }
        return hibernateSessionFactory;
    }


    /**
     * TestListener that makes callbacks to methods of this module while running tests. This TestListener makes sure that
     * <ul>
     * <li>The {@link HibernateTest} annotation is registered as a databasetest annotation in the {@link DatabaseModule}</li>
     * <li>The Hibernate Configuration and SessionFactory are created when running the first Hibernate test</li>
     * <li>A Hibernate Session is created and injected before each test setup and closed after each test teardown</li>
     */
    private class HibernateTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject) {
            if (isHibernateTest(testObject.getClass())) {
                configureHibernate(testObject);
            }
        }

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            if (isHibernateTest(testObject.getClass())) {
                injectHibernateSessionFactory(testObject);
                injectHibernateSession(testObject);
            }
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod) {
            if (isHibernateTest(testObject.getClass())) {
                closeHibernateSession();
            }
        }

    }
}
