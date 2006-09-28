package org.unitils.module;

import static org.unitils.module.UnitilsModulesLoader.*;
import org.unitils.util.UnitilsConfiguration;
import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.List;

/**
 * Test for {@link UnitilsModulesLoader}.
 */
public class UnitilsModulesLoaderTest extends TestCase {


    /* Class under test */
    private UnitilsModulesLoader unitilsModulesLoader;

    /* The unitils configuration settings that control the module loading */
    private Configuration configuration;


    /**
     * Creates the test instance and initializes the fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        unitilsModulesLoader = new UnitilsModulesLoader();

        configuration = new PropertiesConfiguration();
        configuration.setProperty(PROPERTY_MODULES, "a, b, c, d");
        configuration.setProperty(PROPERTY_MODULE_PREFIX + "a" + PROPERTY_MODULE_SUFFIX_CLASS_NAME, TestUnitilsModuleA.class.getName());
        configuration.setProperty(PROPERTY_MODULE_PREFIX + "a" + PROPERTY_MODULE_SUFFIX_RUN_AFTER, "b, d");
        configuration.setProperty(PROPERTY_MODULE_PREFIX + "b" + PROPERTY_MODULE_SUFFIX_CLASS_NAME, TestUnitilsModuleB.class.getName());
        configuration.setProperty(PROPERTY_MODULE_PREFIX + "b" + PROPERTY_MODULE_SUFFIX_RUN_AFTER, "d");
        configuration.setProperty(PROPERTY_MODULE_PREFIX + "c" + PROPERTY_MODULE_SUFFIX_CLASS_NAME, TestUnitilsModuleC.class.getName());
        configuration.setProperty(PROPERTY_MODULE_PREFIX + "c" + PROPERTY_MODULE_SUFFIX_RUN_AFTER, "a");
        configuration.setProperty(PROPERTY_MODULE_PREFIX + "d" + PROPERTY_MODULE_SUFFIX_CLASS_NAME, TestUnitilsModuleD.class.getName());

        UnitilsConfiguration.setInstance(configuration);
    }


    /**
     * Test the loading of a normal configuration.
     */
    public void testLoadModules() {

        List<UnitilsModule> result = unitilsModulesLoader.loadModules();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.get(0) instanceof TestUnitilsModuleD);
        assertTrue(result.get(1) instanceof TestUnitilsModuleB);
        assertTrue(result.get(2) instanceof TestUnitilsModuleA);
        assertTrue(result.get(3) instanceof TestUnitilsModuleC);
    }


    /**
     * Tests the loading with 1 module name left out: c.
     * The c module should not have been loaded.
     */
    public void testLoadModules_notActive() {

        configuration.setProperty(PROPERTY_MODULES, "a, b, d");

        List<UnitilsModule> result = unitilsModulesLoader.loadModules();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.get(0) instanceof TestUnitilsModuleD);
        assertTrue(result.get(1) instanceof TestUnitilsModuleB);
        assertTrue(result.get(2) instanceof TestUnitilsModuleA);
    }


    /**
     * Tests the loading with modules (a, b) and dependencies (a -> b, d) that are declared twice.
     * The doubles should have been ignored
     */
    public void testLoadModules_notDoubles() {

        configuration.setProperty(PROPERTY_MODULES, "a, b, c, d, a, b");
        configuration.setProperty(PROPERTY_MODULE_PREFIX + "a" + PROPERTY_MODULE_SUFFIX_RUN_AFTER, "b, b, d, b, d");

        List<UnitilsModule> result = unitilsModulesLoader.loadModules();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.get(0) instanceof TestUnitilsModuleD);
        assertTrue(result.get(1) instanceof TestUnitilsModuleB);
        assertTrue(result.get(2) instanceof TestUnitilsModuleA);
        assertTrue(result.get(3) instanceof TestUnitilsModuleC);
    }


    /**
     * Tests the loading with a totally empty configuration.
     */
    public void testLoadModules_emptyConfiguration() {

        configuration.clear();

        List<UnitilsModule> result = unitilsModulesLoader.loadModules();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    /**
     * Tests the loading of a module that is configured with a class name for a class that is not a UnitilsModule.
     * A runtime exception should have been thrown.
     */
    public void testLoadModules_wrongClassName() {

        configuration.setProperty(PROPERTY_MODULE_PREFIX + "a" + PROPERTY_MODULE_SUFFIX_CLASS_NAME, "java.lang.String");

        try {
            unitilsModulesLoader.loadModules();
            fail();

        } catch (RuntimeException e) {
            //expected
        }
    }


    /**
     * Tests the loading of modules that contain a circular dependency. a must run after b must run after d must run after a
     * A runtime exception should have been thrown.
     */
    public void testLoadModules_circular() {

        configuration.setProperty(PROPERTY_MODULE_PREFIX + "b" + PROPERTY_MODULE_SUFFIX_RUN_AFTER, "c");

        try {
            unitilsModulesLoader.loadModules();
            fail();

        } catch (RuntimeException e) {
            //expected
        }
    }


    /**
     * Tests the loading of a module that is configured with a class name for a class that has a private constructor.
     * A runtime exception should have been thrown.
     */
    public void testLoadModules_privateConstructor() {

        configuration.setProperty(PROPERTY_MODULE_PREFIX + "a" + PROPERTY_MODULE_SUFFIX_CLASS_NAME, TestUnitilsModulePrivate.class.getName());

        try {
            unitilsModulesLoader.loadModules();
            fail();

        } catch (RuntimeException e) {
            //expected
        }
    }


    /**
     * A test unitils module type
     */
    public static class TestUnitilsModuleA implements UnitilsModule {

        public TestListener createTestListener() {
            return null;
        }
    }

    /**
     * A test unitils module type
     */
    public static class TestUnitilsModuleB implements UnitilsModule {

        public TestListener createTestListener() {
            return null;
        }
    }

    /**
     * A test unitils module type
     */
    public static class TestUnitilsModuleC implements UnitilsModule {

        public TestListener createTestListener() {
            return null;
        }
    }

    /**
     * A test unitils module type
     */
    public static class TestUnitilsModuleD implements UnitilsModule {

        public TestListener createTestListener() {
            return null;
        }
    }


    /**
     * A test unitils module type having a private constructor
     */
    public static class TestUnitilsModulePrivate implements UnitilsModule {

        private TestUnitilsModulePrivate() {
        }

        public TestListener createTestListener() {
            return null;
        }
    }
}
