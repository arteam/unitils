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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import static org.hibernate.cfg.Environment.CONNECTION_PROVIDER;
import org.unitils.core.UnitilsException;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import org.unitils.util.ReflectionUtils;
import static org.unitils.util.ReflectionUtils.invokeMethod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A class for storing and creating Hibernate configurations and session factories.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateConfigurationManager {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(HibernateConfigurationManager.class);

    /**
     * All created hibernate configurations per class
     */
    protected Map<Class<?>, Configuration> hibernateConfigurations = new HashMap<Class<?>, Configuration>();

    /**
     * All created session factories per configuration
     */
    protected Map<Configuration, SessionFactory> hibernateSessionFactories = new HashMap<Configuration, SessionFactory>();

    /* The class name to use when creating a hibernate configuration */
    private String hibernateConfigurationImplClassName;


    /**
     * Creates a config manager that will use the given class name to create new configs when needed.
     *
     * @param configurationImplClassName The class name, not null
     */
    public HibernateConfigurationManager(String configurationImplClassName) {
        this.hibernateConfigurationImplClassName = configurationImplClassName;
    }


    /**
     * Gets the hibernate configuration for the given test.
     * This method first will check whether an existing configuration can be reused and if so, returns that config.
     * A config can be reused, if it is created and there are no {@link HibernateConfiguration} annotations to
     * force the creation of a new one. For example, suppose a config was create for a superclass and the
     * subclass has a {@link HibernateConfiguration} annotation specifying a new location, a new config will need
     * to be created for this subclass. If the subclass did not specify a location, the superclass config can
     * be reused and will thus be returned.
     * <p/>
     * Creating a new config is done following steps:
     * <ul>
     * <li>First find all locations specified in {@link HibernateConfiguration} annotation</li>
     * <li>Create a <code>Configuration</code> using the className provided at construction</li>
     * <li>Load all locations into this config. Subclasses override settings of superclasses. In the same class method level
     * overrides field level which in turn overrrides class level settings</li>
     * <li>Call all custom create methods, passing the current config if requested. The returned config is taken as current config.</li>
     * </ul>
     *
     * @param testObject The test object, not null
     * @return The Hibernate configuration, not null
     */
    public Configuration getHibernateConfiguration(Object testObject) {
        // find class level for wich a new config should be created
        Class<?> testClass = findClassLevelForCreateHibernateConfiguration(testObject.getClass());

        // check whether it already exists
        Configuration hibernateConfiguration = hibernateConfigurations.get(testClass);
        if (hibernateConfiguration != null) {
            return hibernateConfiguration;
        }

        // create hibernate configuration
        hibernateConfiguration = createHibernateConfiguration(testObject);

        // store hibernate configuration
        hibernateConfigurations.put(testClass, hibernateConfiguration);
        return hibernateConfiguration;
    }


    /**
     * Gets the hibernate session factory for the given test.
     * If a session factory already exists, this one is returned, else a new one is constructed using the
     * configuration for the given test.
     *
     * @param testObject The test object, not null
     * @return The Hibernate session factory, not null
     */
    public SessionFactory getHibernateSessionFactory(Object testObject) {
        // check whether it already exists
        Configuration hibernateConfiguration = getHibernateConfiguration(testObject);
        SessionFactory hibernateSessionFactory = hibernateSessionFactories.get(hibernateConfiguration);
        if (hibernateSessionFactory != null) {
            return hibernateSessionFactory;
        }

        // create session factory
        hibernateSessionFactory = hibernateConfiguration.buildSessionFactory();

        // store session factory
        hibernateSessionFactories.put(hibernateConfiguration, hibernateSessionFactory);
        return hibernateSessionFactory;
    }


    /**
     * Forces the reloading of the hibernate configurations the next time that it is requested. If classes are given
     * only hibernate configurations that are linked to those classes will be reset. If no classes are given, all cached
     * hibernate configurations will be reset.
     *
     * @param classes The classes for which to reset the configs
     */
    public void invalidateHibernateConfiguration(Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            hibernateConfigurations.clear();
            return;
        }
        for (Class<?> clazz : classes) {
            hibernateConfigurations.remove(clazz);
        }
    }


    /**
     * Creates a configured Hibernate <code>Configuration</code> object.
     * This will first retrieve all locations specified in {@link HibernateConfiguration} annotations. If a locations
     * were specified, a new config is created. Next, all custom create hibernate configuration methods are invoked
     * passing the created config if requested (null if no context was created yet). If there still was no config
     * created yet, a default hibernate configuration will be created, using hibernate.cfg.xml as config file.
     * <p/>
     * Once a configuration is loaded, the property 'hibernate.connection.provider_class' will be overwritten so that Hibernate
     * will load the {@link HibernateConnectionProvider} as provider. This way we can make sure that Hibernate will use
     * the unitils datasource and thus connect to the unit test database.
     *
     * @param testObject The test object, not null
     * @return The Hibernate configuration, not null
     */
    protected Configuration createHibernateConfiguration(Object testObject) {
        Configuration hibernateConfiguration = null;

        // create hibernate configuration for locations
        List<String> locations = new ArrayList<String>();
        getHibernateConfigurationLocations(testObject, testObject.getClass(), locations);
        if (!locations.isEmpty()) {
            hibernateConfiguration = createHibernateConfigurationForLocations(locations.toArray(new String[0]));
        }

        // call custom create methods
        hibernateConfiguration = invokeHibernateConfigurationMethods(testObject, hibernateConfiguration);

        // check no hibernate configuration was created
        // create configuration using hibernate defaults
        if (hibernateConfiguration == null) {
            hibernateConfiguration = createHibernateConfigurationForLocations(null);
        }

        // configure hibernate to use unitils datasource
        if (hibernateConfiguration.getProperty(CONNECTION_PROVIDER) != null) {
            logger.warn("The property " + CONNECTION_PROVIDER + " is present in your Hibernate configuration. " +
                    "This property will be overwritten with Unitils own ConnectionProvider implementation!");
        }
        Properties connectionProviderProperty = new Properties();
        connectionProviderProperty.setProperty(CONNECTION_PROVIDER, HibernateConnectionProvider.class.getName());
        hibernateConfiguration.addProperties(connectionProviderProperty);
        return hibernateConfiguration;
    }


    /**
     * Gets all locations specified in {@link HibernateConfiguration} annotations for the given testClass
     * and all its superclasses.
     *
     * @param testObject The test object, not null
     * @param testClass  The current class in the hierarchy, can be null
     * @param result     The list to which the locations will be added, not null
     */
    protected void getHibernateConfigurationLocations(Object testObject, Class<?> testClass, List<String> result) {
        // nothing to do (ends the recursion)
        if (testClass == null || testClass == Object.class) {
            return;
        }

        // add locations of super classes  
        getHibernateConfigurationLocations(testObject, testClass.getSuperclass(), result);

        // get class level locations
        String[] classLevelLocations = getLocations(testClass.getAnnotation(HibernateConfiguration.class));
        if (classLevelLocations != null) {
            result.addAll(Arrays.asList(classLevelLocations));
        }
        // get field level locations
        List<Field> fields = getFieldsAnnotatedWith(testClass, HibernateConfiguration.class);
        for (Field field : fields) {
            String[] fieldLevelLocations = getLocations(field.getAnnotation(HibernateConfiguration.class));
            if (fieldLevelLocations != null) {
                result.addAll(Arrays.asList(fieldLevelLocations));
            }
        }
        // get method level locations
        List<Method> methods = getMethodsAnnotatedWith(testClass, HibernateConfiguration.class, false);
        for (Method method : methods) {
            String[] methodLevelLocations = getLocations(method.getAnnotation(HibernateConfiguration.class));
            if (methodLevelLocations != null) {
                result.addAll(Arrays.asList(methodLevelLocations));
            }
        }
    }


    /**
     * Creates a configuration by calling all methods annotated with @HibernateConfiguration.
     * These methods should have one of following exact signatures:
     * <ul>
     * <li>Configuration createMethodName() or</li>
     * <li>Configuration createMethodName(Configuration configuration)</li>
     * </ul>
     * The second version receives the current created hibernate configuration, for example one that was created by a class or
     * superclasses @HibernateConfiguration).
     * They both should return a configuration, either a new one or the given parent context (after for
     * example changing some values).
     *
     * @param testObject             The test object, not null
     * @param hibernateConfiguration The current hibernate configuration
     * @return The new hibernate configuration or the current hibernate configuration
     */
    protected Configuration invokeHibernateConfigurationMethods(Object testObject, Configuration hibernateConfiguration) {
        // get all annotated methods, superclass methods included
        Class<?> testClass = testObject.getClass();
        List<Method> methods = getMethodsAnnotatedWith(testClass, HibernateConfiguration.class, true);
        for (Method method : methods) {
            // do not invoke setter methods
            if (method.getReturnType() == Void.TYPE) {
                continue;
            }
            if (!isCreateHibernateConfigurationMethod(method)) {
                throw new UnitilsException("Unable to invoke method annotated with @" + HibernateConfiguration.class.getSimpleName() +
                        ". Ensure that this method has following signature: " + Configuration.class.getName() + " myMethod(" + Configuration.class.getName() + " context) or " +
                        Configuration.class.getName() + " myMethod()");
            }
            try {
                // call method
                if (method.getParameterTypes().length == 0) {
                    hibernateConfiguration = invokeMethod(testObject, method);
                } else {
                    hibernateConfiguration = invokeMethod(testObject, method, hibernateConfiguration);
                }
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Method " + testClass.getSimpleName() + "." + methods.get(0).getName() +
                        " (annotated with " + HibernateConfiguration.class.getSimpleName() + ") has thrown an exception", e.getCause());
            }
        }
        return hibernateConfiguration;
    }


    /**
     * Creates a hibernate configuration for the given locations. If no locations are specified, the hibernate default
     * (hibernate.cfg.xml) will be used. Which type of configuration will be created depends on the implementation
     * class that was specified upon construction. A UnitilsException is thrown if the configuration could not be loaded.
     *
     * @param locations the file locations
     * @return The configuration, not null
     */
    protected Configuration createHibernateConfigurationForLocations(String[] locations) {
        try {
            // create instance
            Configuration configuration = ReflectionUtils.createInstanceOfType(hibernateConfigurationImplClassName);

            // load default configuration if no locations were specified
            if (locations == null) {
                configuration.configure();
                return configuration;
            }
            // load specified configurations
            for (String location : locations) {
                configuration.configure(location);
            }
            return configuration;

        } catch (Exception e) {
            throw new UnitilsException("Unable to create hibernate configuration for locations: " + Arrays.toString(locations), e);

        }
    }

    /**
     * Finds the level in the class hierarchy for which a hibernate configuration should
     * be created. That is, a class level that contains a custom create method or specifies
     * a location in one of the {@link HibernateConfiguration} annotations. Such a level should
     * have its own hibernate configuration and cannot reuse the config of a superclass.
     * <p/>
     * If a class only contains {@link HibernateConfiguration} annotations without locations (for example
     * for injecting the configuration into a field), the superclasses will be checked untill a level
     * is found for which a config should be created. If no level is found, null is returned.
     *
     * @param testClass The current level in the hierarchy
     * @return The level for which a config should created, null if none found
     */
    protected Class<?> findClassLevelForCreateHibernateConfiguration(Class<?> testClass) {
        // nothing to do (ends the recursion)
        if (testClass == null || testClass == Object.class) {
            return null;
        }

        // check class level locations
        if (getLocations(testClass.getAnnotation(HibernateConfiguration.class)) != null) {
            return testClass;
        }

        // check field level locations
        List<Field> fields = getFieldsAnnotatedWith(testClass, HibernateConfiguration.class);
        for (Field field : fields) {
            if (getLocations(field.getAnnotation(HibernateConfiguration.class)) != null) {
                return testClass;
            }
        }

        // check custom create methods and method level locations
        List<Method> methods = getMethodsAnnotatedWith(testClass, HibernateConfiguration.class, false);
        for (Method method : methods) {
            if (isCreateHibernateConfigurationMethod(method)) {
                return testClass;
            }
            if (getLocations(method.getAnnotation(HibernateConfiguration.class)) != null) {
                return testClass;
            }
        }
        // nothing found on this level, check superclass
        return findClassLevelForCreateHibernateConfiguration(testClass.getSuperclass());
    }


    /**
     * Checks whether the given method is a custom create method.
     * A custom create method must have following signature:
     * <ul>
     * <li>Configuration createMethodName() or</li>
     * <li>Configuration createMethodName(Configuration config)</li>
     * </ul>
     *
     * @param method The method, not null
     * @return True if it has the correct signature
     */
    protected boolean isCreateHibernateConfigurationMethod(Method method) {
        Class<?>[] argumentTypes = method.getParameterTypes();
        if (argumentTypes.length > 1) {
            return false;
        }
        if (argumentTypes.length == 1 && argumentTypes[0] != Configuration.class) {
            return false;
        }
        return (method.getReturnType() == Configuration.class);
    }


    /**
     * Gets the locations that are specified for the given annotation. If the annotation is null or
     * if no locations were specified, null will be returned. An array with 1 empty string will
     * also be considered to be empty.
     *
     * @param annotation The annotation
     * @return The locations, null if no locations were specified
     */
    protected String[] getLocations(HibernateConfiguration annotation) {
        if (annotation == null) {
            return null;
        }
        String[] locations = annotation.value();
        if (locations.length == 0 || (locations.length == 1 && StringUtils.isEmpty(locations[0]))) {
            return null;
        }
        return locations;
    }

}
