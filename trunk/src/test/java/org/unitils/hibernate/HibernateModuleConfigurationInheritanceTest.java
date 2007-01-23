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
package org.unitils.hibernate;

import org.hibernate.cfg.Configuration;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.hibernate.annotation.HibernateConfiguration;

/**
 * Test class for the loading of the configuration in a test class hierarchy for the HibernateModule
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateModuleConfigurationInheritanceTest extends UnitilsJUnit3 {

    /* Tested object */
    private HibernateModule hibernateModule;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        org.apache.commons.configuration.Configuration configuration = new ConfigurationLoader().loadConfiguration();
        hibernateModule = new HibernateModule();
        hibernateModule.init(configuration);
    }


    /**
     * Tests loading of a configuration location specified on class-level.
     */
    public void testGetHibernateConfiguration() {
        HibernateTest1 hibernateTest1 = new HibernateTest1();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(hibernateTest1);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
        assertEquals("overriden", hibernateConfiguration.getProperty("superValue"));
        assertTrue(hibernateTest1.createMethod1Called);
        assertTrue(hibernateTest1.createMethod2Called);
    }


    /**
     * Tests creating the application context. No context creation is done in the sub-class, the context of the super
     * class should be used.
     */
    public void testGetHibernateConfiguration_onlyInSuperClass() {
        HibernateTestNoCreation hibernateTestNoCreation = new HibernateTestNoCreation();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(hibernateTestNoCreation);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
        assertTrue(hibernateTestNoCreation.createMethod1Called);
    }


    /**
     * Test reusing a configuration of a super class.
     */
    public void testGetHibernateConfiguration_twice() {
        Configuration hibernateConfiguration1 = hibernateModule.getHibernateConfiguration(new HibernateTestNoCreation());
        Configuration hibernateConfiguration2 = hibernateModule.getHibernateConfiguration(new HibernateTestNoCreation());

        assertNotNull(hibernateConfiguration1);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration1.getProperty("name"));
        assertSame(hibernateConfiguration1, hibernateConfiguration2);
    }


    /**
     * Test Hibernate super-class.
     */
    @HibernateConfiguration("org/unitils/hibernate/hibernate.cfg.xml")
    public class HibernateTestSuper {

        protected boolean createMethod1Called = false;

        @HibernateConfiguration
        protected Configuration createMethod1(Configuration configuration) {
            createMethod1Called = true;
            return configuration;
        }
    }

    /**
     * Test Hibernate sub-class.
     */
    @HibernateConfiguration("org/unitils/hibernate/hibernate-sub.cfg.xml")
    public class HibernateTest1 extends HibernateTestSuper {

        protected boolean createMethod2Called = false;

        @HibernateConfiguration
        protected Configuration createMethod2(Configuration configuration) {
            createMethod2Called = true;
            return configuration;
        }
    }


    /**
     * Test Hibernate sub-class without any context declaration.
     */
    private class HibernateTestNoCreation extends HibernateTestSuper {
    }

}
