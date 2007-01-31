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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModule;
import org.unitils.database.util.Flushable;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import org.unitils.hibernate.annotation.HibernateSession;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import org.unitils.hibernate.util.HibernateAssert;
import org.unitils.hibernate.util.HibernateConfigurationManager;
import org.unitils.hibernate.util.HibernateConnectionProvider;
import org.unitils.hibernate.util.SessionInterceptingSessionFactory;
import org.unitils.util.AnnotationUtils;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitils.util.ReflectionUtils.setFieldValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Module providing support for unit tests for code that uses Hibernate. It offers an easy way of loading hibernate
 * configuration files, creating Sessions and SessionFactories and injecting them in the test and a test to check
 * whether the hibernate mappings are consistent with the structure of the database.
 * <p/>
 * A Hibernate <code>Session</code> is created when requested and can be injected into the test by annotating a field
 * or setter method with the {@link HibernateSession} annotation. The same is true for the <code>SessionFactory</code>,
 * it will lazily be created and can be injected by annotating a field or setter method with the {@link HibernateSessionFactory}
 * annotation. If a session was created, it is closed automatically after the test's teardown.
 * <p/>
 * This module also manages the hibernate configurations that need to be loaded for the tests. These configurations are
 * cached, so a configuration will be reused when possible. For example suppose a superclass loads a configuration and
 * a test-subclass wants to use this configuration, it will not create a new one. {@link #invalidateHibernateConfiguration}
 * can be used to force a reloading of a configuration if needed. What and how a hibernate configuration is loaded, is
 * specified by using {@link HibernateConfiguration} annotations.
 * <p/>
 * Once a configuration is loaded, the property 'hibernate.connection.provider_class' will be overwritten so that Hibernate
 * will load the {@link HibernateConnectionProvider} as provider. This way we can make sure that Hibernate will use
 * the unitils datasource and thus connect to the unit test database.
 * <p/>
 * It is highly recommended to write a unit test that invokes {@link HibernateUnitils#assertMappingWithDatabaseConsistent()},
 * This is a very powerful test that verifies if the mapping of all your Hibernate mapped objects with the database is
 * correct.
 * <p/>
 * This module depends on the {@link DatabaseModule}, for managing connections to a unit test database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateModule implements Module, Flushable {

    /* Property key of the class name of the hibernate configuration */
    public static final String PROPKEY_CONFIGURATION_CLASS_NAME = "HibernateModule.configuration.implClassName";

    /* todo javadoc */
    public static final String PROPKEY_MANAGECURRENTSESSIONCONTEXT_ENABLED = "HibernateModule.managecurrentsessioncontext.enabled";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(HibernateModule.class);

    /* Manager for storing and creating hibernate configurations */
    private HibernateConfigurationManager hibernateConfigurationManager;

    private SessionInterceptingSessionFactory currentSessionFactory;

    private Session currentSession;

    /**
     * Initializes the module.
     *
     * @param configuration The Unitils configuration, not null
     */
    public void init(org.apache.commons.configuration.Configuration configuration) {

        String hibernateConfigurationImplClassName = configuration.getString(PROPKEY_CONFIGURATION_CLASS_NAME);
        boolean manageCurrentSessionContext = configuration.getBoolean(PROPKEY_MANAGECURRENTSESSIONCONTEXT_ENABLED);
        this.hibernateConfigurationManager = new HibernateConfigurationManager(hibernateConfigurationImplClassName,
                manageCurrentSessionContext);
    }


    /**
     * Checks if the mapping of the Hibernate managed objects with the database is still correct.
     *
     * @param testObject The test instance, not null
     */
    public void assertMappingWithDatabaseConsistent(Object testObject) {

        Configuration configuration = getHibernateConfiguration(testObject);
        Session session = getHibernateSession(testObject);
        Dialect databaseDialect = getDatabaseDialect(configuration);

        HibernateAssert.assertMappingWithDatabaseConsistent(configuration, session, databaseDialect);
    }


    /**
     * Retrieves the current Hibernate <code>Session</code>. If there is no session yet, or if the current session is
     * closed, a new one is created. An exception will be thrown if no session could be returned.
     *
     * @param testObject The test instance, not null
     * @return An open and connected Hibernate <code>Session</code>, not null
     */
    public Session getHibernateSession(Object testObject) {

        currentSession = getHibernateSessionFactory(testObject).getCurrentSession();
        if (currentSession == null || !currentSession.isOpen()) {
            currentSession = getHibernateSessionFactory(testObject).openSession();
        }
        return currentSession;
    }


    /**
     * Gets a configured hibernate session facatory for the given test. If no such hibernate session exists
     * yet, a new one is created. The {@link HibernateConfiguration} annotations of the test will determine
     * how the configuration for creating this session factory will be loaded. An exception will be thrown if no
     * session factory could be returned.
     *
     * @param testObject The test instance, not null
     * @return The Hibernate <code>SessionFactory</code>, not null
     */
    public SessionFactory getHibernateSessionFactory(Object testObject) {

        currentSessionFactory = hibernateConfigurationManager.getHibernateSessionFactory(testObject);
        return currentSessionFactory;
    }


    /**
     * Gets the hibernate configuration for the given test. If no such configuration exists yet, a new one will
     * be loaded. The {@link HibernateConfiguration} annotations of the test will determine how this configuration
     * will be loaded. See the annotation javadoc for more info. An exception will be thrown if no configuration
     * could be loaded.
     *
     * @param testObject The test instance, not null
     * @return The Hibernate <code>Configuration</code>, not null
     */
    public Configuration getHibernateConfiguration(Object testObject) {
        return hibernateConfigurationManager.getHibernateConfiguration(testObject);
    }


    /**
     * Closes the currently opened Hibernate session. Such a Session can exist if the test class contains an
     * {@link HibernateSession} annotation to which a Session was injected, or if the {@link #getHibernateSession(Object)}
     * was called to get a <code>Session</code>, or if the test class contains a {@link HibernateSessionFactory} annotation
     * to which a <code>SessionFactory</code> was injected, or if the {@link #getHibernateSessionFactory(Object)} method
     * was called to get a <code>SessionFactory</code>, and a <code>Session</code> is currently opened on this
     * <code>SessionFactory</code> (i.e. a <code>Session</code> is available using <code>SessionFactory.getCurrentSession()</code>).
     */
    public void closeHibernateSession(Object testObject) {

        Set<Session> openedSessions = getOpenedSessions();
        for (Session openedSession : openedSessions) {
            if (openedSession.isOpen()) {
                logger.debug("Closing Hibernate Session");
                openedSession.close();
            }
        }
        currentSession = null;
        if (currentSessionFactory != null) {
            currentSessionFactory.forgetOpenedSessions();
            currentSessionFactory = null;
        }
    }

    /**
     * @return The hibernate Session that is currently open. Such a Session can exist if the test class contains an
     * {@link HibernateSession} annotation to which a Session was injected, or if the {@link #getHibernateSession(Object)}
     * was called to get a <code>Session</code>, or if the test class contains a {@link HibernateSessionFactory} annotation
     * to which a <code>SessionFactory</code> was injected, or if the {@link #getHibernateSessionFactory(Object)} method
     * was called to get a <code>SessionFactory</code>, and a <code>Session</code> is currently opened on this
     * <code>SessionFactory</code> (i.e. a <code>Session</code> is available using <code>SessionFactory.getCurrentSession()</code>).
     * If no such <code>Session</code> exists, null is returned.
     */
    private Set<Session> getOpenedSessions() {

        Set<Session> openedSessions = new HashSet<Session>();
        if (currentSession != null) {
            openedSessions.add(currentSession);
        }
        if (currentSessionFactory != null) {
            openedSessions.addAll(currentSessionFactory.getOpenedSessions());
        }
        return openedSessions;
    }


    /**
     * Forces the reloading of the hibernate configurations the next time that it is requested. If classes are given
     * only hibernate configurations that are linked to those classes will be reset. If no classes are given, all cached
     * hibernate configurations will be reset.
     *
     * @param classes The classes for which to reset the configs
     */
    public void invalidateHibernateConfiguration(Class<?>... classes) {
        hibernateConfigurationManager.invalidateHibernateConfiguration(classes);
    }


    /**
     * Flushes all pending Hibernate updates to the database. This method is useful when the effect of updates needs to
     * be checked directly on the database. For verifying updates using the Hibernate <code>Session</code> provided by
     * the method #getCurrentSession, flushing is not needed.
     */
    public void flushDatabaseUpdates() {

        Set<Session> openedSessions = getOpenedSessions();
        for (Session openedSession : openedSessions) {
            openedSession.flush();
        }
    }


    /**
     * Gets the hibernate configuration for this class and sets it on the fields and setter methods that are
     * annotated with {@link HibernateConfiguration}. If no configuration could be created, an
     * UnitilsException will be raised.
     *
     * @param testObject The test instance, not null
     */
    public void injectHibernateConfiguration(Object testObject) {
        // inject into fields annotated with @HibernateConfiguration
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), HibernateConfiguration.class);
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, getHibernateConfiguration(testObject));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the hibernate configuration to field annotated with @" + HibernateConfiguration.class.getSimpleName(), e);
            }
        }

        // inject into setter methods annotated with @HibernateConfiguration
        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), HibernateConfiguration.class, false);
        for (Method method : methods) {
            // ignore custom create methods
            if (method.getReturnType() != Void.TYPE) {
                continue;
            }
            try {
                invokeMethod(testObject, method, getHibernateConfiguration(testObject));

            } catch (Exception e) {
                throw new UnitilsException("Unable to assign the hibernate configuration to setter annotated with @" + HibernateConfiguration.class.getSimpleName(), e);
            }
        }
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
                setFieldValue(testObject, field, getHibernateSessionFactory(testObject));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the hibernate Session to field annotated with @" + HibernateSessionFactory.class.getSimpleName() +
                        "Ensure that this field is of type " + SessionFactory.class.getName(), e);
            }
        }

        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), HibernateSessionFactory.class);
        for (Method method : methods) {
            try {
                invokeMethod(testObject, method, getHibernateSessionFactory(testObject));

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
                setFieldValue(testObject, field, getHibernateSession(testObject));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the hibernate Session to field annotated with @" +
                        HibernateSession.class.getSimpleName() + "Ensure that this field is of type " + Session.class.getName(), e);
            }
        }

        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), HibernateSession.class);
        for (Method method : methods) {
            try {
                invokeMethod(testObject, method, getHibernateSession(testObject));

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
     * Gets the database dialect from the Hibernate <code>Configuration</code.
     *
     * @param configuration The hibernate config, not null
     * @return the databazse Dialect, not null
     */
    protected Dialect getDatabaseDialect(Configuration configuration) {
        String dialectClassName = configuration.getProperty("hibernate.dialect");
        if (StringUtils.isEmpty(dialectClassName)) {
            throw new UnitilsException("Property hibernate.dialect not specified");
        }
        try {
            return (Dialect) Class.forName(dialectClassName).newInstance();
        } catch (Exception e) {
            throw new UnitilsException("Could not instantiate dialect class " + dialectClassName, e);
        }
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
    private class HibernateTestListener extends TestListener {

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            injectHibernateSessionFactory(testObject);
            injectHibernateSession(testObject);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod) {
            closeHibernateSession(testObject);
        }

    }
}
