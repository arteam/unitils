package be.ordina.unitils.hibernate;

import be.ordina.unitils.dbunit.DatabaseTestModule;
import be.ordina.unitils.module.BaseUnitilsModule;
import be.ordina.unitils.util.PropertiesUtils;
import be.ordina.unitils.util.UnitilsConfiguration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.hibernate.cfg.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

/**
 * @author Filip Neven
 */
public class HibernateTestModule extends BaseUnitilsModule {

    private static final String PROPKEY_HIBERNATE_CONFIGFILES = "hibernatetestcase.hibernate.cfg.configfiles";

    private static final String PROPKEY_HIBERNATE_CONFIGURATION_CLASS = "hibernatetestcase.hibernate.cfg.configurationclass";

    /* The configuration (unittest.properties) */
    protected static Properties properties;

    private static boolean firstTime;

    public void beforeAll() {
        //Todo refactor
        properties = ConfigurationConverter.getProperties(UnitilsConfiguration.getInstance());

        firstTime = true;
    }

    public void beforeTestClass(Object test) throws Exception {
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
        return new Class[]{DatabaseTestModule.class};
    }
}
