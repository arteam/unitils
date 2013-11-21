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
package org.unitils.orm.hibernate;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.unitils.util.PropertyUtils.getString;

import java.lang.reflect.Method;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.impl.UnitilsTransactionManagementConfiguration;
import org.unitils.database.util.Flushable;
import org.unitils.orm.common.OrmModule;
import org.unitils.orm.common.util.ConfiguredOrmPersistenceUnit;
import org.unitils.orm.common.util.OrmConfig;
import org.unitils.orm.common.util.OrmPersistenceUnitLoader;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.orm.hibernate.util.HibernateAnnotationConfigLoader;
import org.unitils.orm.hibernate.util.HibernateAssert;
import org.unitils.orm.hibernate.util.HibernateSessionFactoryLoader;
import org.unitils.util.ReflectionUtils;

/**
 * Module providing support for unit tests for code that uses Hibernate. It offers an easy way of loading hibernate 
 * SessionFactories and having them injected them into the test. It also offers a test to check whether the hibernate 
 * mapping is consistent with the structure of the database. 
 * <p/>
 * A Hibernate <code>SessionFactory</code> is created when requested and injected into all fields or methods of the test 
 * annotated with {@link HibernateSessionFactory}. 
 * <p/>
 * It is highly recommended to write a unit test that invokes {@link HibernateUnitils#assertMappingWithDatabaseConsistent()},
 * This is a very useful test that verifies whether the mapping of all your Hibernate mapped objects still corresponds
 * with the actual structure of the database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateModule extends OrmModule<SessionFactory, Session, Configuration, HibernateSessionFactory, OrmConfig, HibernateAnnotationConfigLoader> implements Module, Flushable {

    /* Property that defines the class name of the hibernate configuration */
    public static final String PROPKEY_CONFIGURATION_CLASS_NAME = "HibernateModule.configuration.implClassName";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(HibernateModule.class);
    
    /**
     * Subclass of org.hibernate.cfg.Configuration that is used for configuring hibernate
     */
    private Class<? extends Configuration> configurationObjectClass;

    /**
     * @param configuration The Unitils configuration, not null
     */
    public void init(Properties configuration) {
    	super.init(configuration);
    	
        String configurationImplClassName = getString(PROPKEY_CONFIGURATION_CLASS_NAME, configuration);
        configurationObjectClass = ReflectionUtils.getClassWithName(configurationImplClassName);
    }
    
    
    public void afterInit() {
    	super.afterInit();
    	
    	
    }
    
    public void registerTransactionManagementConfiguration() {
     // Make sure that a spring HibernateTransactionManager is used for transaction management in the database module, if the
        // current test object defines a hibernate SessionFactory
        getDatabaseModule().registerTransactionManagementConfiguration(new UnitilsTransactionManagementConfiguration() {
            
            public boolean isApplicableFor(Object testObject) {
                return isPersistenceUnitConfiguredFor(testObject);
            }
            
            public PlatformTransactionManager getSpringPlatformTransactionManager(Object testObject) {
                SessionFactory sessionFactory = getPersistenceUnit(testObject);
                HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager(sessionFactory);
                hibernateTransactionManager.setDataSource(getDataSource());
                return hibernateTransactionManager;
            }

            public boolean isTransactionalResourceAvailable(Object testObject) {
                return getDatabaseModule().getWrapper(databaseName).isDataSourceLoaded();
            }

            public Integer getPreference() {
                return 10;
            }
            
        });
    }
    
    
    @Override
    protected HibernateAnnotationConfigLoader createOrmConfigLoader() {
    	return new HibernateAnnotationConfigLoader();
    }
    
    
    @Override
    protected Class<HibernateSessionFactory> getPersistenceUnitConfigAnnotationClass() {
    	return HibernateSessionFactory.class;
    }

    
    @Override
    protected Class<SessionFactory> getPersistenceUnitClass() {
    	return SessionFactory.class;
    }

    
    @Override
	protected OrmPersistenceUnitLoader<SessionFactory, Configuration, OrmConfig> createOrmPersistenceUnitLoader() {
		return new HibernateSessionFactoryLoader(databaseName);
	}

    
	@Override
    protected String getOrmSpringSupportImplClassName() {
		return "org.unitils.orm.hibernate.util.HibernateSpringSupport";
	}
	
	
    @Override
    protected Session doGetPersistenceContext(Object testObject) {
		return SessionFactoryUtils.getSession(getPersistenceUnit(testObject), true);
	}
    
    
    @Override
    protected Session doGetActivePersistenceContext(Object testObject) {
		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(getPersistenceUnit(testObject));
    	if (sessionHolder != null && sessionHolder.getSession() != null && sessionHolder.getSession().isOpen()) {
    		return sessionHolder.getSession();
    	}
    	return null;
	}
    
    
    @Override
    protected void flushOrmPersistenceContext(Session activeSession) {
		logger.info("Flushing session " + activeSession);
		activeSession.flush();
	}
    
    
    /**
     * Checks if the mapping of the Hibernate managed objects with the database is correct.
     *
     * @param testObject The test instance, not null
     */
    public void assertMappingWithDatabaseConsistent(Object testObject) {
    	ConfiguredOrmPersistenceUnit<SessionFactory, Configuration> configuredPersistenceUnit = getConfiguredPersistenceUnit(testObject);
        Configuration configuration = configuredPersistenceUnit.getOrmConfigurationObject();
        Session session = getPersistenceContext(testObject);
        Dialect databaseDialect = getDatabaseDialect(configuration);

        HibernateAssert.assertMappingWithDatabaseConsistent(configuration, session, databaseDialect);
    }
    
    
    /**
     * @return The subclass of <code>org.hibernate.cfg.Configuration</code> that is used for configuring hibernate
     */
    public Class<? extends Configuration> getConfigurationObjectClass() {
		return configurationObjectClass;
	}
    
    
    /**
     * Gets the database dialect from the given Hibernate <code>Configuration</code.
     *
     * @param configuration The hibernate config, not null
     * @return the database Dialect, not null
     */
    protected Dialect getDatabaseDialect(Configuration configuration) {
        String dialectClassName = configuration.getProperty("hibernate.dialect");
        if (isEmpty(dialectClassName)) {
            throw new UnitilsException("Property hibernate.dialect not specified");
        }
        try {
            return (Dialect) Class.forName(dialectClassName).newInstance();
        } catch (Exception e) {
            throw new UnitilsException("Could not instantiate dialect class " + dialectClassName, e);
        }
    }


    protected DataSource getDataSource() {
    	return getDatabaseModule().getWrapper(databaseName).getDataSourceAndActivateTransactionIfNeeded();
    }
    
    
    /**
     * @return The TestListener associated with this module
     */
    public TestListener getTestListener() {
        return new HibernateTestListener();
    }


    /**
     * The {@link TestListener} for this module
     */
    protected class HibernateTestListener extends OrmTestListener {
        
        /**
         * @see org.unitils.core.TestListener#beforeTestMethod(java.lang.Object, java.lang.reflect.Method)
         */
        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            databaseName = getDatabaseName(testObject, testMethod);
            registerTransactionManagementConfiguration();
            super.beforeTestMethod(testObject, testMethod);
        }
        
    }

   
}
