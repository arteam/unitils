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
package org.unitils.orm.jpa;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.unitils.core.ConfigurationLoader;
import org.unitils.orm.jpa.annotation.JpaEntityManagerFactory;

/**
 * Test class for the loading of the configuration in the JpaModule
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class JpaModuleInjectionTest {

	/* Tested object */
    private JpaModule jpaModule;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        jpaModule = new JpaModule();
        jpaModule.init(configuration);
    }


    /**
     * Tests hibernate session factory injection for a field and a setter method.
     */
    @Test
    public void testInjectHibernateSessionFactory() {
        JpaTestEntityManager jpaTestEntityManager = new JpaTestEntityManager();
        jpaModule.injectOrmPersistenceUnitIntoTestObject(jpaTestEntityManager);

        assertNotNull(jpaTestEntityManager.entityManagerField);
        assertSame(jpaTestEntityManager.entityManagerField, jpaTestEntityManager.entityManagerSetter);
    }


    /**
     * Tests mixing of hibernate session factory injection for a field and a setter method and the use of
     * a custom initializer and a custom create method.
     */
    @Test
    public void testInjectHibernateSessionFactory_mixingWithCustomCreateAndInitializer() {
        JpaTestEntityManagerMixing jpaTestEntityManagerMixing = new JpaTestEntityManagerMixing();
        jpaModule.injectOrmPersistenceUnitIntoTestObject(jpaTestEntityManagerMixing);

        assertNotNull(jpaTestEntityManagerMixing.entityManagerField);
        assertTrue(jpaTestEntityManagerMixing.customInitializerCalled);
    }


    /**
     * Test hibernate test for session factory injection.
     */
    @JpaEntityManagerFactory(persistenceUnit = "unitils", configFile = "org/unitils/orm/jpa/persistence-test.xml")
    public class JpaTestEntityManager {

        @JpaEntityManagerFactory
        private EntityManagerFactory entityManagerField;

        private EntityManagerFactory entityManagerSetter;

        @JpaEntityManagerFactory
        public void setSessionFactorySetter(EntityManagerFactory entityManagerSetter) {
            this.entityManagerSetter = entityManagerSetter;
        }
    }


    /**
     * Test hibernate test for session factory injection. It also contains a custom initializer and custom
     * create for testing the mixing of the HibernateSessionFactory annotation
     */
    @JpaEntityManagerFactory(persistenceUnit = "unitils", configFile = "org/unitils/orm/jpa/persistence-test.xml")
    public class JpaTestEntityManagerMixing {

    	@JpaEntityManagerFactory
        EntityManagerFactory entityManagerField = null;

        EntityManagerFactory entityManagerSetter;

        boolean customInitializerCalled = false;

        @JpaEntityManagerFactory
        public void setEntityManagerSetter(EntityManagerFactory entityManagerSetter) {
            this.entityManagerSetter = entityManagerSetter;
        }

        @JpaEntityManagerFactory
        public void customInitializer(LocalContainerEntityManagerFactoryBean factoryBean) {
            customInitializerCalled = true;
        }

    }


}
