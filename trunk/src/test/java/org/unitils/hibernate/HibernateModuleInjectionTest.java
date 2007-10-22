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
package org.unitils.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.hibernate.annotation.HibernateSessionFactory;

import java.util.Properties;

/**
 * Test class for injection methods of the HibernateModule
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateModuleInjectionTest extends UnitilsJUnit3 {

    /* Tested object */
    private HibernateModule hibernateModule;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        hibernateModule = new HibernateModule();
        hibernateModule.init(configuration);
    }


    /**
     * Tests hibernate session factory injection for a field and a setter method.
     */
    public void testInjectHibernateSessionFactory() {
        HibernateTestSessionFactory hibernateTestSessionFactory = new HibernateTestSessionFactory();
        hibernateModule.injectSessionFactory(hibernateTestSessionFactory);

        assertNotNull(hibernateTestSessionFactory.sessionFactoryField);
        assertSame(hibernateTestSessionFactory.sessionFactoryField, hibernateTestSessionFactory.sessionFactorySetter);
    }


    /**
     * Tests mixing of hibernate session factory injection for a field and a setter method and the use of
     * a custom initializer and a custom create method.
     */
    public void testInjectHibernateSessionFactory_mixingWithCustomCreateAndInitializer() {
        HibernateTestSessionFactoryMixing hibernateTestSessionFactory = new HibernateTestSessionFactoryMixing();
        hibernateModule.injectSessionFactory(hibernateTestSessionFactory);

        assertNotNull(hibernateTestSessionFactory.sessionFactoryField);
        assertTrue(hibernateTestSessionFactory.customInitializerCalled);
        assertTrue(hibernateTestSessionFactory.createConfigurationCalled);
    }


    /**
     * Test hibernate test for session factory injection.
     */
    @HibernateSessionFactory("org/unitils/hibernate/hibernate.cfg.xml")
    public class HibernateTestSessionFactory {

        @HibernateSessionFactory
        private SessionFactory sessionFactoryField = null;

        private SessionFactory sessionFactorySetter;

        @HibernateSessionFactory
        public void setSessionFactorySetter(SessionFactory sessionFactorySetter) {
            this.sessionFactorySetter = sessionFactorySetter;
        }
    }


    /**
     * Test hibernate test for session factory injection. It also contains a custom initializer and custom
     * create for testing the mixing of the HibernateSessionFactory annotation
     */
    @HibernateSessionFactory("org/unitils/hibernate/hibernate.cfg.xml")
    public class HibernateTestSessionFactoryMixing {

        @HibernateSessionFactory
        private SessionFactory sessionFactoryField = null;

        private SessionFactory sessionFactorySetter;

        private boolean customInitializerCalled = false;

        private boolean createConfigurationCalled = false;

        @HibernateSessionFactory
        public void setSessionFactorySetter(SessionFactory sessionFactorySetter) {
            this.sessionFactorySetter = sessionFactorySetter;
        }

        @HibernateSessionFactory
        public void customInitializer(Configuration configuration) {
            customInitializerCalled = true;
        }

        @HibernateSessionFactory
        public Configuration createConfiguration() {
            createConfigurationCalled = true;
            return new AnnotationConfiguration();
        }
    }


}