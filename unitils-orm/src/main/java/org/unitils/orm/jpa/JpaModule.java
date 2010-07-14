/*
 * Copyright Unitils.org
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
package org.unitils.orm.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.unitils.core.TestListener;
import org.unitils.core.util.ConfigUtils;
import org.unitils.database.transaction.impl.UnitilsTransactionManagementConfiguration;
import org.unitils.orm.common.OrmModule;
import org.unitils.orm.common.util.OrmPersistenceUnitLoader;
import org.unitils.orm.jpa.annotation.JpaEntityManagerFactory;
import org.unitils.orm.jpa.util.JpaAnnotationConfigLoader;
import org.unitils.orm.jpa.util.JpaConfig;
import org.unitils.orm.jpa.util.JpaEntityManagerFactoryLoader;
import org.unitils.orm.jpa.util.JpaProviderSupport;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;

import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.PropertyUtils.getString;
import static org.unitils.util.ReflectionUtils.setFieldAndSetterValue;

/**
 * Module providing support for unit tests for code that uses JPA. It offers an easy way of loading a
 * <code>EntityManagerFactory</code> and having it injected into the test. The configured
 * <code>EntityManagerFactory</code> will connect to the unitils configured test datasource, and
 * join in unitils test-bound transactions.
 * <p/>
 * <code>EntityManagerFactory</code> instances are cached, to make sure that for any two tests that share the same
 * configuration, the same instance of this object is used.
 * <p/>
 * An <code>EntityManagerFactory</code> is injected into all fields or methods of the test object
 * annotated with {@link JpaEntityManagerFactory} or <code>javax.persistence.PersistenceUnit</code>. An
 * <code>EntityManager</code> is injected into all fields or methods of the test annotated with
 * <code>javax.persistence.PersistenceContext</code>.
 * todo injection into other objects
 * <p/>
 * This module also offers a test to check whether the mapping of all entities is consistent with the structure of the
 * database. It is highly recommended to write a unit test that invokes {@link JpaUnitils#assertMappingWithDatabaseConsistent()},
 * This is a very useful test that verifies whether the mapping of all your objects entities still corresponds
 * with the actual structure of the database. Currently, it's only available when the persistence provider is hibernate.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class JpaModule extends OrmModule<EntityManagerFactory, EntityManager, Object, JpaEntityManagerFactory, JpaConfig, JpaAnnotationConfigLoader> {

    /* Property key that defines the persistence provider */
    public static final String PROPKEY_PERSISTENCE_PROVIDER = "jpa.persistenceProvider";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(JpaModule.class);

    /**
     * Implements provider specific operations
     */
    protected JpaProviderSupport jpaProviderSupport;

    /**
     * Constructor for JpaModule.
     */
    public JpaModule() {
        super();
        // Make sure recent version spring ORM module is in the classpath
        AbstractEntityManagerFactoryBean.class.getName();
    }


    /**
     * @param configuration The Unitils configuration, not null
     */
    public void init(Properties configuration) {
        super.init(configuration);

        String persistenceProviderImplClassName = getString(PROPKEY_PERSISTENCE_PROVIDER, configuration);
        jpaProviderSupport = ConfigUtils.getInstanceOf(JpaProviderSupport.class, configuration, persistenceProviderImplClassName);
    }


    public void afterInit() {
        super.afterInit();

        // Make sure that a spring JpaTransactionManager is used for transaction management in the database module, if the
        // current test object defines a JPA EntityManagerFactory
        getDatabaseModule().registerTransactionManagementConfiguration(new UnitilsTransactionManagementConfiguration() {

            public boolean isApplicableFor(Object testObject) {
                return isPersistenceUnitConfiguredFor(testObject);
            }

            public PlatformTransactionManager getSpringPlatformTransactionManager(Object testObject) {
                EntityManagerFactory entityManagerFactory = getPersistenceUnit(testObject);
                JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(entityManagerFactory);
                jpaTransactionManager.setDataSource(getDataSource());
                jpaTransactionManager.setJpaDialect(jpaProviderSupport.getSpringJpaVendorAdaptor().getJpaDialect());
                return jpaTransactionManager;
            }

            public boolean isTransactionalResourceAvailable(Object testObject) {
                return getDatabaseModule().isDataSourceLoaded();
            }

            public Integer getPreference() {
                return 10;
            }

        });
    }


    @Override
    protected JpaAnnotationConfigLoader createOrmConfigLoader() {
        return new JpaAnnotationConfigLoader();
    }


    @Override
    protected Class<JpaEntityManagerFactory> getPersistenceUnitConfigAnnotationClass() {
        return JpaEntityManagerFactory.class;
    }


    @Override
    protected Class<EntityManagerFactory> getPersistenceUnitClass() {
        return EntityManagerFactory.class;
    }


    @Override
    protected OrmPersistenceUnitLoader<EntityManagerFactory, Object, JpaConfig> createOrmPersistenceUnitLoader() {
        return new JpaEntityManagerFactoryLoader();
    }


    @Override
    protected String getOrmSpringSupportImplClassName() {
        return "org.unitils.orm.jpa.util.spring.JpaSpringSupport";
    }


    protected EntityManager doGetPersistenceContext(Object testObject) {
        return EntityManagerFactoryUtils.getTransactionalEntityManager(getPersistenceUnit(testObject));
    }

    protected EntityManager doGetActivePersistenceContext(Object testObject) {
        EntityManagerHolder entityManagerHolder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(getPersistenceUnit(testObject));
        if (entityManagerHolder != null && entityManagerHolder.getEntityManager() != null && entityManagerHolder.getEntityManager().isOpen()) {
            return entityManagerHolder.getEntityManager();
        }
        return null;
    }

    protected void flushOrmPersistenceContext(EntityManager activeEntityManager) {
        logger.info("Flushing entity manager " + activeEntityManager);
        activeEntityManager.flush();
    }


    /**
     * Checks if the mapping of the managed objects with the database is still correct.
     *
     * @param testObject The test instance, not null
     */
    public void assertMappingWithDatabaseConsistent(Object testObject) {
        jpaProviderSupport.assertMappingWithDatabaseConsistent(getPersistenceUnit(testObject).createEntityManager(),
                getConfiguredPersistenceUnit(testObject).getOrmConfigurationObject());
    }


    /**
     * Injects the currently configured <code>EntityManagerFactory</code> and currently active
     * <code>EntityManager</code> into fields or methods or the test object annotated with
     * <code>javax.persistence.PersistenceUnit</code> or <code>javax.persistence.PersistenceContext</code>,
     * respectively.
     *
     * @param testObject The test instance, not null
     */
    public void injectJpaResourcesIntoTestObject(Object testObject) {
        // If no EntityManagerFactory was configured in unitils, nothing can be injected
        if (!isPersistenceUnitConfiguredFor(testObject)) {
            return;
        }

        injectJpaResourcesInto(testObject, testObject);
    }


    /**
     * Injects the <code>EntityManagerFactory</code> and currently active <code>EntityManager</code> into fields
     * or methods of the given target object annotated with <code>javax.persistence.PersistenceUnit</code> or
     * <code>javax.persistence.PersistenceContext</code>, respectively.
     *
     * @param testObject The test object, not null
     * @param target     The target object to inject the resources into, not null
     */
    public void injectJpaResourcesInto(Object testObject, Object target) {
        injectEntityManagerFactory(testObject, target);
        injectEntityManager(testObject, target);
    }


    /**
     * Injects the JPA <code>EntityManagerFactory</code> into all fields and methods that are
     * annotated with <code>javax.persistence.PersistenceUnit</code>
     *
     * @param testObject The test object, not null
     */
    public void injectEntityManagerFactory(Object testObject, Object target) {
        Set<Field> fields = getFieldsAnnotatedWith(target.getClass(), PersistenceUnit.class);
        Set<Method> methods = getMethodsAnnotatedWith(target.getClass(), PersistenceUnit.class);
        if (fields.isEmpty() && methods.isEmpty()) {
            // Jump out to make sure that we don't try to instantiate the EntityManagerFactory
            return;
        }

        EntityManagerFactory entityManagerFactory = getPersistenceUnit(testObject);
        setFieldAndSetterValue(target, fields, methods, entityManagerFactory);
    }


    /**
     * Injects the currently active JPA <code>EntityManager</code> into all fields and methods that are
     * annotated with <code>javax.persistence.PersistenceContext</code>
     *
     * @param testObject The test object, not null
     */
    public void injectEntityManager(Object testObject, Object target) {
        Set<Field> fields = getFieldsAnnotatedWith(target.getClass(), PersistenceContext.class);
        Set<Method> methods = getMethodsAnnotatedWith(target.getClass(), PersistenceContext.class);
        if (fields.isEmpty() && methods.isEmpty()) {
            // Jump out to make sure that we don't try to instantiate the EntityManagerFactory
            return;
        }

        EntityManager entityManager = getPersistenceContext(testObject);
        setFieldAndSetterValue(target, fields, methods, entityManager);
    }


    protected DataSource getDataSource() {
        return getDatabaseModule().getDataSource();
    }


    public JpaProviderSupport getJpaProviderSupport() {
        return jpaProviderSupport;
    }


    /**
     * @return The TestListener associated with this module
     */
    public TestListener getTestListener() {
        return new JpaTestListener();
    }


    /**
     * The {@link TestListener} for this module
     */
    protected class JpaTestListener extends OrmTestListener {

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            injectJpaResourcesIntoTestObject(testObject);
        }

    }
}
