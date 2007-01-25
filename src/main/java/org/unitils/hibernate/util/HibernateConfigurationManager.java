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
import static org.unitils.util.ReflectionUtils.isSetter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * todo javadoc
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
     * @param hibernateConfigurationImplClassName
     *         The class name, not null
     */
    public HibernateConfigurationManager(String hibernateConfigurationImplClassName) {
        this.hibernateConfigurationImplClassName = hibernateConfigurationImplClassName;
    }


    //todo javadoc
    public Configuration getHibernateConfiguration(Object testObject) {
        // check whether it already exists
        Class<?> testClass = testObject.getClass();
        Configuration hibernateConfiguration = hibernateConfigurations.get(testClass);
        if (hibernateConfiguration != null) {
            return hibernateConfiguration;
        }

        // create configuration
        hibernateConfiguration = createHibernateConfiguration(testObject);

        // store hibernate configuration
        hibernateConfigurations.put(testClass, hibernateConfiguration);
        return hibernateConfiguration;
    }


    // todo javadoc
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
     * Creates completely configured Hibernate <code>Configuration</code> object.
     * <p/>
     * todo javadoc
     * <p/>
     * Unitils' own implementation of the Hibernate <code>ConnectionProvider</code>, {@link HibernateConnectionProvider},
     * is set as connection provider. This object makes sure that Hibernate uses connections of Unitils' own
     * <code>DataSource</code>.
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


    //todo javadoc
    protected void getHibernateConfigurationLocations(Object testObject, Class<?> testClass, List<String> result) {

        // nothing to do (ends the recursion)
        if (testClass == null || testClass == Object.class) {
            return;
        }

        // get locations of super classes
        getHibernateConfigurationLocations(testObject, testClass.getSuperclass(), result);

        // get locations for class annotation
        HibernateConfiguration hibernateConfigurationAnnotation = testClass.getAnnotation(HibernateConfiguration.class);
        if (hibernateConfigurationAnnotation != null) {
            String[] locations = hibernateConfigurationAnnotation.value();
            if (locations != null && locations.length > 0 && !(locations.length == 1 && StringUtils.isEmpty(locations[0]))) {
                result.addAll(Arrays.asList(locations));
            }
        }

        // get locations for annotated field
        List<Field> fields = getFieldsAnnotatedWith(testClass, HibernateConfiguration.class);
        for (Field field : fields) {
            hibernateConfigurationAnnotation = field.getAnnotation(HibernateConfiguration.class);
            String[] locations = hibernateConfigurationAnnotation.value();
            if (locations != null && locations.length > 0 && !(locations.length == 1 && StringUtils.isEmpty(locations[0]))) {
                result.addAll(Arrays.asList(locations));
            }
        }

        // get locations for annotated methods
        List<Method> methods = getMethodsAnnotatedWith(testClass, HibernateConfiguration.class, false);
        for (Method method : methods) {
            hibernateConfigurationAnnotation = method.getAnnotation(HibernateConfiguration.class);
            String[] locations = hibernateConfigurationAnnotation.value();
            if (locations != null && locations.length > 0 && !(locations.length == 1 && StringUtils.isEmpty(locations[0]))) {
                result.addAll(Arrays.asList(locations));
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
        Class<?> testClass = testObject.getClass();
        List<Method> methods = getMethodsAnnotatedWith(testClass, HibernateConfiguration.class, true);
        for (Method method : methods) {
            // do not invoke setter methods
            if (isSetter(method)) {
                continue;
            }
            Class<?>[] argumentTypes = method.getParameterTypes();
            if (argumentTypes.length > 1 || (argumentTypes.length == 1 && argumentTypes[0] != Configuration.class) || method.getReturnType() != Configuration.class) {
                throw new UnitilsException("Unable to invoke method annotated with @" + HibernateConfiguration.class.getSimpleName() +
                        ". Ensure that this method has following signature: " + Configuration.class.getName() + " myMethod(" + Configuration.class.getName() + " context) or " +
                        Configuration.class.getName() + " myMethod()");
            }
            try {
                // call method
                if (argumentTypes.length == 0) {
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

}
