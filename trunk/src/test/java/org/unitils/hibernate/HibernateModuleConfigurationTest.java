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
import org.unitils.core.UnitilsException;
import org.unitils.hibernate.annotation.HibernateSessionFactory;

import java.util.List;
import java.util.Properties;

/**
 * Test class for the loading of the configuration in the HibernateModule
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateModuleConfigurationTest extends UnitilsJUnit3 {

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
     * Tests loading of a configuration location specified on class-level.
     */
    public void testGetHibernateConfiguration_classLevel() {
        HibernateTestClassLevel hibernateTestClassLevel = new HibernateTestClassLevel();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(hibernateTestClassLevel);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration annotation on class-level but no location. An exception should be raised.
     */
    public void testGetHibernateConfiguration_classLevelNoLocation() {
        HibernateTestClassLevelNoLocation hibernateTestClassLevelNoLocation = new HibernateTestClassLevelNoLocation();
        try {
            hibernateModule.getHibernateConfiguration(hibernateTestClassLevelNoLocation);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests loading of a configuration location specified on field-level.
     */
    public void testGetHibernateConfiguration_fieldLevel() {
        HibernateTestFieldLevel hibernateTestFieldLevel = new HibernateTestFieldLevel();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(hibernateTestFieldLevel);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration annotation on field-level but no location. An exception should be raised.
     */
    public void testGetHibernateConfiguration_fieldLevelNoLocation() {
        HibernateTestFieldLevelNoLocation hibernateTestFieldLevelNoLocation = new HibernateTestFieldLevelNoLocation();
        try {
            hibernateModule.getHibernateConfiguration(hibernateTestFieldLevelNoLocation);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests loading of a configuration location specified on field-level.
     */
    public void testGetHibernateConfiguration_setterLevel() {
        HibernateTestSetterLevel hibernateTestSetterLevel = new HibernateTestSetterLevel();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(hibernateTestSetterLevel);

        assertNotNull(hibernateConfiguration);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration annotation on field-level but no location. An exception should be raised.
     */
    public void testGetHibernateConfiguration_setterLevelNoLocation() {
        HibernateTestSetterLevelNoLocation hibernateTestSetterLevelNoLocation = new HibernateTestSetterLevelNoLocation();
        try {
            hibernateModule.getHibernateConfiguration(hibernateTestSetterLevelNoLocation);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests for more than 1 annotation with values. An exception should have been raised.
     */
    public void testGetApplicationContext_multipleAnnotationsWithValues() {
        HibernateTestMultipleAnnotationsWithValues hibernateTestMultipleAnnotationsWithValues = new HibernateTestMultipleAnnotationsWithValues();
        try {
            hibernateModule.getHibernateConfiguration(hibernateTestMultipleAnnotationsWithValues);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests loading of a configuration annotation through a custom create method
     */
    public void testGetHibernateConfiguration_customCreateMethod() {
        HibernateTestCustomCreate hibernateTestCustomCreate = new HibernateTestCustomCreate();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(hibernateTestCustomCreate);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration annotation through a custom create method passing the
     * locations as argument.
     */
    public void testGetHibernateConfiguration_customCreateMethodWithLocationsArgument() {
        HibernateTestCustomCreateWithLocations hibernateTestCustomCreateWithConfiguration = new HibernateTestCustomCreateWithLocations();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(hibernateTestCustomCreateWithConfiguration);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration through a method-level specified location and then
     * overriding this configuration in a custom create method.
     */
    public void testGetHibernateConfiguration_customCreateMethodOverridingConfiguration() {
        HibernateTestCustomCreateOverridingCurrentConfiguration testCustomCreateOverridingCurrentConfiguration = new HibernateTestCustomCreateOverridingCurrentConfiguration();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(testCustomCreateOverridingCurrentConfiguration);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
    }


    /**
     * Tests loading of a configuration using a custom create method with a wrong signature.
     */
    public void testGetHibernateConfiguration_customCreateWrongSignature() {
        HibernateTestCustomCreateWrongSignature hibernateTestCustomCreateWrongSignature = new HibernateTestCustomCreateWrongSignature();
        try {
            hibernateModule.getHibernateConfiguration(hibernateTestCustomCreateWrongSignature);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests calling a custom initialization.
     */
    public void testGetHibernateConfiguration_customInitializationMethod() {
        HibernateTestCustomInitialization hibernateTestCustomInitialization = new HibernateTestCustomInitialization();
        hibernateModule.getHibernateConfiguration(hibernateTestCustomInitialization);

        assertTrue(hibernateTestCustomInitialization.initCalled);
    }


    /**
     * Tests calling a custom initialization having a wrong signature.
     */
    public void testGetHibernateConfiguration_customInitializationMethodWrongSignature() {
        HibernateTestCustomInitializationWrongSignature hibernateTestCustomInitializationWrongSignature = new HibernateTestCustomInitializationWrongSignature();
        try {
            hibernateModule.getHibernateConfiguration(hibernateTestCustomInitializationWrongSignature);
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
            hibernateModule.getHibernateConfiguration(hibernateTestWrongLocation);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Test reusing a configuration for the same class.
     */
    public void testGetHibernateConfiguration_twice() {
        Configuration hibernateConfiguration1 = hibernateModule.getHibernateConfiguration(new HibernateTestClassLevel());
        Configuration hibernateConfiguration2 = hibernateModule.getHibernateConfiguration(new HibernateTestClassLevel());

        assertNotNull(hibernateConfiguration1);
        assertEquals("org/unitils/hibernate/hibernate.cfg.xml", hibernateConfiguration1.getProperty("name"));
        assertSame(hibernateConfiguration1, hibernateConfiguration2);
    }


    /**
     * Test invalidating a cached configuration.
     */
    public void testInvalidateHibernateConfiguration() {
        Configuration hibernateConfiguration1 = hibernateModule.getHibernateConfiguration(new HibernateTestClassLevel());
        hibernateModule.invalidateConfiguration();
        Configuration hibernateConfiguration2 = hibernateModule.getHibernateConfiguration(new HibernateTestClassLevel());

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
        Configuration hibernateConfiguration1 = hibernateModule.getHibernateConfiguration(new HibernateTestClassLevel());
        hibernateModule.invalidateConfiguration(HibernateTestClassLevel.class);
        Configuration hibernateConfiguration2 = hibernateModule.getHibernateConfiguration(new HibernateTestClassLevel());

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
        Configuration hibernateConfiguration1 = hibernateModule.getHibernateConfiguration(new HibernateTestClassLevel());
        hibernateModule.invalidateConfiguration(String.class, List.class);
        Configuration hibernateConfiguration2 = hibernateModule.getHibernateConfiguration(new HibernateTestClassLevel());

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
     * Test SpringTest class mixing multiple annotations.
     */
    @HibernateSessionFactory({"1"})
    private class HibernateTestMultipleAnnotationsWithValues {

        @HibernateSessionFactory({"2"})
        protected SessionFactory field1;

        @HibernateSessionFactory({"3"})
        protected SessionFactory field2;
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
     * Configuration with custom create with locations argument.
     */
    public class HibernateTestCustomCreateWithLocations {

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
        public List<?> createMethod(String a) {
            return null;
        }
    }

    /**
     * Configuration with custom initialization with configuration argument.
     */
    @HibernateSessionFactory("org/unitils/hibernate/hibernate.cfg.xml")
    public class HibernateTestCustomInitialization {

        public boolean initCalled = false;

        @HibernateSessionFactory
        public void initializationMethod(Configuration configuration) {
            initCalled = true;
        }
    }


    /**
     * Configuration with custom initialization with wrong signature.
     */
    @HibernateSessionFactory("org/unitils/hibernate/hibernate.cfg.xml")
    public class HibernateTestCustomInitializationWrongSignature {

        @HibernateSessionFactory
        public List<?> initializationMethod(String a) {
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
