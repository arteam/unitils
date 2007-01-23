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
import org.unitils.util.AnnotationUtils;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Module providing support for unit tests for code that uses Hibernate. This involves unit tests for queries to the
 * database and the Hibernate mapping test implemented by {@link org.unitils.hibernate.util.HibernateAssert}
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
 * It is highly recommended to write a unit test that invokes {@link #assertMappingToDatabase(Object)},
 * This is a very powerful test that verifies if the mapping of all your Hibernate mapped objects with the database is
 * correct.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateModule implements Module, Flushable {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(HibernateModule.class);

    /* Manager for storing and creating hibernate configurations */
    private HibernateConfigurationManager hibernateConfigurationManager;

    /* The Hibernate Session that is used in unit tests */
    private Session hibernateSession;


    /**
     * Initializes the module.
     *
     * @param configuration The Unitils configuration, not null
     */
    public void init(org.apache.commons.configuration.Configuration configuration) {
        this.hibernateConfigurationManager = new HibernateConfigurationManager();
    }


    /**
     * Checks if the mapping of the Hibernate managed objects with the database is still correct.
     *
     * @param testObject The test instance, not null
     */
    public void assertMappingToDatabase(Object testObject) {
        Configuration configuration = getHibernateConfiguration(testObject);
        Session session = getHibernateSession(testObject);
        Dialect databaseDialect = getDatabaseDialect(configuration);

        HibernateAssert.assertMappingToDatabase(configuration, session, databaseDialect);
    }


    /**
     * Retrieves the current Hibernate <code>Session</code>. If there is no session yet, or if the current session is
     * closed, a new one is created. If the current session is disconnected, it is reconnected with a
     * <code>Connection</code> from the pool.
     *
     * @param testObject The test instance, not null
     * @return An open and connected Hibernate <code>Session</code>
     */
    public Session getHibernateSession(Object testObject) {
        if (hibernateSession == null || !hibernateSession.isOpen()) {
            hibernateSession = getHibernateSessionFactory(testObject).openSession();
        }
        return hibernateSession;
    }


    //todo javadoc
    public Configuration getHibernateConfiguration(Object testObject) {
        return hibernateConfigurationManager.getHibernateConfiguration(testObject);
    }


    /**
     * todo javadoc
     *
     * @param testObject The test instance, not null
     * @return The Hibernate SessionFactory
     */
    public SessionFactory getHibernateSessionFactory(Object testObject) {
        return hibernateConfigurationManager.getHibernateSessionFactory(testObject);
    }


    /**
     * Closes the current Hibernate session.
     */
    public void closeHibernateSession() {
        if (hibernateSession != null && hibernateSession.isOpen()) {
            logger.debug("Closing Hibernate Session");
            hibernateSession.close();
            hibernateSession = null;
        }
    }


    /**
     * Flushes all pending Hibernate updates to the database. This method is useful when the effect of updates needs to
     * be checked directly on the database. For verifying updates using the Hibernate <code>Session</code> provided by
     * the method #getCurrentSession, flushing is not needed.
     */
    public void flushDatabaseUpdates() {
        if (hibernateSession != null) {
            hibernateSession.flush();
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
            if (!(isSetter(method))) {
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
            closeHibernateSession();
        }

    }
}
