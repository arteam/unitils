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
package org.unitils.hibernate.util;

import static org.apache.commons.lang.StringUtils.isEmpty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import static org.hibernate.cfg.Environment.CONNECTION_PROVIDER;
import static org.hibernate.cfg.Environment.CURRENT_SESSION_CONTEXT_CLASS;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.AnnotatedInstanceManager;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

import java.util.*;
import static java.util.Arrays.asList;

/**
 * A class for managing and creating Hibernate configurations and session factories.
 * todo javadoc
 * <p/>
 * Creating a new config is done following steps:
 * <ul>
 * <li>First find all locations specified in {@link HibernateSessionFactory} annotation</li>
 * <li>Create a <code>Configuration</code> using the className provided at construction</li>
 * <li>Load all locations into this config. Subclasses override settings of superclasses. In the same class method level
 * overrides field level which in turn overrrides class level settings</li>
 * <li>Call all custom create methods, passing the current config if requested. The returned config is taken as current config.</li>
 * </ul>
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SessionFactoryManager extends AnnotatedInstanceManager<Configuration, HibernateSessionFactory> {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(SessionFactoryManager.class);

    /**
     * All created session factories per configuration
     */
    protected Map<Configuration, SessionInterceptingSessionFactory> sessionFactories = new HashMap<Configuration, SessionInterceptingSessionFactory>();

    /* The class name to use when creating a hibernate configuration */
    private String configurationImplClassName;

    /* True if the HibernateCurrentSessionContext should be registered */
    private boolean manageCurrentSessionContext;


    /**
     * Creates a config manager that will use the given class name to create new configs when needed.
     *
     * @param configurationImplClassName  The class name, not null
     * @param manageCurrentSessionContext True if the {@link HibernateCurrentSessionContext} should be registered
     */
    public SessionFactoryManager(String configurationImplClassName, boolean manageCurrentSessionContext) {
        super(Configuration.class, HibernateSessionFactory.class);
        this.configurationImplClassName = configurationImplClassName;
        this.manageCurrentSessionContext = manageCurrentSessionContext;
    }


    /**
     * Gets the hibernate session factory for the given test as described in the class javadoc. A UnitilsException will
     * be thrown if no configuration could be retrieved or created.
     *
     * @param testObject The test object, not null
     * @return The Hibernate session factory, not null
     */
    public SessionInterceptingSessionFactory getSessionFactory(Object testObject) {
        // check whether it already exists
        Configuration configuration = getConfiguration(testObject);
        SessionInterceptingSessionFactory sessionFactory = sessionFactories.get(configuration);
        if (sessionFactory != null) {
            return sessionFactory;
        }

        // create session factory
        sessionFactory = new SessionInterceptingSessionFactory(configuration.buildSessionFactory());

        // store session factory
        sessionFactories.put(configuration, sessionFactory);
        return sessionFactory;
    }


    /**
     * Gets the hibernate configuration for the given test. A UnitilsException will be thrown if no configuration
     * could be retrieved or created.
     *
     * @param testObject The test object, not null
     * @return The Hibernate configuration, not null
     */
    public Configuration getConfiguration(Object testObject) {
        return getInstance(testObject);
    }


    //todo javadoc
    public void registerSessionFactory(Class<?> testClass, SessionInterceptingSessionFactory sessionInterceptingSessionFactory, Configuration configuration) {
        registerInstance(testClass, configuration);
        sessionFactories.put(configuration, sessionInterceptingSessionFactory);
    }


    /**
     * Gets all existing hibernate session factories. This will not create any session factories.
     *
     * @return The Hibernate session factories, not null
     */
    public List<SessionInterceptingSessionFactory> getSessionFactories() {
        return new ArrayList<SessionInterceptingSessionFactory>(sessionFactories.values());
    }


    /**
     * Forces the reloading of the session factory and configurations the next time that it is requested. If classes
     * are given only session factories and configurations that are linked to those classes will be reset. If no
     * classes are given, all cached session factories and configurations will be reset.
     *
     * @param classes The classes for which to reset the factories and configs
     */
    public void invalidateSessionFactory(Class<?>... classes) {
        // remove all session factories
        if (classes == null || classes.length == 0) {
            sessionFactories.clear();

        } else {
            for (Class<?> clazz : classes) {
                Configuration configuration = instances.get(clazz);
                sessionFactories.remove(configuration);
            }
        }
        // remove all configurations
        invalidateInstance(classes);
    }


    /**
     * Creates a configured Hibernate <code>Configuration</code> object.
     * <p/>
     * Once a configuration is loaded, the property 'hibernate.connection.provider_class' will be overwritten so that
     * Hibernate will load the {@link HibernateConnectionProvider} as connection provider. This way we can make sure
     * that Hibernate will use the unitils datasource and thus connect to the unit test database.
     *
     * @param testObject The test object, not null
     * @param testClass  The level in the hierarchy
     * @return The Hibernate configuration, not null
     */
    @Override
    protected Configuration createInstance(Object testObject, Class<?> testClass) {
        // create instance
        Configuration configuration = super.createInstance(testObject, testClass);

        // configure hibernate to use unitils datasource
        Properties unitilsHibernateProperties = new Properties();
        if (configuration.getProperty(CONNECTION_PROVIDER) != null) {
            logger.warn("The property " + CONNECTION_PROVIDER + " is present in your Hibernate configuration. This property will be overwritten with Unitils own ConnectionProvider implementation!");
        }
        unitilsHibernateProperties.setProperty(CONNECTION_PROVIDER, HibernateConnectionProvider.class.getName());

        // if enabled, configure hibernate's current session management
        if (manageCurrentSessionContext) {
            if (configuration.getProperty(CURRENT_SESSION_CONTEXT_CLASS) != null) {
                logger.warn("The property " + CURRENT_SESSION_CONTEXT_CLASS + " is present in your Hibernate " +
                        "configuration. This property will be overwritten with Unitils own CurrentSessionContext implementation!");
            }
            unitilsHibernateProperties.setProperty(CURRENT_SESSION_CONTEXT_CLASS, HibernateCurrentSessionContext.class.getName());
        }
        configuration.addProperties(unitilsHibernateProperties);
        return configuration;
    }


    /**
     * Creates a new configuration for the given locations. The configuration implementation class name provided at
     * construction time, determines what type of instance will be created.
     *
     * @param locations The locations where to find configuration files, not null
     * @return the configuration, not null
     */
    protected Configuration createInstanceForValues(List<String> locations) {
        try {
            // create instance
            Configuration configuration = createInstanceOfType(configurationImplClassName);

            // load default configuration if no locations were specified
            if (locations == null || locations.isEmpty()) {
                configuration.configure();
                return configuration;
            }
            // load specified configurations
            for (String location : locations) {
                configuration.configure(location);
            }
            return configuration;

        } catch (Exception e) {
            throw new UnitilsException("Unable to create hibernate configuration for locations: " + locations, e);
        }
    }


    /**
     * Gets the locations that are specified for the given {@link HibernateSessionFactory} annotation. If the
     * annotation is null or if no locations were specified, null is returned. An array with 1 empty string is
     * also be considered to be empty.
     *
     * @param annotation The annotation
     * @return The locations, null if no values were specified
     */
    protected List<String> getAnnotationValues(HibernateSessionFactory annotation) {
        if (annotation == null) {
            return null;
        }
        String[] locations = annotation.value();
        if (locations.length == 0 || (locations.length == 1 && isEmpty(locations[0]))) {
            return null;
        }
        return asList(locations);
    }
}
                         