package org.unitils.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsModule;
import org.unitils.core.UnitilsException;
import org.unitils.util.UnitilsConfiguration;
import org.unitils.util.AnnotationUtils;
import org.unitils.db.DatabaseModule;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import org.unitils.hibernate.annotation.InjectHibernateSession;

import java.lang.reflect.Method;
import java.util.List;
import java.sql.Connection;

/**
 */
public class HibernateModule implements UnitilsModule {

    private static final String PROPKEY_HIBERNATE_CONFIGFILES = "hibernatetestcase.hibernate.cfg.configfiles";

    private static final String PROPKEY_HIBERNATE_CONFIGURATION_CLASS = "hibernatetestcase.hibernate.cfg.configurationclass";

    private Configuration hibernateConfiguration;

    private SessionFactory hibernateSessionFactory;

    private ThreadLocal<Session> currentHibernateSessionHolder = new ThreadLocal<Session>();

    public Configuration getHibernateConfiguration() {
        return hibernateConfiguration;
    }

    public Session getCurrentSession() {
        return currentHibernateSessionHolder.get();
    }

    private void configureHibernate() {
        Object testObject = Unitils.getTestContext().getTestObject();
        createHibernateConfiguration(testObject);
        createHibernateSessionFactory();
    }

    private void createHibernateConfiguration(Object test) {
        String configurationClassName = UnitilsConfiguration.getInstance().getString(PROPKEY_HIBERNATE_CONFIGURATION_CLASS);
        List<String> configFiles = UnitilsConfiguration.getInstance().getList(PROPKEY_HIBERNATE_CONFIGFILES);
        try {
            hibernateConfiguration = (Configuration) Class.forName(configurationClassName).newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid configuration class " + configurationClassName, e);
        }
        for (String configFile : configFiles) {
            hibernateConfiguration.configure(configFile);
        }
        performExtraHibernateConfiguration(test, hibernateConfiguration);
    }

    private void performExtraHibernateConfiguration(Object test, Configuration configuration) {
        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(test.getClass(), HibernateConfiguration.class);
        for (Method method : methods) {
            try {
                method.invoke(test, configuration);
            } catch (Exception e) {
                throw new UnitilsException("Error while calling method annotated with @" +
                        HibernateConfiguration.class.getSimpleName() + ". Ensure that this method has following signature: " +
                        "void myMethod(Configuration hibernateConfiguration)", e);
            }
        }
    }

    private void createAndInjectHibernateSessionIfNecessary(Object testObject) {
        Session currentHibernateSession = currentHibernateSessionHolder.get();
        if (currentHibernateSession == null || !currentHibernateSession.isConnected()) {
            if (currentHibernateSession != null && currentHibernateSession.isOpen()) {
                currentHibernateSession.close();
            }
            currentHibernateSessionHolder.set(hibernateSessionFactory.openSession(getConnection()));
            injectHibernateSession(testObject);
        }
    }

    private void injectHibernateSession(Object testObject) {
        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), InjectHibernateSession.class);
        for (Method method : methods) {
            try {
                method.invoke(testObject, currentHibernateSessionHolder);
            } catch (Exception e) {
                throw new UnitilsException("Error while calling method annotated with @" +
                        InjectHibernateSession.class.getSimpleName() + ". Ensure that this method has following signature: " +
                        "void myMethod(org.hibernate.Session session)", e);
            }
        }
    }

    private Connection getConnection() {
        DatabaseModule dbModule = Unitils.getModulesRepository().getModule(DatabaseModule.class);
        return dbModule.getCurrentConnection();
    }

    private void createHibernateSessionFactory() {
        hibernateSessionFactory = hibernateConfiguration.buildSessionFactory();
    }

    public TestListener createTestListener() {
        return new HibernateTestListener();
    }

    private class HibernateTestListener extends TestListener {

        @Override
        public void beforeTestClass() {
            if (hibernateConfiguration == null) {
                configureHibernate();
            }
        }

        @Override
        public void beforeTestMethod() {
            createAndInjectHibernateSessionIfNecessary(Unitils.getTestContext().getTestObject());
        }

    }
}
