/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.orm.common;

import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.ResourceConfigLoader;
import org.unitils.database.DatabaseModule;
import org.unitils.database.util.Flushable;
import org.unitils.orm.common.spring.OrmSpringSupport;
import org.unitils.orm.common.util.ConfiguredOrmPersistenceUnit;
import org.unitils.orm.common.util.OrmConfig;
import org.unitils.orm.common.util.OrmPersistenceUnitLoader;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;
import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Base module defining common behavior for a module that provides object relational mapping support for tests.
 * This abstract module takes into account loading and caching of persistence units. A persistence unit
 * can be configured using unitils annotations or in a spring <code>ApplicationContext</code>.
 * The persistence unit is injected into the test object's annotated fields. This module also supports
 * flushing of the active persistence context.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @param <ORM_PERSISTENCE_UNIT> Type of the ORM persistence unit
 * @param <ORM_PERSISTENCE_CONTEXT> Type of the ORM persistence context
 * @param <PROVIDER_CONFIGURATION_OBJECT> Type of the implementation specific configuration object
 * @param <PERSISTENCE_UNIT_CONFIG_ANNOTATION> Type of the annotation used for configuring and injecting the persistence unit
 * @param <ORM_CONFIG> Type of the value object extending {@link OrmConfig} that contains all unitils specific persitence unit configuration
 * @param <ORM_PERSISTENCE_UNIT_CONFIGG_LOADER> Subtype of {@link OrmPersistenceUnitLoader} that loads the persistence unit based on the ORM_CONFIG.
 */
abstract public class OrmModule<ORM_PERSISTENCE_UNIT, ORM_PERSISTENCE_CONTEXT, PROVIDER_CONFIGURATION_OBJECT, PERSISTENCE_UNIT_CONFIG_ANNOTATION extends Annotation, ORM_CONFIG extends OrmConfig, ORM_PERSISTENCE_UNIT_CONFIGG_LOADER extends ResourceConfigLoader<ORM_CONFIG>> implements Module, Flushable {

    /**
     * Class that loads the persistence unit configuration
     */
    protected ORM_PERSISTENCE_UNIT_CONFIGG_LOADER persistenceUnitConfigLoader;

    /**
     * Class that loads the persistence unit, given an object extending {@link OrmConfig}
     */
    protected OrmPersistenceUnitLoader<ORM_PERSISTENCE_UNIT, PROVIDER_CONFIGURATION_OBJECT, ORM_CONFIG> ormPersistenceUnitLoader;

    /**
     * Cache for persistence units and its configuration. We use this to make sure that for tests that use the same
     * persistence unit configuration, the same persistence unit instance is reused
     */
    protected Map<ORM_CONFIG, ConfiguredOrmPersistenceUnit<ORM_PERSISTENCE_UNIT, PROVIDER_CONFIGURATION_OBJECT>> configuredOrmPersistenceUnitCache
            = new HashMap<ORM_CONFIG, ConfiguredOrmPersistenceUnit<ORM_PERSISTENCE_UNIT, PROVIDER_CONFIGURATION_OBJECT>>();

    /**
     * Support class that enables getting a configured persistence unit from a spring ApplicationContext configured in
     * unitils. If the spring module is not enabled, this object is null.
     */
    protected OrmSpringSupport<ORM_PERSISTENCE_UNIT, PROVIDER_CONFIGURATION_OBJECT> ormSpringSupport;


    public void init(Properties configuration) {
        persistenceUnitConfigLoader = createOrmConfigLoader();
        ormPersistenceUnitLoader = createOrmPersistenceUnitLoader();
    }


    public void afterInit() {
        initOrmSpringSupport();
    }


    /**
     * @return A new instance of the {@link ResourceConfigLoader} that scans a test object for a persistence
     *         unit configuration, and returns a specific subtype of {@link OrmConfig} that wraps this configuration
     */
    abstract protected ORM_PERSISTENCE_UNIT_CONFIGG_LOADER createOrmConfigLoader();


    /**
     * @return The class of the annotation that is used for configuring and requesting injection of the
     *         persistence unit
     */
    abstract protected Class<PERSISTENCE_UNIT_CONFIG_ANNOTATION> getPersistenceUnitConfigAnnotationClass();


    /**
     * @return The type of the persistence unit
     */
    abstract protected Class<ORM_PERSISTENCE_UNIT> getPersistenceUnitClass();


    /**
     * @return A new instance of {@link OrmPersistenceUnitLoader} that can create a new persistence unit
     *         based on an {@link OrmConfig} object
     */
    abstract protected OrmPersistenceUnitLoader<ORM_PERSISTENCE_UNIT, PROVIDER_CONFIGURATION_OBJECT, ORM_CONFIG> createOrmPersistenceUnitLoader();


    /**
     * @return The fully qualified classname of the concrete implementation of {@link OrmSpringSupport} that
     *         is used by the ORM module implementation
     */
    abstract protected String getOrmSpringSupportImplClassName();


    /**
     * Returns a configured ORM persistence unit for the given test object. This persistence unit can be either configured
     * in a Spring <code>ApplicationContext</code> or by using the annotation that is applicable for the ORM implementation.
     * An exception is thrown if no persistence unit is configured. If possible, a cached instance is returned that was
     * created during a previous test.
     *
     * @param testObject The test instance, not null
     * @return The ORM persistence unit, not null
     */
    public ORM_PERSISTENCE_UNIT getPersistenceUnit(Object testObject) {
        ConfiguredOrmPersistenceUnit<ORM_PERSISTENCE_UNIT, PROVIDER_CONFIGURATION_OBJECT> configuredPersistenceUnit = getConfiguredPersistenceUnit(testObject);
        return configuredPersistenceUnit.getOrmPersistenceUnit();
    }


    /**
     * Returns the ORM implementation specific configuration object. This object cannot be used for configuration of a
     * persistence units anymore, but may be useful for implementing some specific behavior, like the entity-database
     * mapping test
     *
     * @param testObject The test instance, not null
     * @return The ORM implementation specific configuration object
     */
    public PROVIDER_CONFIGURATION_OBJECT getConfigurationObject(Object testObject) {
        ConfiguredOrmPersistenceUnit<ORM_PERSISTENCE_UNIT, PROVIDER_CONFIGURATION_OBJECT> configuredPersistenceUnit = getConfiguredPersistenceUnit(testObject);
        return configuredPersistenceUnit.getOrmConfigurationObject();
    }


    /**
     * Returns a wrapper for the persistence unit and any implementation specific configuration object for the given test
     * object. This persistence unit can be either configured in a Spring <code>ApplicationContext</code> or by using the
     * annotation that is applicable for the ORM implementation. An exception is thrown if no persistence unit is configured.
     * If possible, a cached instance is returned that was created during a previous test.
     *
     * @param testObject The test instance, not null
     * @return The persistence unit, not null
     */
    protected ConfiguredOrmPersistenceUnit<ORM_PERSISTENCE_UNIT, PROVIDER_CONFIGURATION_OBJECT> getConfiguredPersistenceUnit(Object testObject) {
        // If a persistence unit was configured in the spring ApplicationContext for this test object, we return
        // this one. Notice that in that case, no extra caching is done. This is not needed because the ApplicationContext
        // is already cached, and the ApplicationContext makes sure that the same persistence unit instance is always returned.
        if (ormSpringSupport != null && ormSpringSupport.isPersistenceUnitConfiguredInSpring(testObject)) {
            return ormSpringSupport.getConfiguredPersistenceUnit(testObject);
        }

        // Check if a persistence unit configuration can be found on the test class. If not, throw an exception
        ORM_CONFIG persistenceUnitConfig = getPersistenceUnitConfig(testObject);
        if (persistenceUnitConfig == null) {
            throw new UnitilsException("Could not find a configuring @" + getPersistenceUnitConfigAnnotationClass().getSimpleName() + " annotation or custom config method");
        }

        // Look for a cached instance. If not available, a new instance is created and added to the cache
        ConfiguredOrmPersistenceUnit<ORM_PERSISTENCE_UNIT, PROVIDER_CONFIGURATION_OBJECT> configuredPersistenceUnit = configuredOrmPersistenceUnitCache.get(persistenceUnitConfig);
        if (configuredPersistenceUnit == null) {
            configuredPersistenceUnit = ormPersistenceUnitLoader.getConfiguredOrmPersistenceUnit(testObject, persistenceUnitConfig);
            configuredOrmPersistenceUnitCache.put(persistenceUnitConfig, configuredPersistenceUnit);
        }
        return configuredPersistenceUnit;
    }
    
    
    protected boolean isConfiguredPersistenceUnitActive(Object testObject) {
        if (ormSpringSupport != null && ormSpringSupport.isPersistenceUnitConfiguredInSpring(testObject)) {
            return true;
        }
        return configuredOrmPersistenceUnitCache.containsKey(testObject);
    }


    /**
     * @param testObject The test instance, not null
     * @return The persistence unit configuration for this test class. Null if no configuration is available
     */
    protected ORM_CONFIG getPersistenceUnitConfig(Object testObject) {
        return persistenceUnitConfigLoader.loadResourceConfig(testObject);
    }


    /**
     * Indicates whether an ORM persistence unit has been configured for the given testObject. This persistence
     * unit can be either configured in a Spring <code>ApplicationContext</code> or by using the annotation that
     * is applicable for the ORM implementation.
     *
     * @param testObject The test instance, not null
     * @return true if a <code>EntityManagerFactory</code> has been configured, false otherwise
     */
    public boolean isPersistenceUnitConfiguredFor(Object testObject) {
        return (ormSpringSupport != null && ormSpringSupport.isPersistenceUnitConfiguredInSpring(testObject) || getPersistenceUnitConfig(testObject) != null);
    }


    /**
     * Returns an implementation specific persistence context, which is associated with the current transaction.
     *
     * @param testObject The test instance, not null
     * @return A persistence context, not null
     */
    public ORM_PERSISTENCE_CONTEXT getPersistenceContext(Object testObject) {
        // If no EntityManagerFactory was configured in unitils, no EntityManagers can be created
        if (!isPersistenceUnitConfiguredFor(testObject)) {
            throw new UnitilsException("No persistence unit has been configured for this test class. Make sure you either "
                    + "configure one in the spring ApplicationContext for this class, or by using the annotation @"
                    + getPersistenceUnitConfigAnnotationClass().getSimpleName());
        }
        return doGetPersistenceContext(testObject);
    }


    /**
     * Implementations of this method must return a persistence context object, and must be associated with the
     * current transaction active in unitils. The implementation can presume that a persistence unit is available,
     * so the method may not return null;
     *
     * @param testObject The test instance, not null
     * @return An implementation specific persistence context, not null
     */
    abstract protected ORM_PERSISTENCE_CONTEXT doGetPersistenceContext(Object testObject);


    /**
     * The currently active persistence context, if any. This method will not create a new persistence context,
     * it will only return something if a persistence context has previously been requested within the current
     * transaction.
     *
     * @param testObject The test instance, not null
     * @return The currently active persistence context, if any
     */
    protected ORM_PERSISTENCE_CONTEXT getActivePersistenceContext(Object testObject) {
        // If no EntityManagerFactory was configured in unitils, there are no open EntityManagers
        if (!isPersistenceUnitConfiguredFor(testObject)) {
            return null;
        }

        return doGetActivePersistenceContext(testObject);
    }


    /**
     * Implementations of this method must return the persistence context object that is associated with the current
     * transaction, if any. The implementation can presume that a persistence unit is available. If no persistence
     * context is currently active, null is returned.
     *
     * @param testObject The test instance, not null
     * @return The currently active persistence context, if any
     */
    abstract protected ORM_PERSISTENCE_CONTEXT doGetActivePersistenceContext(Object testObject);


    /**
     * Flushes all pending updates to the database. This method is useful when the effect
     * of updates needs to be checked directly on the database, without going through the persistence unit
     *
     * @param testObject The test instance, not null
     */
    public void flushDatabaseUpdates(Object testObject) {
        ORM_PERSISTENCE_CONTEXT activePersistenceContext = getActivePersistenceContext(testObject);
        if (activePersistenceContext != null) {
            flushOrmPersistenceContext(activePersistenceContext);
        }
    }


    /**
     * Flushes all pending update, using the given active persistence context
     *
     * @param activePersistenceContext Active persistence context, associated with the current transaction, not null
     */
    abstract protected void flushOrmPersistenceContext(ORM_PERSISTENCE_CONTEXT activePersistenceContext);


    /**
     * Injects the persistence unit object into all fields and methods that are annotated with the annotation
     * defined by {@link #getPersistenceUnitConfigAnnotationClass()}
     *
     * @param testObject The test object, not null
     */
    public void injectOrmPersistenceUnitIntoTestObject(Object testObject) {
        Set<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), getPersistenceUnitConfigAnnotationClass());
        Set<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), getPersistenceUnitConfigAnnotationClass());

        // filter out methods without entity manager factory argument
        Iterator<Method> iterator = methods.iterator();
        while (iterator.hasNext()) {
            Class<?>[] parameterTypes = iterator.next().getParameterTypes();
            if (parameterTypes.length == 0 || !getPersistenceUnitClass().isAssignableFrom(parameterTypes[0])) {
                iterator.remove();
            }
        }

        if (fields.isEmpty() && methods.isEmpty()) {
            // Jump out to make sure that we don't try to instantiate the EntityManagerFactory
            return;
        }

        ORM_PERSISTENCE_UNIT entityManagerFactory = getPersistenceUnit(testObject);
        setFieldAndSetterValue(testObject, fields, methods, entityManagerFactory);
    }


    /**
     * Creates an instance of {@link org.unitils.orm.common.spring.OrmSpringSupport}, that
     * implements the dependency to the {@link org.unitils.spring.SpringModule}. If the
     * {@link org.unitils.spring.SpringModule} is not active, or if a dependency of
     * {@link org.unitils.orm.common.spring.OrmSpringSupport} could not be found in the classpath,
     * the instance is not loaded.
     */
    protected void initOrmSpringSupport() {
        if (!isSpringModuleEnabled()) {
            return;
        }
        ormSpringSupport = createInstanceOfType(getOrmSpringSupportImplClassName(), false);
    }


    /**
     * Verifies whether the SpringModule is enabled. If not, this means that either the property unitils.modules doesn't
     * include spring, unitils.module.spring.enabled = false, or that the module could not be loaded because spring is not
     * in the classpath.
     *
     * @return true if the SpringModule is enabled, false otherwise
     */
    protected boolean isSpringModuleEnabled() {
        // We specify the fully qualified classname of the spring module as string, to avoid classloading issues
        return Unitils.getInstance().getModulesRepository().isModuleEnabled("org.unitils.spring.SpringModule");
    }


    protected DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
    }


    /**
     * The {@link TestListener} for this module
     */
    protected class OrmTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            injectOrmPersistenceUnitIntoTestObject(testObject);
        }

    }

}
