/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.db.DatabaseModule;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import org.unitils.hibernate.annotation.HibernateSession;
import org.unitils.hibernate.annotation.HibernateTest;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

/**
 * Module providing support for unit tests for code that uses Hibernate. This involves unit tests for queries to the
 * database and the Hibernate mapping test implemented by {@link HibernateAssert}
 * <p/>
 * This module depends on the {@link DatabaseModule}, for managing connections to a unit test database.
 * <p/>
 * The configuration of Hibernate is performed by loading all configuration files associated with the property key
 * {@link #PROPKEY_HIBERNATE_CONFIGFILES}, using the Hibernate configuration class associated with the property key
 * {@link #PROPKEY_HIBERNATE_CONFIGURATION_CLASS}. Support for programmatic configuration is also foreseen: all methods
 * annotated with {@link HibernateConfiguration} will be invoked with the Hibernate <code>Configuration</code> object
 * as parameters, before the Hibernate <code>SesssionFactory</code> is constructed.
 * <p/>
 * A Hibernate <code>Session</code> is created before each test setup, and closed after eacht test's teardown.
 * This session will connect to the unit test database as configured by the {@link DatabaseModule}. The session will be
 * injected to fields or methods annotated with {@link HibernateSession} before each test setup. This way, the Hibernate
 * session can easily be injected into the tested environment.
 * <p/>
 * It is highly recommended to write a unit test that invokes {@link org.unitils.hibernate.HibernateAssert#assertMappingToDatabase()},
 * This is a very powerful test that verifies if the mapping of all your Hibernate mapped objects with the database is
 * correct.
 */
public class HibernateModule implements Module {

    /* Property key for a comma seperated list of Hibernate configuration files, that can be found in the classpath */
    public static final String PROPKEY_HIBERNATE_CONFIGFILES = "hibernatetestcase.hibernate.cfg.configfiles";

    /* Property key for the Hibernate configuration class that is used */
    public static final String PROPKEY_HIBERNATE_CONFIGURATION_CLASS = "hibernatetestcase.hibernate.cfg.configurationclass";

    /* The Hibernate configuration */
    private Configuration hibernateConfiguration;

    /* The Hibernate SessionFactory */
    private SessionFactory hibernateSessionFactory;

    /* The Hibernate Session that is used in unit tests, one for each thread */
    private ThreadLocal<Session> currentHibernateSessionHolder = new ThreadLocal<Session>();

    /* Fully qualified class name of the Hibernate Configuration class that is used */
    private String configurationClassName;

    /* List of Hibernate configuration files */
    private List<String> configFiles;

    /**
     * Initializes the module. The given <code>Configuration</code> object should contain values for the properties
     * {@link #PROPKEY_HIBERNATE_CONFIGFILES}, and {@link #PROPKEY_HIBERNATE_CONFIGURATION_CLASS}
     *
     * @param configuration The Unitils configuration, not null
     */
    public void init(org.apache.commons.configuration.Configuration configuration) {

        configFiles = configuration.getList(PROPKEY_HIBERNATE_CONFIGFILES);
        configurationClassName = configuration.getString(PROPKEY_HIBERNATE_CONFIGURATION_CLASS);
    }


    /**
     * Checks whether the given test instance is a hibernate test, i.e. is annotated with the {@link HibernateTest} annotation.
     *
     * @param testClass the test class, not null
     * @return true if the test class is a hibernate test, false otherwise
     */
    public boolean isHibernateTest(Class<?> testClass) {

        return testClass.getAnnotation(HibernateTest.class) != null;
    }

    /**
     * Configurates Hibernate, i.e. creates a Hibernate configuration object, and instantiates the Hibernate
     * <code>SessionFactory</code>. The class-level JavaDoc explains how Hibernate is to be configured.
     *
     * @param testObject
     */
    protected void configureHibernate(Object testObject) {

        if (hibernateConfiguration == null) {
            hibernateConfiguration = createHibernateConfiguration(testObject);
            createHibernateSessionFactory();
        }
    }

    /**
     * Creates completely configured Hibernate <code>Configuration</code> object
     *
     * @param test
     * @return the Hibernate configuration
     */
    private Configuration createHibernateConfiguration(Object test) {

        Configuration hbnConfiguration = createHibernateConfiguration();
        callHibernateConfigurationMethods(test, hbnConfiguration);
        return hbnConfiguration;
    }

    /**
     * Creates an unconfigured instance of <code>Configuration</code>
     *
     * @return a Hibernate <code>Configuration</code> object
     */
    protected Configuration createHibernateConfiguration() {

        Configuration hbnConfiguration;
        try {
            hbnConfiguration = (Configuration) Class.forName(configurationClassName).newInstance();
        } catch (ClassNotFoundException e) {
            throw new UnitilsException("Invalid configuration class " + configurationClassName, e);
        } catch (IllegalAccessException e) {
            throw new UnitilsException("Illegal access to configuration class " + configurationClassName, e);
        } catch (InstantiationException e) {
            throw new UnitilsException("Exception while instantiating instance of class " + configurationClassName, e);
        }
        for (String configFile : configFiles) {
            hbnConfiguration.configure(configFile);
        }
        return hbnConfiguration;
    }

    /**
     * Calls all methods annotated with {@link HibernateConfiguration}, so that they can perform extra Hibernate
     * configuration before the <code>SessionFactory</code> is created.
     *
     * @param testObject
     * @param configuration
     */
    private void callHibernateConfigurationMethods(Object testObject, Configuration configuration) {

        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), HibernateConfiguration.class);
        for (Method method : methods) {
            try {
                ReflectionUtils.invokeMethod(testObject, method, configuration);
            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to invoke method annotated with @" +
                        HibernateConfiguration.class.getSimpleName() + ". Ensure that this method has following signature: " +
                        "void myMethod(" + Configuration.class.getName() + " configuration)", e);
            }
        }
    }

    /**
     * Injects the Hibernate <code>Session</code> into all fields and methods that are annotated with {@link HibernateSession}
     *
     * @param testObject
     */
    protected void injectHibernateSession(Object testObject) {

        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), HibernateSession.class);
        for (Field field : fields) {
            try {
                ReflectionUtils.setFieldValue(testObject, field, getCurrentSession());

            } catch (UnitilsException e) {

                throw new UnitilsException("Unable to assign the hibernate Session to field annotated with @" +
                        HibernateSession.class.getSimpleName() + "Ensure that this field is of type " +
                        Session.class.getName(), e);
            }
        }

        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), HibernateSession.class);
        for (Method method : methods) {
            try {
                ReflectionUtils.invokeMethod(testObject, method, getCurrentSession());
            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to invoke method annotated with @" +
                        HibernateSession.class.getSimpleName() + ". Ensure that this method has following signature: " +
                        "void myMethod(" + Session.class.getName() + " session)", e);
            }
        }
    }

    /**
     * Retries the currently active <code>Connection</code> to the unit test database
     *
     * @return the connection to the database
     */
    protected Connection getConnection() {

        Unitils unitils = Unitils.getInstance();
        DatabaseModule dbModule = unitils.getModulesRepository().getFirstModule(DatabaseModule.class);
        return dbModule.getCurrentConnection();
    }

    /**
     * Creates the Hibernate <code>SessionFactory</code>
     */
    private void createHibernateSessionFactory() {
        hibernateSessionFactory = hibernateConfiguration.buildSessionFactory();
    }

    /**
     * @return The Hibernate Configuration object
     */
    public Configuration getHibernateConfiguration() {
        return hibernateConfiguration;
    }

    /**
     * Retrieves the current Hibernate <code>Session</code>. If there is no session yet, or if the current session is
     * closed, a new one is created. If the current session is disconnected, it is reconnected with the current
     * <code>Connection</code>.
     *
     * @return An open and connected Hibernate <code>Session</code>
     */
    public Session getCurrentSession() {

        Session currentSession = currentHibernateSessionHolder.get();
        if (currentSession == null || !currentSession.isOpen()) {
            currentSession = hibernateSessionFactory.openSession(getConnection());
            currentHibernateSessionHolder.set(currentSession);
        } else {
            if (!currentSession.isConnected()) {
                currentSession.reconnect(getConnection());
            }
        }
        return currentSession;
    }

    /**
     * Closes the current Hibernate session.
     */
    public void closeHibernateSession() {

        Session currentSession = currentHibernateSessionHolder.get();
        if (currentSession != null && currentSession.isOpen()) {
            currentSession.close();
        }
    }

    /**
     * Flushes all pending Hibernate updates to the database. This method is useful when the effect of updates needs to
     * be checked directly on the database. For verifying updates using the Hibernate <code>Session</code> provided by
     * the method #getCurrentSession, flushing is not needed.
     */
    public void flushDatabaseUpdates() {

        getCurrentSession().flush();
    }

    /**
     * @return The TestListener associated with this module
     */
    public TestListener createTestListener() {
        return new HibernateTestListener();
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
        public void beforeAll() {
            Unitils unitils = Unitils.getInstance();
            DatabaseModule databaseModule = unitils.getModulesRepository().getFirstModule(DatabaseModule.class);
            databaseModule.registerDatabaseTestAnnotation(HibernateTest.class);
        }

        @Override
        public void beforeTestSetUp(Object testObject) {
            if (isHibernateTest(testObject.getClass())) {
                configureHibernate(testObject);
            }
        }

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {

            if (isHibernateTest(testObject.getClass())) {
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
