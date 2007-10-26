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
package org.unitils.jpa.util;

import static org.hibernate.cfg.Environment.CONNECTION_PROVIDER;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.invokeMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ejb.Ejb3Configuration;
import org.unitils.core.UnitilsException;
import org.unitils.hibernate.util.HibernateConnectionProvider;
import org.unitils.hibernate.util.SessionFactoryManager;

public class HibernateEntityManagerFactoryManager extends
		EntityManagerFactoryManager {


	/* The logger instance for this class */
    private static Log logger = LogFactory.getLog(SessionFactoryManager.class);

    /**
     * All created session factories per configuration
     */
    protected Map<EntityManagerInterceptingEntityManagerFactory, Ejb3Configuration> configurations = new HashMap<EntityManagerInterceptingEntityManagerFactory, Ejb3Configuration>();


    /**
     * Gets the hibernate EJB3 configuration for the given test. A UnitilsException will be thrown if no configuration
     * could be retrieved or created.
     *
     * @param testObject The test object, not null
     * @return The hibernate EJB3 configuration, not null
     */
    public Ejb3Configuration getConfiguration(Object testObject) {
    	// Check if a SessionFactory has been configured
    	EntityManagerInterceptingEntityManagerFactory entityManagerFactory = getEntityManagerFactory(testObject);
        if (entityManagerFactory == null) {
            return null;
        }
        
        return configurations.get(entityManagerFactory);
    }


    /**
     * Forces the reloading of the session factory and configurations the next time that it is requested. If classes
     * are given only session factories and configurations that are linked to those classes will be reset. If no
     * classes are given, all cached session factories and configurations will be reset.
     *
     * @param classes The classes for which to reset the factories and configs
     */
    public void invalidateEntityManagerFactory(Class<?>... classes) {
        // remove all session factories
        if (classes == null || classes.length == 0) {
            configurations.clear();

        } else {
            for (Class<?> clazz : classes) {
            	EntityManagerInterceptingEntityManagerFactory entityManagerFactory = instances.get(clazz);
            	Ejb3Configuration configuration = configurations.get(entityManagerFactory);
                configurations.remove(configuration);
            }
        }
        // remove all configurations
        invalidateInstance(classes);
    }


    /**
     * Creates a configured Hibernate <code>Ejb3Configuration</code> object.
     * <p/>
     * Once a configuration is loaded, the property 'hibernate.connection.provider_class' will be overwritten so that
     * Hibernate will load the {@link HibernateConnectionProvider} as connection provider. This way we can make sure
     * that Hibernate will use the unitils datasource and thus connect to the unit test database.
     *
     * @param configuration The Hibernate <code>Ejb3Configuration</code>, not null
     * @param testObject    The test object, not null
     * @param testClass     The level in the hierarchy
     */
    protected void postProcessConfiguration(Ejb3Configuration configuration, Object testObject, Class<?> testClass) {
        // configure hibernate to use the unitils datasource
        Properties unitilsHibernateProperties = new Properties();
        if (configuration.getProperties().getProperty(CONNECTION_PROVIDER) != null) {
            logger.warn("The property " + CONNECTION_PROVIDER + " is present in your (hibernate powered) JPA configuration. This property will be overwritten with Unitils own ConnectionProvider implementation!");
        }
        unitilsHibernateProperties.setProperty(CONNECTION_PROVIDER, HibernateConnectionProvider.class.getName());

        // if enabled, configure hibernate's current session management
        configuration.addProperties(unitilsHibernateProperties);
    }


    /**
     * Creates a new configuration for the given locations. The configuration implementation class name provided at
     * construction time, determines what type of instance will be created.
     * @param locations The locations where to find configuration files, not null
     *
     * @return the configuration, not null
     */
    @Override
    protected EntityManagerInterceptingEntityManagerFactory createInstanceForValues(Object testObject, Class<?> testClass, List<String> locations) {
        // Create a hibernate Ejb3Configuration object from the specfied configuration files
    	Ejb3Configuration configuration = createConfigurationFromConfigFiles(locations);
    	
        // invoke custom initialization method
        invokeCustomInitializationMethod(testObject, testClass, configuration);
        
        // Prepare the Configuration object to be used for testing purposes: connect to the test database, etc.
        postProcessConfiguration(configuration, testObject, testClass);
        
        // Create a SessionFactory
        EntityManagerInterceptingEntityManagerFactory entityManagerFactory = new EntityManagerInterceptingEntityManagerFactory(configuration.buildEntityManagerFactory());
        
        // Make sure we can later retrieve the Configuration object by the SessionFactory object
        configurations.put(entityManagerFactory, configuration);
        return entityManagerFactory;
    }


    protected Ejb3Configuration createConfigurationFromConfigFiles(List<String> locations) {
    	try {
            // create instance
    		Ejb3Configuration configuration = new Ejb3Configuration();

            // load default configuration if no locations were specified
            if (locations.isEmpty()) {
            	configuration.configure("META-INF/persistence.xml", new HashMap());
                return configuration;
            }
            // load custom specified persistence unit configuration file. Only one such file is supported, since a hibernate EntityManager can only be configured using 
            // one JPA standard persistence unit config files
            if (locations.size() > 1) { // This normally never occurs
            	throw new UnitilsException("Only one persistence unit config file can be specified");
            }
            // There is exacltly one persistence unit config file
            configuration.configure(locations.get(0), new HashMap());
            
            return configuration;

        } catch (Exception e) {
            throw new UnitilsException("Unable to create hibernate configuration for locations: " + locations, e);
        }
	}


    /**
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
    protected void invokeCustomInitializationMethod(Object testObject, Class<?> testClass, Ejb3Configuration configuration) {
        // get all annotated methods from the given test class, superclasses included
        List<Method> methods = getMethodsAnnotatedWith(testClass, annotationClass, true);
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (method.getReturnType() != Void.TYPE) {
                //do not invoke custom create methods
                continue;
            }
            if (parameterTypes.length == 1 && EntityManagerFactory.class.isAssignableFrom(parameterTypes[0])) {
                // do not invoke session factory setter methods
                continue;
            }
            if (parameterTypes.length != 1 || !Ejb3Configuration.class.isAssignableFrom(parameterTypes[0])) {
                throw new UnitilsException("Unable to invoke method annotated with @" + annotationClass.getSimpleName() +
                        ". Ensure that this method has following signature: void myMethod(" + Ejb3Configuration.class.getSimpleName() + 
                        " configuration)");
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
    
    
    @Override
	protected EntityManagerInterceptingEntityManagerFactory createCustomCreatedInstanceFromCustomCreateMethodResult(
			Object testObject, Class<?> testClass, Object customCreateMethodResult) {
		
    	Ejb3Configuration configuration = (Ejb3Configuration) customCreateMethodResult;
    	invokeCustomInitializationMethod(testObject, testClass, configuration);
    	EntityManagerInterceptingEntityManagerFactory entityManagerFactory = new EntityManagerInterceptingEntityManagerFactory(configuration.buildEntityManagerFactory());
		configurations.put(entityManagerFactory, configuration);
    	return entityManagerFactory;
	}


	/**
     * @return The hibernate <code>Configuration</code> type. This to make sure that a custom create method has to return
     * an object of type <code>Configuration</code>, not of type <code>SessionFactory</code>
     */
	@Override
	protected Class<?> getCustomCreateMethodReturnType() {
		return Ejb3Configuration.class;
	}

}
