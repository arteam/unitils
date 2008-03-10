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
package org.unitils.orm.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.unitils.orm.hibernate.HibernateModule;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;

/**
 * Test class for the loading of the configuration in a test class hierarchy for the HibernateModule
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateModuleConfigurationInheritanceTest {

	HibernateModule hibernateModule;

    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
    	hibernateModule = new HibernateModule();
    	Properties properties = new Properties();
    	properties.put(HibernateModule.PROPKEY_CONFIGURATION_CLASS_NAME, Configuration.class.getName());
		hibernateModule.init(properties);
    }


    /**
     * Tests loading of a configuration location specified on class-level.
     * Both super and sub class have annotations with values and custom create methods.
     */
    @Test
    public void testGetHibernateConfiguration_overriden() {
        HibernateTestCustomInit hibernateTest1 = new HibernateTestCustomInit();
        Configuration hibernateConfiguration = hibernateModule.getConfigurationObject(hibernateTest1);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate-sub.cfg.xml", hibernateConfiguration.getProperty("name"));
        assertFalse(hibernateTest1.initMethod1Called);
        assertTrue(hibernateTest1.initMethod2Called);
    }


    /**
     * Tests loading of a configuration location specified on class-level.
     * Both super and sub class have annotations with values and but only super class has custom create method.
     */
    @Test
    public void testGetHibernateConfiguration_overridenNoCustomCreateInSubClass() {
        HibernateTestNoCustomCreate hibernateTest2 = new HibernateTestNoCustomCreate();
        Configuration hibernateConfiguration = hibernateModule.getConfigurationObject(hibernateTest2);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate-sub.cfg.xml", hibernateConfiguration.getProperty("name"));
        assertFalse(hibernateTest2.initMethod1Called);
    }


    /**
     * Tests creating the application context. No context creation is done in the sub-class, the context of the super
     * class should be used.
     */
    @Test
    public void testGetHibernateConfiguration_onlyInSuperClass() {
        HibernateTestNoCreation1 hibernateTestNoCreation = new HibernateTestNoCreation1();
        Configuration hibernateConfiguration = hibernateModule.getConfigurationObject(hibernateTestNoCreation);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
        assertTrue(hibernateTestNoCreation.initMethod1Called);
    }


    /**
     * Test reusing a configuration of a super class.
     */
    @Test
    public void testGetHibernateConfiguration_twice() {
        Configuration hibernateConfiguration1 = hibernateModule.getConfigurationObject(new HibernateTestNoCreation1());
        Configuration hibernateConfiguration2 = hibernateModule.getConfigurationObject(new HibernateTestNoCreation2());

        assertNotNull(hibernateConfiguration1);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration1.getProperty("name"));
        assertSame(hibernateConfiguration1, hibernateConfiguration2);
    }


    /**
     * Test Hibernate super-class.
     */
    @HibernateSessionFactory("org/unitils/hibernate/hibernate.cfg.xml")
    public class HibernateTestSuper {

        protected boolean initMethod1Called = false;

        @HibernateSessionFactory
        protected void initMethod1(Configuration cfg) {
            initMethod1Called = true;
        }
    }

    /**
     * Test Hibernate sub-class with custom create.
     */
    @HibernateSessionFactory("org/unitils/hibernate/hibernate-sub.cfg.xml")
    public class HibernateTestCustomInit extends HibernateTestSuper {

        protected boolean initMethod2Called = false;

        @HibernateSessionFactory
        protected void initMethod2(Configuration cfg) {
            initMethod2Called = true;
        }
    }

    /**
     * Test Hibernate sub-class without custom create.
     */
    @HibernateSessionFactory("org/unitils/hibernate/hibernate-sub.cfg.xml")
    public class HibernateTestNoCustomCreate extends HibernateTestSuper {
    }

    /**
     * Test Hibernate sub-class without any context declaration.
     */
    private class HibernateTestNoCreation1 extends HibernateTestSuper {
    }

    /**
     * Test Hibernate sub-class without any context declaration.
     */
    private class HibernateTestNoCreation2 extends HibernateTestSuper {
    }

}
