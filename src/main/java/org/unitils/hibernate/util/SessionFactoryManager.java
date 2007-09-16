/*
 * Copyright 2006-2007,  Unitils.org
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
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import static org.hibernate.cfg.Environment.CONNECTION_PROVIDER;
import static org.hibernate.cfg.Environment.CURRENT_SESSION_CONTEXT_CLASS;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.AnnotatedInstanceManager;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;
import static org.unitils.util.ReflectionUtils.invokeMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    /* The class name of the CurrentSessionContext, null for no context */
    private String currentSessionContextImplClassName;


    /**
     * Creates a config manager that will use the given class name to create new configs when needed.
     *
     * @param configurationImplClassName The class name, not null
     * @param currentSessionContextImplClassName
     *                                   The class name of the CurrentSessionContext, null for no context
     */
    public SessionFactoryManager(String configurationImplClassName, String currentSessionContextImplClassName) {
        super(Configuration.class, HibernateSessionFactory.class);
        this.configurationImplClassName = configurationImplClassName;
        this.currentSessionContextImplClassName = currentSessionContextImplClassName;
    }


    /**
     * Gets the hibernate session factory for the given test as described in the class javadoc. A UnitilsException will
     * be thrown if no configuration could be retrieved or created.
     *
     * @param testObject The test object, not null
     * @return The Hibernate session factory, not null
     */
    public SessionInterceptingSessionFactory getSessionFactory(Object testObject) {
        // Check if a SessionFactory has been configured
        Configuration configuration = getConfiguration(testObject);
        if (configuration == null) {
            return null;
        }

        // check whether a SessionFactory was already created before based on this hibernate Configuration
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
     * @param configuration The Hibernate configuration, not null
     * @param testObject    The test object, not null
     * @param testClass     The level in the hierarchy
     */
    @Override
    protected void afterInstanceCreate(Configuration configuration, Object testObject, Class<?> testClass) {
        // invoke custom initialization method 
        invokeInitializationMethod(testObject, testClass, configuration);

        // configure hibernate to use unitils datasource
        Properties unitilsHibernateProperties = new Properties();
        if (configuration.getProperty(CONNECTION_PROVIDER) != null) {
            logger.warn("The property " + CONNECTION_PROVIDER + " is present in your Hibernate configuration. This property will be overwritten with Unitils own ConnectionProvider implementation!");
        }
        unitilsHibernateProperties.setProperty(CONNECTION_PROVIDER, HibernateConnectionProvider.class.getName());

        // if enabled, configure hibernate's current session management
        if (currentSessionContextImplClassName != null) {
            if (configuration.getProperty(CURRENT_SESSION_CONTEXT_CLASS) != null) {
                logger.warn("The property " + CURRENT_SESSION_CONTEXT_CLASS + " is present in your Hibernate " +
                        "configuration. This property will be overwritten with Unitils own CurrentSessionContext implementation!");
            }
            unitilsHibernateProperties.setProperty(CURRENT_SESSION_CONTEXT_CLASS, currentSessionContextImplClassName);
        }
        configuration.addProperties(unitilsHibernateProperties);
    }


    /**
     * Creates a new configuration for the given locations. The configuration implementation class name provided at
     * construction time, determines what type of instance will be created.
     *
     * @param locations The locations where to find configuration files, not null
     * @return the configuration, not null
     */
    @Override
    protected Configuration createInstanceForValues(List<String> locations) {
        try {
            // create instance
            Configuration configuration = createInstanceOfType(configurationImplClassName, false);

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
     * Gets the locations that are specified for the given {@link HibernateSessionFactory} annotation. An array with
     * 1 empty string should be considered to be empty and null should be returned.
     *
     * @param annotation The annotation, not null
     * @return The locations, null if no values were specified
     */
    @Override
    protected List<String> getAnnotationValues(HibernateSessionFactory annotation) {
        String[] locations = annotation.value();
        if (locations.length == 0 || (locations.length == 1 && isEmpty(locations[0]))) {
            return null;
        }
        return asList(locations);
    }


    /**
     * todo javadoc
     * <p/>
     * Creates an instance by calling a custom create method (if there is one). Such a create method should have one of
     * following exact signatures:
     * <ul>
     * <li>Configuration createMethodName() or</li>
     * <li>Configuration createMethodName(List<String> locations)</li>
     * </ul>
     * The second version receives the given locations. They both should return an instance (not null)
     * <p/>
     * If no create method was found, null is returned. If there is more than 1 create method found, an exception is raised.
     *
     * @param testObject    The test object, not null
     * @param testClass     The level in the hierarchy
     * @param configuration The configuration to initialize, not null
     */
    @SuppressWarnings({"unchecked"})
    protected void invokeInitializationMethod(Object testObject, Class<?> testClass, Configuration configuration) {
        // get all annotated methods from the given test class, superclasses included
        List<Method> methods = getMethodsAnnotatedWith(testClass, annotationClass, true);
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (method.getReturnType() != Void.TYPE) {
                //do not invoke custom create methods
                continue;
            }
            if (parameterTypes.length == 1 && SessionFactory.class.isAssignableFrom(parameterTypes[0])) {
                // do not invoke session factory setter methods
                continue;
            }
            if (parameterTypes.length != 1 || !Configuration.class.isAssignableFrom(parameterTypes[0])) {
                throw new UnitilsException("Unable to invoke method annotated with @" + annotationClass.getSimpleName() +
                        ". Ensure that this method has following signature: void myMethod( Configuration configuration )");
            }
            try {
                // call method
                invokeMethod(testObject, method, configuration);

            } catch (InvocationTargetException e) {
                throw new UnitilsException("Method " + testClass.getSimpleName() + "." + methods.get(0).getName() +
                        " (annotated with " + annotationClass.getSimpleName() + ") has thrown an exception", e.getCause());
            }
        }
    }
}
                         