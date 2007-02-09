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
 * todo javadoc
 * <p/>
 * A class for storing and creating Hibernate configurations and session factories.
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

    /* todo javadoc */
    private boolean manageCurrentSessionContext;


    /**
     * Creates a config manager that will use the given class name to create new configs when needed.
     *
     * @param configurationImplClassName  The class name, not null
     * @param manageCurrentSessionContext todo javadoc
     */
    public SessionFactoryManager(String configurationImplClassName, boolean manageCurrentSessionContext) {
        super(Configuration.class, HibernateSessionFactory.class);
        this.configurationImplClassName = configurationImplClassName;
        this.manageCurrentSessionContext = manageCurrentSessionContext;
    }


    /**
     * Gets the hibernate configuration for the given test.
     * This method first will check whether an existing configuration can be reused and if so, returns that config.
     * A config can be reused, if it is created and there are no {@link HibernateSessionFactory} annotations to
     * force the creation of a new one. For example, suppose a config was create for a superclass and the
     * subclass has a {@link HibernateSessionFactory} annotation specifying a new location, a new config will need
     * to be created for this subclass. If the subclass did not specify a location, the superclass config can
     * be reused and will thus be returned.
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
     * @param testObject The test object, not null
     * @return The Hibernate configuration, not null
     */
    public Configuration getConfiguration(Object testObject) {
        return getInstance(testObject);
    }


    /**
     * Gets the hibernate session factory for the given test.
     * If a session factory already exists, this one is returned, else a new one is constructed using the
     * configuration for the given test.
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
     * Forces the reloading of the session factory the next time that it is requested. If classes are given
     * only session factories that are linked to those classes will be reset. If no classes are given, all cached
     * session factories will be reset.
     *
     * @param classes The classes for which to reset the configs
     */
    public void invalidateSessionFactory(Class<?>... classes) {
        invalidateInstance(classes);       
        //todo also remove sessionfactory
    }


    /**
     * Creates a configured Hibernate <code>Configuration</code> object.
     * This will first retrieve all locations specified in {@link HibernateSessionFactory} annotations. If a locations
     * were specified, a new config is created. Next, all custom create hibernate configuration methods are invoked
     * passing the created config if requested (null if no context was created yet). If there still was no config
     * created yet, a default hibernate configuration will be created, using hibernate.cfg.xml as config file.
     * <p/>
     * Once a configuration is loaded, the property 'hibernate.connection.provider_class' will be overwritten so that Hibernate
     * will load the {@link HibernateConnectionProvider} as provider. This way we can make sure that Hibernate will use
     * the unitils datasource and thus connect to the unit test database.
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


    protected Configuration createInstanceForValues(List<String> values) {
        try {
            // create instance
            Configuration configuration = createInstanceOfType(configurationImplClassName);

            // load default configuration if no locations were specified
            if (values == null || values.isEmpty()) {
                configuration.configure();
                return configuration;
            }
            // load specified configurations
            for (String value : values) {
                configuration.configure(value);
            }
            return configuration;

        } catch (Exception e) {
            throw new UnitilsException("Unable to create hibernate configuration for locations: " + values, e);
        }
    }


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
                         