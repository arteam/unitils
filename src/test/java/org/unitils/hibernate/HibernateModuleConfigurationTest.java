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


import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.hibernate.annotation.HibernateConfiguration;

import java.util.List;

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

        org.apache.commons.configuration.Configuration configuration = new ConfigurationLoader().loadConfiguration();
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
     * Tests loading of a configuration annotation on class-level but no location (default should be loaded).
     */
    public void testGetHibernateConfiguration_classLevelNoLocation() {
        HibernateTestClassLevelNoLocation hibernateTestClassLevelNoLocation = new HibernateTestClassLevelNoLocation();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(hibernateTestClassLevelNoLocation);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
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
     * Tests loading of a configuration annotation on field-level but no location (default should be loaded).
     */
    public void testGetHibernateConfiguration_fieldLevelNoLocation() {
        HibernateTestFieldLevelNoLocation hibernateTestFieldLevelNoLocation = new HibernateTestFieldLevelNoLocation();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(hibernateTestFieldLevelNoLocation);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
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
     * Tests loading of a configuration annotation on field-level but no location (default should be loaded).
     */
    public void testGetHibernateConfiguration_setterLevelNoLocation() {
        HibernateTestSetterLevelNoLocation hibernateTestSetterLevelNoLocation = new HibernateTestSetterLevelNoLocation();
        Configuration hibernateConfiguration = hibernateModule.getHibernateConfiguration(hibernateTestSetterLevelNoLocation);

        assertNotNull(hibernateConfiguration);
        assertEquals("resources/hibernate.cfg.xml", hibernateConfiguration.getProperty("name"));
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
     * Tests loading of a configuration annotation through a custom create method passing the current
     * configuration as argument.
     */
    public void testGetHibernateConfiguration_customCreateMethodWithConfigurationArgument() {
        HibernateTestCustomCreateWithConfiguration hibernateTestCustomCreateWithConfiguration = new HibernateTestCustomCreateWithConfiguration();
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
     * Class level configuration.
     */
    @HibernateConfiguration("org/unitils/hibernate/hibernate.cfg.xml")
    public class HibernateTestClassLevel {
    }

    /**
     * Class level configuration no location specified.
     */
    @HibernateConfiguration
    public class HibernateTestClassLevelNoLocation {
    }

    /**
     * Field level configuration.
     */
    public class HibernateTestFieldLevel {

        @HibernateConfiguration("org/unitils/hibernate/hibernate.cfg.xml")
        protected Configuration field;
    }

    /**
     * Field level configuration no location specified.
     */
    public class HibernateTestFieldLevelNoLocation {

        @HibernateConfiguration
        protected Configuration field;
    }

    /**
     * Setter level configuration.
     */
    public class HibernateTestSetterLevel {

        @HibernateConfiguration("org/unitils/hibernate/hibernate.cfg.xml")
        public void setField(Configuration configuration) {
        }
    }

    /**
     * Setter level configuration no location specified.
     */
    public class HibernateTestSetterLevelNoLocation {

        @HibernateConfiguration
        public void setField(Configuration configuration) {
        }
    }

    /**
     * Configuration with custom create.
     */
    public class HibernateTestCustomCreate {

        @HibernateConfiguration
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

        @HibernateConfiguration
        public Configuration createMethod(Configuration configuration) {
            assertNull(configuration);
            configuration = new AnnotationConfiguration();
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

        @HibernateConfiguration("org/unitils/hibernate/hibernate.cfg.xml")
        protected Configuration createMethod1(Configuration configuration) {
            assertNotNull(configuration);
            assertEquals("org/unitils/hibernate/hibernate.cfg.xml", configuration.getProperty("name"));

            configuration = new AnnotationConfiguration();
            configuration.configure();
            return configuration;
        }
    }

    /**
     * Configuration with custom create with wrong signature.
     */
    public class HibernateTestCustomCreateWrongSignature {

        @HibernateConfiguration
        public List createMethod(String a) {
            return null;
        }
    }

    /**
     * Class level configuration a wrong location specified.
     */
    @HibernateConfiguration("xxxxxxx")
    public class HibernateTestWrongLocation {
    }


}
