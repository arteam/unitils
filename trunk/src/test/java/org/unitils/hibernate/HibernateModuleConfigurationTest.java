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


import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.UnitilsException;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import org.unitils.hibernate.util.SessionFactoryManager;

import java.util.List;

/**
 * Test class for the loading of the configuration in the HibernateModule
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateModuleConfigurationTest extends UnitilsJUnit3 {

    /* Tested object */
    private SessionFactoryManager sessionFactoryManager;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        sessionFactoryManager = new SessionFactoryManager(AnnotationConfiguration.class.getName(), true);
    }


    /**
     * Tests loading of a configuration location specified on class-level.
     */
    public void testGetHibernateConfiguration_classLevel() {
        HibernateTestClassLevel hibernateTestClassLevel = new HibernateTestClassLevel();
        Configuration hibernateConfiguration = sessionFactoryManager.getHibernateConfiguration(hibernateTestClassLevel);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration annotation on class-level but no location (default should be loaded).
     */
    public void testGetHibernateConfiguration_classLevelNoLocation() {
        HibernateTestClassLevelNoLocation hibernateTestClassLevelNoLocation = new HibernateTestClassLevelNoLocation();
        Configuration hibernateConfiguration = sessionFactoryManager.getHibernateConfiguration(hibernateTestClassLevelNoLocation);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration location specified on field-level.
     */
    public void testGetHibernateConfiguration_fieldLevel() {
        HibernateTestFieldLevel hibernateTestFieldLevel = new HibernateTestFieldLevel();
        Configuration hibernateConfiguration = sessionFactoryManager.getHibernateConfiguration(hibernateTestFieldLevel);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration annotation on field-level but no location (default should be loaded).
     */
    public void testGetHibernateConfiguration_fieldLevelNoLocation() {
        HibernateTestFieldLevelNoLocation hibernateTestFieldLevelNoLocation = new HibernateTestFieldLevelNoLocation();
        Configuration hibernateConfiguration = sessionFactoryManager.getHibernateConfiguration(hibernateTestFieldLevelNoLocation);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration location specified on field-level.
     */
    public void testGetHibernateConfiguration_setterLevel() {
        HibernateTestSetterLevel hibernateTestSetterLevel = new HibernateTestSetterLevel();
        Configuration hibernateConfiguration = sessionFactoryManager.getHibernateConfiguration(hibernateTestSetterLevel);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration annotation on field-level but no location (default should be loaded).
     */
    public void testGetHibernateConfiguration_setterLevelNoLocation() {
        HibernateTestSetterLevelNoLocation hibernateTestSetterLevelNoLocation = new HibernateTestSetterLevelNoLocation();
        Configuration hibernateConfiguration = sessionFactoryManager.getHibernateConfiguration(hibernateTestSetterLevelNoLocation);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration annotation through a custom create method
     */
    public void testGetHibernateConfiguration_customCreateMethod() {
        HibernateTestCustomCreate hibernateTestCustomCreate = new HibernateTestCustomCreate();
        Configuration hibernateConfiguration = sessionFactoryManager.getHibernateConfiguration(hibernateTestCustomCreate);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration annotation through a custom create method passing the current
     * configuration as argument.
     */
    public void testGetHibernateConfiguration_customCreateMethodWithConfigurationArgument() {
        HibernateTestCustomCreateWithConfiguration hibernateTestCustomCreateWithConfiguration = new HibernateTestCustomCreateWithConfiguration();
        Configuration hibernateConfiguration = sessionFactoryManager.getHibernateConfiguration(hibernateTestCustomCreateWithConfiguration);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration through a method-level specified location and then
     * overriding this configuration in a custom create method.
     */
    public void testGetHibernateConfiguration_customCreateMethodOverridingConfiguration() {
        HibernateTestCustomCreateOverridingCurrentConfiguration testCustomCreateOverridingCurrentConfiguration = new HibernateTestCustomCreateOverridingCurrentConfiguration();
        Configuration hibernateConfiguration = sessionFactoryManager.getHibernateConfiguration(testCustomCreateOverridingCurrentConfiguration);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration using a custom create method with a wrong signature.
     */
    public void testGetHibernateConfiguration_customCreateWrongSignature() {
        HibernateTestCustomCreateWrongSignature hibernateTestCustomCreateWrongSignature = new HibernateTestCustomCreateWrongSignature();
        try {
            sessionFactoryManager.getHibernateConfiguration(hibernateTestCustomCreateWrongSignature);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests loading of a configuration with a wrong location.
     */
    public void testGetHibernateConfiguration_wrongLocation() {
        HibernateTestWrongLocation hibernateTestWrongLocation = new HibernateTestWrongLocation();
        try {
            sessionFactoryManager.getHibernateConfiguration(hibernateTestWrongLocation);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Test reusing a configuration for the same class.
     */
    public void testGetHibernateConfiguration_twice() {
        Configuration hibernateConfiguration1 = sessionFactoryManager.getHibernateConfiguration(new HibernateTestClassLevel());
        Configuration hibernateConfiguration2 = sessionFactoryManager.getHibernateConfiguration(new HibernateTestClassLevel());

        assertNotNull(hibernateConfiguration1);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration1.getProperty("name"));
        assertSame(hibernateConfiguration1, hibernateConfiguration2);
    }


    /**
     * Test invalidating a cached configuration.
     */
    public void testInvalidateHibernateConfiguration() {
        Configuration hibernateConfiguration1 = sessionFactoryManager.getHibernateConfiguration(new HibernateTestClassLevel());
        sessionFactoryManager.invalidateHibernateConfiguration();
        Configuration hibernateConfiguration2 = sessionFactoryManager.getHibernateConfiguration(new HibernateTestClassLevel());

        assertNotNull(hibernateConfiguration1);
        assertNotNull(hibernateConfiguration2);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration1.getProperty("name"));
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration2.getProperty("name"));
        assertNotSame(hibernateConfiguration1, hibernateConfiguration2);
    }


    /**
     * Test invalidating a cached configuration using the class name.
     */
    public void testInvalidateHibernateConfiguration_classSpecified() {
        Configuration hibernateConfiguration1 = sessionFactoryManager.getHibernateConfiguration(new HibernateTestClassLevel());
        sessionFactoryManager.invalidateHibernateConfiguration(HibernateTestClassLevel.class);
        Configuration hibernateConfiguration2 = sessionFactoryManager.getHibernateConfiguration(new HibernateTestClassLevel());

        assertNotNull(hibernateConfiguration1);
        assertNotNull(hibernateConfiguration2);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration1.getProperty("name"));
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration2.getProperty("name"));
        assertNotSame(hibernateConfiguration1, hibernateConfiguration2);
    }


    /**
     * Test invalidating a cached configuration using a wrong class name.
     */
    public void testInvalidateHibernateConfiguration_otherClassSpecified() {
        Configuration hibernateConfiguration1 = sessionFactoryManager.getHibernateConfiguration(new HibernateTestClassLevel());
        sessionFactoryManager.invalidateHibernateConfiguration(String.class, List.class);
        Configuration hibernateConfiguration2 = sessionFactoryManager.getHibernateConfiguration(new HibernateTestClassLevel());

        assertNotNull(hibernateConfiguration1);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration1.getProperty("name"));
        assertSame(hibernateConfiguration1, hibernateConfiguration2);
    }


    /**
     * Class level configuration.
     */
    @HibernateSessionFactory("org/unitils/hibernate/hibernate.cfg.xml")
    public class HibernateTestClassLevel {
    }

    /**
     * Class level configuration no location specified.
     */
    @HibernateSessionFactory
    public class HibernateTestClassLevelNoLocation {
    }

    /**
     * Field level configuration.
     */
    public class HibernateTestFieldLevel {

        @HibernateSessionFactory("org/unitils/hibernate/hibernate.cfg.xml")
        protected SessionFactory field;
    }

    /**
     * Field level configuration no location specified.
     */
    public class HibernateTestFieldLevelNoLocation {

        @HibernateSessionFactory
        protected SessionFactory field;
    }

    /**
     * Setter level configuration.
     */
    public class HibernateTestSetterLevel {

        @HibernateSessionFactory("org/unitils/hibernate/hibernate.cfg.xml")
        public void setField(SessionFactory sessionFactory) {
        }
    }

    /**
     * Setter level configuration no location specified.
     */
    public class HibernateTestSetterLevelNoLocation {

        @HibernateSessionFactory
        public void setField(SessionFactory sessionFactory) {
        }
    }

    /**
     * Configuration with custom create.
     */
    public class HibernateTestCustomCreate {

        @HibernateSessionFactory
        public Configuration createMethod() {
            Configuration configuration = new AnnotationConfiguration();
            configuration.configure();
            return configuration;
        }
    }

    /**
     * Configuration with custom create with configuration argument.
     */
    public class HibernateTestCustomCreateWithConfiguration {

        @HibernateSessionFactory
        public Configuration createMethod(List<String> locations) {
            assertTrue(locations.isEmpty());
            Configuration configuration = new AnnotationConfiguration();
            configuration.configure();
            return configuration;
        }
    }

    /**
     * Configuration loading with method level location specified and
     * then passing this context to the custom create method. This configuration
     * is the overriden by the custom create
     */
    private class HibernateTestCustomCreateOverridingCurrentConfiguration {

        @HibernateSessionFactory("org/unitils/hibernate/hibernate.cfg.xml")
        protected Configuration createMethod1(List<String> locations) {
            assertEquals(1, locations.size());
            assertEquals("org/unitils/hibernate/hibernate.cfg.xml", locations.get(0));

            Configuration configuration = new AnnotationConfiguration();
            configuration.configure();
            return configuration;
        }
    }

    /**
     * Configuration with custom create with wrong signature.
     */
    public class HibernateTestCustomCreateWrongSignature {

        @HibernateSessionFactory
        public List createMethod(String a) {
            return null;
        }
    }

    /**
     * Class level configuration a wrong location specified.
     */
    @HibernateSessionFactory("xxxxxxx")
    public class HibernateTestWrongLocation {
    }


}
