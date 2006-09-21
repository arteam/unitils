package be.ordina.unitils.testing.dao.hibernate;

import be.ordina.unitils.testing.UnitilsModule;
import be.ordina.unitils.testing.dao.DatabaseTestModule;
import be.ordina.unitils.util.PropertiesUtils;

import java.util.Properties;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.hibernate.cfg.Configuration;

/**
 * @author Filip Neven
 */
public class HibernateTestModule implements UnitilsModule {

    private static final String PROPKEY_HIBERNATE_CONFIGFILES = "hibernatetestcase.hibernate.cfg.configfiles";

    private static final String PROPKEY_HIBERNATE_CONFIGURATION_CLASS = "hibernatetestcase.hibernate.cfg.configurationclass";

    /* The configuration (unittest.properties) */
    protected static Properties properties;

    private static boolean firstTime;

    public void beforeSuite(Properties unitilsProperties) throws Exception {
        properties = unitilsProperties;
        firstTime = true;
    }

    public void beforeClass(Object test) throws Exception {
        if (firstTime) {
            firstTime = false;
            Configuration configuration = createHibernateConfiguration(this);
            UnitTestHibernateSessionManager unitTestHibernateSessionManager = new UnitTestHibernateSessionManager(configuration);
            HibernateSessionManager.injectInstance(unitTestHibernateSessionManager);
            injectSessionManager(test, unitTestHibernateSessionManager);
        }
    }

    private void injectSessionManager(Object test, UnitTestHibernateSessionManager unitTestHibernateSessionManager) {
        Method[] methods = test.getClass().getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(InjectSessionManager.class) != null) {
                try {
                    method.invoke(test, unitTestHibernateSessionManager);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Configuration createHibernateConfiguration(Object test) {
        String configurationClassName = PropertiesUtils.getPropertyRejectNull(properties,
                PROPKEY_HIBERNATE_CONFIGURATION_CLASS);
        List<String> configFiles = PropertiesUtils.getCommaSeperatedStringsRejectNull(properties,
                PROPKEY_HIBERNATE_CONFIGFILES);
        try {
            Configuration configuration = (Configuration) Class.forName(configurationClassName).newInstance();
            for (String configFile : configFiles) {
                configuration.configure(configFile);
            }
            // Hook method to perform extra configuration
            performExtraHibernateConfiguration(test, configuration);
            return configuration;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid configuration class " + configurationClassName, e);
        }
    }

    private void performExtraHibernateConfiguration(Object test, Configuration configuration) {
        Method[] methods = test.getClass().getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(HibernateConfiguration.class) != null) {
                try {
                    method.invoke(test, configuration);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void beforeTestMethod(Object test, String methodName) {
         // Nothing to do in particular (datafile loading is performed by DatabaseTestModule)
    }

    public Class[] getModulesDependingOn() {
        return new Class[] {DatabaseTestModule.class};
    }
}
